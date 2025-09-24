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
import android.os.Build
import androidx.lifecycle.ProcessLifecycleOwner
import io.getstream.android.core.api.StreamClient
import io.getstream.android.core.api.authentication.StreamTokenManager
import io.getstream.android.core.api.authentication.StreamTokenProvider
import io.getstream.android.core.api.log.StreamLoggerProvider
import io.getstream.android.core.api.model.config.StreamClientSerializationConfig
import io.getstream.android.core.api.model.config.StreamHttpConfig
import io.getstream.android.core.api.model.exceptions.StreamClientException
import io.getstream.android.core.api.model.value.StreamApiKey
import io.getstream.android.core.api.model.value.StreamHttpClientInfoHeader
import io.getstream.android.core.api.model.value.StreamUserId
import io.getstream.android.core.api.model.value.StreamWsUrl
import io.getstream.android.core.api.processing.StreamBatcher
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.android.core.api.processing.StreamSerialProcessingQueue
import io.getstream.android.core.api.processing.StreamSingleFlightProcessor
import io.getstream.android.core.api.serialization.StreamEventSerialization
import io.getstream.android.core.api.socket.StreamConnectionIdHolder
import io.getstream.android.core.api.socket.StreamWebSocketFactory
import io.getstream.android.core.api.socket.listeners.StreamClientListener
import io.getstream.android.core.api.socket.monitor.StreamHealthMonitor
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.android.core.network.NetworkStateProvider
import io.getstream.feeds.android.client.BuildConfig
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.model.FeedsConfig
import io.getstream.feeds.android.client.api.model.User
import io.getstream.feeds.android.client.internal.client.reconnect.ConnectionRecoveryHandler
import io.getstream.feeds.android.client.internal.client.reconnect.DefaultRetryStrategy
import io.getstream.feeds.android.client.internal.client.reconnect.FeedWatchHandler
import io.getstream.feeds.android.client.internal.client.reconnect.lifecycle.StreamLifecycleObserver
import io.getstream.feeds.android.client.internal.file.StreamFeedUploader
import io.getstream.feeds.android.client.internal.http.FeedsSingleFlightApi
import io.getstream.feeds.android.client.internal.logging.createLoggerProvider
import io.getstream.feeds.android.client.internal.logging.createLoggingInterceptor
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.AppRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.BookmarksRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.CommentsRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.DevicesRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.FeedsRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.FilesRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.ModerationRepositoryImpl
import io.getstream.feeds.android.client.internal.repository.PollsRepositoryImpl
import io.getstream.feeds.android.client.internal.serialization.FeedsMoshiJsonParser
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.infrastructure.Serializer
import io.getstream.feeds.android.network.models.WSEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.plus
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

/** Creates a [StreamClient] instance with the given configuration and dependencies. */
internal fun createStreamCoreClient(
    scope: CoroutineScope,
    apiKey: StreamApiKey,
    userId: StreamUserId,
    wsUrl: StreamWsUrl,
    clientInfoHeader: StreamHttpClientInfoHeader,
    tokenProvider: StreamTokenProvider,
    okHttpClient: OkHttpClient.Builder,
    feedsMoshiJsonParser: FeedsMoshiJsonParser,
    logProvider: StreamLoggerProvider,
): StreamClient {
    val clientSubscriptionManager =
        StreamSubscriptionManager<StreamClientListener>(
            logger = logProvider.taggedLogger("SCClientSubscriptions"),
            maxStrongSubscriptions = 250,
            maxWeakSubscriptions = 250,
        )
    val singleFlight = StreamSingleFlightProcessor(scope)
    val tokenManager = StreamTokenManager(userId, tokenProvider, singleFlight)
    val serialQueue =
        StreamSerialProcessingQueue(
            logger = logProvider.taggedLogger("SCSerialProcessing"),
            scope = scope,
        )
    val retryProcessor = StreamRetryProcessor(logger = logProvider.taggedLogger("SCRetryProcessor"))
    val connectionIdHolder = StreamConnectionIdHolder()
    val socketFactory =
        StreamWebSocketFactory(logger = logProvider.taggedLogger("SCWebSocketFactory"))
    val healthMonitor =
        StreamHealthMonitor(logger = logProvider.taggedLogger("SCHealthMonitor"), scope = scope)
    val batcher =
        StreamBatcher<String>(
            scope = scope,
            batchSize = 10,
            initialDelayMs = 100L,
            maxDelayMs = 1_000L,
        )

    return StreamClient(
        scope = scope,
        apiKey = apiKey,
        userId = userId,
        wsUrl = wsUrl,
        products = listOf("feeds"),
        clientInfoHeader = clientInfoHeader,
        tokenProvider = tokenProvider,
        logProvider = logProvider,
        clientSubscriptionManager = clientSubscriptionManager,
        tokenManager = tokenManager,
        singleFlight = singleFlight,
        serialQueue = serialQueue,
        retryProcessor = retryProcessor,
        connectionIdHolder = connectionIdHolder,
        socketFactory = socketFactory,
        healthMonitor = healthMonitor,
        httpConfig = StreamHttpConfig(httpBuilder = okHttpClient, automaticInterceptors = true),
        serializationConfig =
            StreamClientSerializationConfig.default(
                object : StreamEventSerialization<WSEvent> {
                    override fun serialize(data: WSEvent): Result<String> =
                        feedsMoshiJsonParser.toJson(data)

                    override fun deserialize(raw: String): Result<WSEvent> =
                        feedsMoshiJsonParser.fromJson(raw, WSEvent::class.java)
                }
            ),
        batcher = batcher,
    )
}

