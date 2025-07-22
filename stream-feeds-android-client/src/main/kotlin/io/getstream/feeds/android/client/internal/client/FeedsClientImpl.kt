package io.getstream.feeds.android.client.internal.client

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ProcessLifecycleOwner
import io.getstream.android.core.http.XStreamClient
import io.getstream.android.core.http.interceptor.AnonymousAuthInterceptor
import io.getstream.android.core.http.interceptor.ApiKeyInterceptor
import io.getstream.android.core.http.interceptor.HeadersInterceptor
import io.getstream.android.core.http.interceptor.TokenAuthInterceptor
import io.getstream.android.core.lifecycle.StreamLifecycleObserver
import io.getstream.android.core.network.NetworkStateProvider
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserAuthType
import io.getstream.android.core.user.UserToken
import io.getstream.android.core.websocket.DisconnectionSource
import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.BuildConfig
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityList
import io.getstream.feeds.android.client.api.state.BookmarkFolderList
import io.getstream.feeds.android.client.api.state.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.BookmarkList
import io.getstream.feeds.android.client.api.state.BookmarksQuery
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FeedQuery
import io.getstream.feeds.android.client.api.state.FeedsQuery
import io.getstream.feeds.android.client.api.state.FollowList
import io.getstream.feeds.android.client.api.state.FollowsQuery
import io.getstream.feeds.android.client.api.state.MemberList
import io.getstream.feeds.android.client.api.state.MembersQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManagerImpl
import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.repository.BookmarksRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepositoryImpl
import io.getstream.feeds.android.client.internal.socket.ConnectUserData
import io.getstream.feeds.android.client.internal.socket.FeedsSocket
import io.getstream.feeds.android.client.internal.socket.FeedsSocketConfig
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.socket.common.StreamWebSocketImpl
import io.getstream.feeds.android.client.internal.socket.common.debounce.DebounceProcessor
import io.getstream.feeds.android.client.internal.socket.common.factory.StreamSocketConfig
import io.getstream.feeds.android.client.internal.socket.common.factory.StreamWebSocketFactory
import io.getstream.feeds.android.client.internal.socket.common.monitor.StreamHealthMonitor
import io.getstream.feeds.android.client.internal.socket.common.parser.MoshiJsonParser
import io.getstream.feeds.android.client.internal.socket.common.reconnect.ConnectionRecoveryHandler
import io.getstream.feeds.android.client.internal.socket.common.reconnect.DefaultRetryStrategy
import io.getstream.feeds.android.client.internal.state.ActivityListImpl
import io.getstream.feeds.android.client.internal.state.BookmarkFolderListImpl
import io.getstream.feeds.android.client.internal.state.BookmarkListImpl
import io.getstream.feeds.android.client.internal.state.FeedImpl
import io.getstream.feeds.android.client.internal.state.FeedListImpl
import io.getstream.feeds.android.client.internal.state.FeedsClientStateImpl
import io.getstream.feeds.android.client.internal.state.FollowListImpl
import io.getstream.feeds.android.client.internal.state.MemberListImpl
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.infrastructure.Serializer
import io.getstream.feeds.android.core.generated.models.WSEvent
import io.getstream.feeds.android.core.generated.models.WSEventAdapter
import io.getstream.log.AndroidStreamLogger
import io.getstream.log.StreamLog
import io.getstream.log.TaggedLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

