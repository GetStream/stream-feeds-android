/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.feeds.android.client.internal.client

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ProcessLifecycleOwner
import io.getstream.android.core.http.XStreamClient
import io.getstream.android.core.http.interceptor.ApiKeyInterceptor
import io.getstream.android.core.http.interceptor.AuthInterceptor
import io.getstream.android.core.http.interceptor.ConnectionIdInterceptor
import io.getstream.android.core.http.interceptor.HeadersInterceptor
import io.getstream.android.core.lifecycle.StreamLifecycleObserver
import io.getstream.android.core.network.NetworkStateProvider
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.CacheableUserTokenProvider
import io.getstream.android.core.user.TokenManager
import io.getstream.android.core.user.TokenManagerImpl
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserAuthType
import io.getstream.android.core.user.UserTokenProvider
import io.getstream.android.core.websocket.DisconnectionSource
import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.BuildConfig
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.Moderation
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsConfig
import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityCommentList
import io.getstream.feeds.android.client.api.state.ActivityList
import io.getstream.feeds.android.client.api.state.ActivityReactionList
import io.getstream.feeds.android.client.api.state.BookmarkFolderList
import io.getstream.feeds.android.client.api.state.BookmarkList
import io.getstream.feeds.android.client.api.state.CommentList
import io.getstream.feeds.android.client.api.state.CommentReactionList
import io.getstream.feeds.android.client.api.state.CommentReplyList
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FollowList
import io.getstream.feeds.android.client.api.state.MemberList
import io.getstream.feeds.android.client.api.state.ModerationConfigList
import io.getstream.feeds.android.client.api.state.PollList
import io.getstream.feeds.android.client.api.state.PollVoteList
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManagerImpl
import io.getstream.feeds.android.client.internal.file.StreamFeedUploader
import io.getstream.feeds.android.client.internal.http.interceptor.ApiErrorInterceptor
import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.AppRepository
import io.getstream.feeds.android.client.internal.repository.AppRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.repository.BookmarksRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.DevicesRepository
import io.getstream.feeds.android.client.internal.repository.DevicesRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.FilesRepository
import io.getstream.feeds.android.client.internal.repository.FilesRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.ModerationRepository
import io.getstream.feeds.android.client.internal.repository.ModerationRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepositoryImpl
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
import io.getstream.feeds.android.client.internal.state.ActivityCommentListImpl
import io.getstream.feeds.android.client.internal.state.ActivityImpl
import io.getstream.feeds.android.client.internal.state.ActivityListImpl
import io.getstream.feeds.android.client.internal.state.ActivityReactionListImpl
import io.getstream.feeds.android.client.internal.state.BookmarkFolderListImpl
import io.getstream.feeds.android.client.internal.state.BookmarkListImpl
import io.getstream.feeds.android.client.internal.state.CommentListImpl
import io.getstream.feeds.android.client.internal.state.CommentReactionListImpl
import io.getstream.feeds.android.client.internal.state.CommentReplyListImpl
import io.getstream.feeds.android.client.internal.state.FeedImpl
import io.getstream.feeds.android.client.internal.state.FeedListImpl
import io.getstream.feeds.android.client.internal.state.FeedsClientStateImpl
import io.getstream.feeds.android.client.internal.state.FollowListImpl
import io.getstream.feeds.android.client.internal.state.MemberListImpl
import io.getstream.feeds.android.client.internal.state.ModerationConfigListImpl
import io.getstream.feeds.android.client.internal.state.PollListImpl
import io.getstream.feeds.android.client.internal.state.PollVoteListImpl
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.infrastructure.Serializer
import io.getstream.feeds.android.network.models.ActivityRequest
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesResponse
import io.getstream.feeds.android.network.models.ListDevicesResponse
import io.getstream.feeds.android.network.models.WSEvent
import io.getstream.feeds.android.network.models.WSEventAdapter
import io.getstream.log.AndroidStreamLogger
import io.getstream.log.StreamLog
import io.getstream.log.TaggedLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.plus
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