/** Creates a [FeedsClient] instance with the given configuration and dependencies. */
internal fun createFeedsClient(
    context: Context,
    apiKey: StreamApiKey,
    user: User,
    tokenProvider: StreamTokenProvider,
    config: FeedsConfig,
): FeedsClient {

    val logProvider = createLoggerProvider(config.loggingConfig.customLogger)
    val logger = logProvider.taggedLogger("FeedsClient")

    // Setup coroutine scope for the client
    val clientScope =
        CoroutineScope(Dispatchers.Default) +
            SupervisorJob() +
            CoroutineExceptionHandler { coroutineContext, throwable ->
                logger.e(throwable) {
                    "[clientScope] Uncaught exception in coroutine $coroutineContext: $throwable"
                }
            }

    // Processing
    val singleFlight = StreamSingleFlightProcessor(clientScope)

    // UserID
    val userId = StreamUserId.fromString(user.id)

    // Setup network
    val endpointConfig = EndpointConfig.PRODUCTION // TODO: Make this configurable
    val clientInfoHeader =
        StreamHttpClientInfoHeader.create(
            product = BuildConfig.PRODUCT_NAME,
            productVersion = BuildConfig.PRODUCT_VERSION,
            os = "Android",
            apiLevel = Build.VERSION.SDK_INT,
            deviceModel = Build.MODEL,
        )
    // HTTP Configuration
    val okHttpBuilder =
        OkHttpClient.Builder()
            .addInterceptor(
                createLoggingInterceptor(logProvider, config.loggingConfig.httpLoggingLevel)
            )

    val client =
        createStreamCoreClient(
            clientScope,
            apiKey,
            userId,
            StreamWsUrl.fromString(endpointConfig.wsUrl),
            clientInfoHeader,
            tokenProvider,
            okHttpBuilder,
            FeedsMoshiJsonParser(Serializer.moshi),
            logProvider,
        )
    val connectionRecoveryHandler =
        ConnectionRecoveryHandler(
            scope = clientScope,
            client = client,
            lifecycleObserver =
                StreamLifecycleObserver(
                    scope = clientScope,
                    lifecycle = ProcessLifecycleOwner.Companion.get().lifecycle,
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
            logger = logProvider.taggedLogger("ConnectionRecoveryHandler"),
        )
    val okHttpClient = okHttpBuilder.build()
    val retrofit =
        Retrofit.Builder()
            .baseUrl(endpointConfig.httpUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(Serializer.moshi))
            .build()
    val feedsApi: FeedsApi = FeedsSingleFlightApi(retrofit.create(), singleFlight)
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
    val errorBus = MutableSharedFlow<StreamClientException>(extraBufferCapacity = 100)

    val feedWatchHandler =
        FeedWatchHandler(
            connectionState = client.connectionState,
            feedsRepository = feedsRepository,
            retryProcessor = StreamRetryProcessor(logProvider.taggedLogger("WatchHandler")),
            errorBus = errorBus,
            scope = clientScope,
        )

    // Build client
    return FeedsClientImpl(
        apiKey = apiKey,
        user = user,
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
        coreClient = client,
        feedsEventsSubscriptionManager =
            StreamSubscriptionManager(
                logProvider.taggedLogger("FeedEventSubscriptions"),
                maxStrongSubscriptions = Integer.MAX_VALUE,
                maxWeakSubscriptions = Integer.MAX_VALUE,
            ),
        stateEventsSubscriptionManager =
            StreamSubscriptionManager(
                logProvider.taggedLogger("StateEventSubscriptions"),
                maxStrongSubscriptions = Integer.MAX_VALUE,
                maxWeakSubscriptions = Integer.MAX_VALUE,
            ),
        feedWatchHandler = feedWatchHandler,
        errorBus = errorBus,
        scope = clientScope,
        logger = logger,
    )
}
