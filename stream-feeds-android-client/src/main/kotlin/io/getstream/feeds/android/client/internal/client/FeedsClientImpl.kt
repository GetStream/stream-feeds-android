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
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManagerImpl
import io.getstream.feeds.android.client.internal.log.provideLogger
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
import io.getstream.feeds.android.client.internal.state.FeedImpl
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
    val clientScope = CoroutineScope(Dispatchers.Default) + SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable ->
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
    // HTTP Configuration
    val authInterceptor = if (user.type == UserAuthType.ANONYMOUS) {
        AnonymousAuthInterceptor(token.rawValue)
    } else {
        // TODO: Implement these things properly
        TokenAuthInterceptor(
            token = { token.rawValue },
            connectionId = { "" }
        )
    }
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(apiKey))
        .addInterceptor(HeadersInterceptor(xStreamClient))
        .addInterceptor(authInterceptor)
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(endpointConfig.httpUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Serializer.moshi))
        .build()
    val feedsApi: ApiService = retrofit.create(ApiService::class.java)
    val feedsRepository = FeedsRepositoryImpl(feedsApi)

    // Build client
    return FeedsClientImpl(
        apiKey = apiKey,
        user = user,
        token = token,
        socket = socket,
        connectionRecoveryHandler = connectionRecoveryHandler,
        feedRepository = feedsRepository,
        logger = logger,
    )
}

internal class FeedsClientImpl(
    private val apiKey: ApiKey,
    private val user: User,
    private val token: UserToken,
    private val socket: FeedsSocket,
    private val connectionRecoveryHandler: ConnectionRecoveryHandler,
    private val feedRepository: FeedsRepository,
    private val logger: TaggedLogger = provideLogger(tag = "Client")
) : FeedsClient {

    private val socketListener: FeedsSocketListener = object : FeedsSocketListener {
        override fun onState(state: WebSocketConnectionState) {
            logger.d { "[onState] $state" }
            // TODO: Implementation
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
        feedsRepository = feedRepository,
    )
}
