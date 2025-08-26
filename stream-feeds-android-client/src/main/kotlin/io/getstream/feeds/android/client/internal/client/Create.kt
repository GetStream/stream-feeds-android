package io.getstream.feeds.android.client.internal.client

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.lifecycle.ProcessLifecycleOwner
import io.getstream.android.core.api.StreamClient
import io.getstream.android.core.api.authentication.StreamTokenManager
import io.getstream.android.core.api.authentication.StreamTokenProvider
import io.getstream.android.core.api.log.StreamLogger
import io.getstream.android.core.api.log.StreamLoggerProvider
import io.getstream.android.core.api.model.config.StreamClientSerializationConfig
import io.getstream.android.core.api.model.config.StreamHttpConfig
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
import io.getstream.feeds.android.client.api.model.User
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.model.FeedsConfig
import io.getstream.feeds.android.client.internal.client.reconnect.lifecycle.StreamLifecycleObserver
import io.getstream.feeds.android.client.internal.file.StreamFeedUploader
import io.getstream.feeds.android.client.internal.log.provideLogger
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
import io.getstream.feeds.android.client.internal.client.reconnect.ConnectionRecoveryHandler
import io.getstream.feeds.android.client.internal.client.reconnect.DefaultRetryStrategy
import io.getstream.feeds.android.client.internal.http.FeedsSingleFlightApi
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.infrastructure.Serializer
import io.getstream.feeds.android.network.models.WSEvent
import io.getstream.log.AndroidStreamLogger
import io.getstream.log.StreamLog
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
import retrofit2.create

/**
 * Creates a [StreamClient] instance with the given configuration and dependencies.
 */
internal fun createStreamCoreClient(
    scope: CoroutineScope,
    apiKey: StreamApiKey,
    userId: StreamUserId,
    wsUrl: StreamWsUrl,
    clientInfoHeader: StreamHttpClientInfoHeader,
    tokenProvider: StreamTokenProvider,
    okHttpClient: OkHttpClient.Builder,
    feedsMoshiJsonParser: FeedsMoshiJsonParser,
    logProvider: StreamLoggerProvider? = null,
): StreamClient {
    val logProvider = logProvider ?: StreamLoggerProvider.Companion.defaultAndroidLogger(
        minLevel = StreamLogger.LogLevel.Verbose,
        honorAndroidIsLoggable = false,
    )
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
        httpConfig = StreamHttpConfig(
            httpBuilder = okHttpClient,
            automaticInterceptors = true,
        ),
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

/**
 * Creates a [FeedsClient] instance with the given configuration and dependencies.
 */
internal fun createFeedsClient(
    context: Context,
    apiKey: StreamApiKey,
    user: User,
    tokenProvider: StreamTokenProvider,
    config: FeedsConfig,
): FeedsClient {

    val logProvider = StreamLoggerProvider.Companion.defaultAndroidLogger(
        minLevel = StreamLogger.LogLevel.Verbose,
        honorAndroidIsLoggable = false,
    )
    // Setup logging
    if (!StreamLog.isInstalled) {
        // If no logger is installed, install the default logger
        StreamLog.setValidator { _, _ -> true } // TODO: Make the log level configurable
        StreamLog.install(AndroidStreamLogger())
    }
    val logger = provideLogger(tag = "Client")

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

    // Token management
    val tokenManager = StreamTokenManager(userId, tokenProvider, singleFlight)
    // Setup network
    val endpointConfig = EndpointConfig.PRODUCTION // TODO: Make this configurable
    val clientInfoHeader = StreamHttpClientInfoHeader.create(
        product = "stream-feeds-android",
        productVersion = "0.0.1",
        os = "Android",
        apiLevel = Build.VERSION.SDK_INT,
        deviceModel = Build.MODEL,
    )
    // HTTP Configuration


    val okHttpBuilder =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

    val client = createStreamCoreClient(
        clientScope,
        apiKey,
        userId,
        StreamWsUrl.fromString(endpointConfig.wsUrl),
        clientInfoHeader,
        tokenProvider,
        okHttpBuilder,
        FeedsMoshiJsonParser(Serializer.moshi),
    )
    val connectionRecoveryHandler = ConnectionRecoveryHandler(
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
        feedsEventsSubscriptionManager = StreamSubscriptionManager(logProvider.taggedLogger("FeedEventSubscriptions"),
            maxStrongSubscriptions = Integer.MAX_VALUE,
            maxWeakSubscriptions = Integer.MAX_VALUE,
        ),
        logger = logger,
    )
}