internal fun createFeedsClient(
    context: Context,
    apiKey: ApiKey,
    user: User,
    tokenProvider: UserTokenProvider,
    config: FeedsConfig,
): FeedsClient {
    // Setup logging
    if (!StreamLog.isInstalled) {
        // If no logger is installed, install the default logger
        StreamLog.setValidator { _, _ -> true } // TODO: Make the log level configurable
        StreamLog.install(AndroidStreamLogger())
    }
    val logger = provideLogger(tag = "Client")

    // Token management
    val cacheableTokenProvider = CacheableUserTokenProvider(tokenProvider)
    val tokenManager = TokenManagerImpl()
    tokenManager.setTokenProvider(cacheableTokenProvider)

    // Setup coroutine scope for the client
    val clientScope =
        CoroutineScope(Dispatchers.Default) +
            SupervisorJob() +
            CoroutineExceptionHandler { coroutineContext, throwable ->
                logger.e(throwable) {
                    "[clientScope] Uncaught exception in coroutine $coroutineContext: $throwable"
                }
            }
    // Setup network
    val endpointConfig = EndpointConfig.PRODUCTION // TODO: Make this configurable
    val xStreamClient =
        XStreamClient.create(
            context = context,
            product = BuildConfig.PRODUCT_NAME,
            productVersion = BuildConfig.PRODUCT_VERSION,
        )
    // Socket configuration
    val socket =
        FeedsSocket(
            config =
                FeedsSocketConfig(
                    socketConfig =
                        StreamSocketConfig(
                            url = endpointConfig.wsUrl,
                            apiKey = apiKey,
                            authType = "jwt",
                            xStreamClient = xStreamClient,
                        ),
                    connectUserData =
                        ConnectUserData(
                            userId = user.id,
                            token = tokenManager.getToken().rawValue,
                            name = user.name,
                            image = user.imageURL,
                            custom = user.customData,
                        ),
                ),
            healthMonitor = StreamHealthMonitor(scope = clientScope),
            internalSocket =
                StreamWebSocketImpl(
                    socketFactory = StreamWebSocketFactory(),
                    subscriptionManager = StreamSubscriptionManagerImpl(),
                ),
            jsonParser = MoshiJsonParser(Serializer.moshiBuilder.add(WSEventAdapter()).build()),
            debounceProcessor = DebounceProcessor(scope = clientScope),
            subscriptionManager = StreamSubscriptionManagerImpl(),
        )
    val connectionRecoveryHandler =
        ConnectionRecoveryHandler(
            scope = clientScope,
            socket = socket,
            lifecycleObserver =
                StreamLifecycleObserver(
                    scope = clientScope,
                    lifecycle = ProcessLifecycleOwner.get().lifecycle,
                ),
            networkStateProvider =
                NetworkStateProvider(
                    scope = clientScope,
                    connectivityManager =
                        context.getSystemService(Context.CONNECTIVITY_SERVICE)
                            as ConnectivityManager,
                ),
            keepConnectionAliveInBackground = false,
            reconnectStrategy = DefaultRetryStrategy(),
        )
    val clientState = FeedsClientStateImpl()
    // HTTP Configuration
    val jsonParser = MoshiJsonParser(Serializer.moshi)
    val authInterceptor =
        AuthInterceptor(
            tokenManager = tokenManager,
            jsonParser = jsonParser,
            authType = user.type.rawValue,
        )
    val connectionIdInterceptor = ConnectionIdInterceptor {
        val connectionState = clientState.connectionState
        if (connectionState !is WebSocketConnectionState.Connected) {
            logger.w { "[connectionId] Connection state is not connected: $connectionState" }
            return@ConnectionIdInterceptor ""
        }
        connectionState.connectionId
    }
    val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .addInterceptor(HeadersInterceptor(xStreamClient))
            .addInterceptor(ApiErrorInterceptor(jsonParser))
            .apply {
                if (user.type != UserAuthType.ANONYMOUS) {
                    addInterceptor(connectionIdInterceptor)
                }
            }
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    val retrofit =
        Retrofit.Builder()
            .baseUrl(endpointConfig.httpUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(Serializer.moshi))
            .build()
    val feedsApi: FeedsApi = retrofit.create()
    val uploader: FeedUploader = config.customUploader ?: StreamFeedUploader(retrofit.create())
    val activitiesRepository = ActivitiesRepositoryImpl(feedsApi, uploader)
    val appRepository = AppRepositoryImpl(feedsApi)
    val bookmarksRepository = BookmarksRepositoryImpl(feedsApi)
    val commentsRepository = CommentsRepositoryImpl(feedsApi, uploader)
    val devicesRepository = DevicesRepositoryImpl(feedsApi)
    val feedsRepository = FeedsRepositoryImpl(feedsApi)
    val filesRepository = FilesRepositoryImpl(feedsApi)
    val moderationRepository = ModerationRepositoryImpl(feedsApi)
    val pollsRepository = PollsRepositoryImpl(feedsApi)

    val moderation = ModerationImpl(moderationRepository)

    // Build client
    return FeedsClientImpl(
        apiKey = apiKey,
        user = user,
        tokenManager = tokenManager,
        socket = socket,
        connectionRecoveryHandler = connectionRecoveryHandler,
        activitiesRepository = activitiesRepository,
        appRepository = appRepository,
        bookmarksRepository = bookmarksRepository,
        commentsRepository = commentsRepository,
        devicesRepository = devicesRepository,
        feedsRepository = feedsRepository,
        filesRepository = filesRepository,
        moderationRepository = moderationRepository,
        pollsRepository = pollsRepository,
        uploader = uploader,
        moderation = moderation,
        clientState = clientState,
        logger = logger,
    )
}