internal fun createFeedsClient(
    context: Context,
    apiKey: ApiKey,
    user: User,
    token: UserToken,
): FeedsClient {
    // Setup logging
    if (!StreamLog.isInstalled) {
        // If no logger is installed, install the default logger
        StreamLog.setValidator { _, _ -> true } // TODO: Make the log level configurable
        StreamLog.install(AndroidStreamLogger())
    }
    val logger = provideLogger(tag = "Client")
    // Setup coroutine scope for the client
    val clientScope =
        CoroutineScope(Dispatchers.Default) + SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable ->
            logger.e(throwable) { "[clientScope] Uncaught exception in coroutine $coroutineContext: $throwable" }
        }
    // Setup network
    val endpointConfig = EndpointConfig.STAGING // TODO: Make this configurable
    val xStreamClient = XStreamClient.create(
        context = context,
        product = BuildConfig.PRODUCT_NAME,
        productVersion = BuildConfig.PRODUCT_VERSION,
    )
    // Socket configuration
    val socket = FeedsSocket(
        config = FeedsSocketConfig(
            socketConfig = StreamSocketConfig(
                url = endpointConfig.wsUrl,
                apiKey = apiKey,
                authType = "jwt",
                xStreamClient = xStreamClient,
            ),
            connectUserData = ConnectUserData(
                userId = user.id,
                token = token.rawValue,
                name = user.name,
                image = user.imageURL,
                custom = user.customData,
            ),
        ),
        healthMonitor = StreamHealthMonitor(scope = clientScope),
        internalSocket = StreamWebSocketImpl(
            socketFactory = StreamWebSocketFactory(),
            subscriptionManager = StreamSubscriptionManagerImpl(),
        ),
        jsonParser = MoshiJsonParser(Serializer.moshiBuilder.add(WSEventAdapter()).build()),
        debounceProcessor = DebounceProcessor(scope = clientScope),
        subscriptionManager = StreamSubscriptionManagerImpl(),
    )
    val connectionRecoveryHandler = ConnectionRecoveryHandler(
        scope = clientScope,
        socket = socket,
        lifecycleObserver = StreamLifecycleObserver(
            scope = clientScope,
            lifecycle = ProcessLifecycleOwner.get().lifecycle,
        ),
        networkStateProvider = NetworkStateProvider(
            scope = clientScope,
            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
        ),
        keepConnectionAliveInBackground = false,
        reconnectStrategy = DefaultRetryStrategy(),
    )
    val clientState = FeedsClientStateImpl()
    // HTTP Configuration
    val authInterceptor = if (user.type == UserAuthType.ANONYMOUS) {
        AnonymousAuthInterceptor(token.rawValue)
    } else {
        // TODO: Implement these things properly
        TokenAuthInterceptor(
            token = { token.rawValue },
            connectionId = {
                val connectionState = clientState.connectionState
                if (connectionState !is WebSocketConnectionState.Connected) {
                    logger.w { "[connectionId] Connection state is not connected: $connectionState" }
                    return@TokenAuthInterceptor ""
                }
                connectionState.connectionId
            },
        )
    }
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(apiKey))
        .addInterceptor(HeadersInterceptor(xStreamClient))
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(endpointConfig.httpUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Serializer.moshi))
        .build()
    val feedsApi: ApiService = retrofit.create(ApiService::class.java)
    val activitiesRepository = ActivitiesRepositoryImpl(feedsApi)
    val bookmarksRepository = BookmarksRepositoryImpl(feedsApi)
    val commentsRepository = CommentsRepositoryImpl(feedsApi)
    val feedsRepository = FeedsRepositoryImpl(feedsApi)

    // Build client
    return FeedsClientImpl(
        apiKey = apiKey,
        user = user,
        token = token,
        socket = socket,
        connectionRecoveryHandler = connectionRecoveryHandler,
        activitiesRepository = activitiesRepository,
        bookmarksRepository = bookmarksRepository,
        commentsRepository = commentsRepository,
        feedsRepository = feedsRepository,
        clientState = clientState,
        logger = logger,
    )
}

internal class FeedsClientImpl(
    private val apiKey: ApiKey,
    private val user: User,
    private val token: UserToken,
    private val socket: FeedsSocket,
    private val connectionRecoveryHandler: ConnectionRecoveryHandler,
    private val activitiesRepository: ActivitiesRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val commentsRepository: CommentsRepository,
    private val feedsRepository: FeedsRepository,
    private val clientState: FeedsClientStateImpl,
    private val logger: TaggedLogger = provideLogger(tag = "Client")
) : FeedsClient {

    private val socketListener: FeedsSocketListener = object : FeedsSocketListener {
        override fun onState(state: WebSocketConnectionState) {
            logger.d { "[onState] $state" }
            clientState.setConnectionState(state)
        }

        override fun onEvent(event: WSEvent) {
            logger.d { "[onEvent] $event" }
            // TODO: Implementation
        }
    }

    init {
        socket.subscribe(socketListener)
    }

    override suspend fun connect(): Result<Unit> {
        if (user.type == UserAuthType.ANONYMOUS) {
            logger.e { "[connect] Attempting to connect an anonymous user, returning an error." }
            return Result.failure(IllegalArgumentException("Anonymous users cannot connect."))
        }
        val connectUserData = ConnectUserData(
            userId = user.id,
            token = token.rawValue,
            name = user.name,
            image = user.imageURL,
            custom = user.customData,
        )
        connectionRecoveryHandler.start()
        val connectionState = socket.connect(connectUserData)
        // TODO: Implement guards for multiple connections
        return when (connectionState) {
            is WebSocketConnectionState.Connected -> Result.success(Unit)
            else -> Result.failure(
                IllegalStateException("Failed to connect: $connectionState")
            )
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        connectionRecoveryHandler.stop()
        return socket.disconnect(DisconnectionSource.UserInitiated)
    }

    override fun feed(group: String, id: String): Feed = feed(FeedId(group, id))

    override fun feed(fid: FeedId): Feed = feed(FeedQuery(fid))

    override fun feed(query: FeedQuery): Feed = FeedImpl(
        query = query,
        currentUserId = user.id,
        feedsRepository = feedsRepository,
    )

    override fun feedList(query: FeedsQuery): FeedList = FeedListImpl(
        query = query,
        feedsRepository = feedsRepository,
        subscriptionManager = socket,
    )

    override fun followList(query: FollowsQuery): FollowList = FollowListImpl(
        query = query,
        feedsRepository = feedsRepository,
    )

    override fun activity(activityId: String, fid: FeedId): Activity {
        TODO("Not yet implemented")
    }

    override fun activityList(query: ActivitiesQuery): ActivityList = ActivityListImpl(
        query = query,
        currentUserId = user.id,
        activitiesRepository = activitiesRepository,
    )

    override fun bookmarkList(query: BookmarksQuery): BookmarkList = BookmarkListImpl(
        query = query,
        bookmarksRepository = bookmarksRepository,
    )

    override fun bookmarkFolderList(query: BookmarkFoldersQuery): BookmarkFolderList =
        BookmarkFolderListImpl(
            query = query,
            bookmarksRepository = bookmarksRepository,
        )

    override fun memberList(query: MembersQuery): MemberList = MemberListImpl(
        query = query,
        feedsRepository = feedsRepository,
    )
}