internal class FeedsClientImpl(
    override val apiKey: ApiKey,
    override val user: User,
    private val tokenManager: TokenManager,
    private val socket: FeedsSocket,
    private val connectionRecoveryHandler: ConnectionRecoveryHandler,
    private val activitiesRepository: ActivitiesRepository,
    private val appRepository: AppRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val commentsRepository: CommentsRepository,
    private val devicesRepository: DevicesRepository,
    private val feedsRepository: FeedsRepository,
    private val filesRepository: FilesRepository,
    private val moderationRepository: ModerationRepository,
    private val pollsRepository: PollsRepository,
    override val uploader: FeedUploader,
    override val moderation: Moderation,
    private val clientState: FeedsClientStateImpl, // TODO: Expose
    private val logger: TaggedLogger = provideLogger(tag = "Client"),
) : FeedsClient {

    private val socketListener: FeedsSocketListener =
        object : FeedsSocketListener {
            override fun onState(state: WebSocketConnectionState) {
                logger.d { "[onState] $state" }
                clientState.setConnectionState(state)
            }

            override fun onEvent(event: WSEvent) {
                logger.d { "[onEvent] $event" }
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
        tokenManager.ensureTokenLoaded()
        val token = tokenManager.getToken().rawValue
        val connectUserData =
            ConnectUserData(
                userId = user.id,
                token = token,
                name = user.name,
                image = user.imageURL,
                custom = user.customData,
            )
        connectionRecoveryHandler.start()
        val connectionState = socket.connect(connectUserData)
        // TODO: Implement guards for multiple connections
        return when (connectionState) {
            is WebSocketConnectionState.Connected -> Result.success(Unit)
            else -> Result.failure(IllegalStateException("Failed to connect: $connectionState"))
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        connectionRecoveryHandler.stop()
        return socket.disconnect(DisconnectionSource.UserInitiated)
    }

    override fun events() = callbackFlow {
        val listener = object : FeedsSocketListener {
            override fun onState(state: WebSocketConnectionState) {}

            override fun onEvent(event: WSEvent) {
                trySend(event)
            }
        }
        val subscription = socket.subscribe(listener)

        awaitClose { subscription.getOrNull()?.cancel() }
    }

    override fun feed(group: String, id: String): Feed = feed(FeedId(group, id))

    override fun feed(fid: FeedId): Feed = feed(FeedQuery(fid))

    override fun feed(query: FeedQuery): Feed =
        FeedImpl(
            query = query,
            currentUserId = user.id,
            activitiesRepository = activitiesRepository,
            bookmarksRepository = bookmarksRepository,
            commentsRepository = commentsRepository,
            feedsRepository = feedsRepository,
            pollsRepository = pollsRepository,
            subscriptionManager = socket,
        )

    override fun feedList(query: FeedsQuery): FeedList =
        FeedListImpl(query = query, feedsRepository = feedsRepository, subscriptionManager = socket)

    override fun followList(query: FollowsQuery): FollowList =
        FollowListImpl(
            query = query,
            feedsRepository = feedsRepository,
            subscriptionManager = socket,
        )

    override fun activity(activityId: String, fid: FeedId): Activity =
        ActivityImpl(
            activityId = activityId,
            fid = fid,
            currentUserId = user.id,
            activitiesRepository = activitiesRepository,
            commentsRepository = commentsRepository,
            pollsRepository = pollsRepository,
            subscriptionManager = socket,
            commentList =
                ActivityCommentListImpl(
                    query =
                        ActivityCommentsQuery(
                            objectId = activityId,
                            objectType = "activity",
                            depth = 3,
                        ),
                    currentUserId = user.id,
                    commentsRepository = commentsRepository,
                    subscriptionManager = socket,
                ),
        )

    override fun activityList(query: ActivitiesQuery): ActivityList =
        ActivityListImpl(
            query = query,
            currentUserId = user.id,
            activitiesRepository = activitiesRepository,
            subscriptionManager = socket,
        )

    override fun activityReactionList(query: ActivityReactionsQuery): ActivityReactionList =
        ActivityReactionListImpl(
            query = query,
            activitiesRepository = activitiesRepository,
            subscriptionManager = socket,
        )

    override suspend fun addActivity(request: AddActivityRequest): Result<ActivityData> {
        return activitiesRepository.addActivity(request)
    }

    override suspend fun upsertActivities(
        activities: List<ActivityRequest>
    ): Result<List<ActivityData>> {
        return activitiesRepository.upsertActivities(activities)
    }

    override suspend fun deleteActivities(
        request: DeleteActivitiesRequest
    ): Result<DeleteActivitiesResponse> {
        return activitiesRepository.deleteActivities(request)
    }

    override fun bookmarkList(query: BookmarksQuery): BookmarkList =
        BookmarkListImpl(
            query = query,
            bookmarksRepository = bookmarksRepository,
            subscriptionManager = socket,
        )

    override fun bookmarkFolderList(query: BookmarkFoldersQuery): BookmarkFolderList =
        BookmarkFolderListImpl(
            query = query,
            bookmarksRepository = bookmarksRepository,
            subscriptionManager = socket,
        )

    override fun commentList(query: CommentsQuery): CommentList =
        CommentListImpl(
            query = query,
            commentsRepository = commentsRepository,
            subscriptionManager = socket,
        )

    override fun activityCommentList(query: ActivityCommentsQuery): ActivityCommentList =
        ActivityCommentListImpl(
            query = query,
            currentUserId = user.id,
            commentsRepository = commentsRepository,
            subscriptionManager = socket,
        )

    override fun commentReplyList(query: CommentRepliesQuery): CommentReplyList =
        CommentReplyListImpl(
            query = query,
            currentUserId = user.id,
            commentsRepository = commentsRepository,
            subscriptionManager = socket,
        )

    override fun commentReactionList(query: CommentReactionsQuery): CommentReactionList =
        CommentReactionListImpl(
            query = query,
            commentsRepository = commentsRepository,
            subscriptionManager = socket,
        )

    override fun memberList(query: MembersQuery): MemberList =
        MemberListImpl(
            query = query,
            feedsRepository = feedsRepository,
            subscriptionManager = socket,
        )

    override fun pollVoteList(query: PollVotesQuery): PollVoteList =
        PollVoteListImpl(query = query, repository = pollsRepository, subscriptionManager = socket)

    override fun pollList(query: PollsQuery): PollList =
        PollListImpl(query = query, pollsRepository = pollsRepository, subscriptionManager = socket)

    override fun moderationConfigList(query: ModerationConfigsQuery): ModerationConfigList =
        ModerationConfigListImpl(query = query, moderationRepository = moderationRepository)

    override suspend fun getApp(): Result<AppData> {
        return appRepository.getApp()
    }

    override suspend fun queryDevices(): Result<ListDevicesResponse> {
        return devicesRepository.queryDevices()
    }

    override suspend fun createDevice(
        id: String,
        pushProvider: PushNotificationsProvider,
        pushProviderName: String,
    ): Result<Unit> {
        return devicesRepository.createDevice(id, pushProvider, pushProviderName)
    }

    override suspend fun deleteDevice(id: String): Result<Unit> {
        return devicesRepository.deleteDevice(id)
    }

    override suspend fun deleteFile(url: String): Result<Unit> {
        return filesRepository.deleteFile(url)
    }

    override suspend fun deleteImage(url: String): Result<Unit> {
        return filesRepository.deleteImage(url)
    }
}
