package io.getstream.feeds.android.client.internal.socket

import io.getstream.android.core.error.APIError
import io.getstream.android.core.error.APIErrorContainer
import io.getstream.android.core.user.ConnectUserDetailsRequest
import io.getstream.android.core.user.WSAuthMessageRequest
import io.getstream.android.core.websocket.DisconnectionSource
import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.api.subscribe.StreamSubscriber
import io.getstream.feeds.android.client.api.subscribe.StreamSubscription
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.feeds.android.client.internal.socket.common.StreamWebSocket
import io.getstream.feeds.android.client.internal.socket.common.debounce.DebounceProcessor
import io.getstream.feeds.android.client.internal.socket.common.factory.StreamSocketConfig
import io.getstream.feeds.android.client.internal.socket.common.listeners.StreamWebSocketListener
import io.getstream.feeds.android.client.internal.socket.common.monitor.StreamHealthMonitor
import io.getstream.feeds.android.client.internal.socket.common.parser.FeedsEventParser
import io.getstream.feeds.android.client.internal.socket.common.parser.JsonParser
import io.getstream.feeds.android.client.internal.socket.events.ConnectedEvent
import io.getstream.feeds.android.client.internal.socket.events.ConnectionErrorEvent
import io.getstream.feeds.android.core.generated.models.HealthCheckEvent
import io.getstream.feeds.android.core.generated.models.WSEvent
import io.getstream.log.TaggedLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume


/**
 * Listener interface for Feeds socket events.
 *
 * This interface defines methods to handle socket state changes and events.
 * Implement this interface to receive updates about the socket connection state
 * and incoming events.
 */
internal interface FeedsSocketListener : StreamSubscriber {

    /**
     * Called when the socket connection state changes.
     *
     * @param state The new state of the WebSocket connection.
     */
    fun onState(state: WebSocketConnectionState)

    /**
     * Called when a new event is received from the socket.
     *
     * @param event The event received from the WebSocket.
     */
    fun onEvent(event: WSEvent)
}

/**
 * Represents the data used to connect a user to the Feeds client.
 *
 * @param userId The unique identifier for the user.
 * @param token The authentication token for the user.
 * @param name Optional name of the user.
 * @param image Optional image URL of the user.
 * @param invisible If true, the user will be invisible to others.
 * @param language Optional language preference of the user.
 */
internal data class ConnectUserData(
    val userId: String,
    val token: String,
    val name: String? = null,
    val image: String? = null,
    val invisible: Boolean = false,
    val language: String? = null,
    val custom: Map<String, Any?>? = null
)

/**
 * Represents a user connected to the Feeds client.
 *
 * @property createdAt The date and time when the user was created.
 * @property id The unique identifier for the user.
 * @property language The language preference of the user.
 * @property role The role of the user (e.g., admin, user).
 * @property updatedAt The date and time when the user was last updated.
 * @property blockedUserIds List of user IDs that this user has blocked.
 * @property teams List of team IDs that the user belongs to.
 * @property custom Custom data associated with the user.
 * @property deactivatedAt The date and time when the user was deactivated.
 * @property deletedAt The date and time when the user was deleted.
 * @property image Optional image URL of the user.
 * @property lastActive The date and time when the user was last active.
 * @property name Optional name of the user.
 */
internal data class StreamConnectedUser(
    val createdAt: org.threeten.bp.OffsetDateTime,
    val id: String,
    val language: String,
    val role: String,
    val updatedAt: org.threeten.bp.OffsetDateTime,
    val blockedUserIds: List<String>,
    val teams: List<String>,
    val custom: Map<String, Any?> = emptyMap(),
    val deactivatedAt: org.threeten.bp.OffsetDateTime? = null,
    val deletedAt: org.threeten.bp.OffsetDateTime? = null,
    val image: String? = null,
    val lastActive: org.threeten.bp.OffsetDateTime? = null,
    val name: String? = null,
)

/**
 * Configuration for the Feeds socket connection.
 *
 * @param socketConfig The configuration for the underlying WebSocket connection.
 * @param connectUserData The data used to connect the user to the Feeds client.
 */
internal data class FeedsSocketConfig(
    val socketConfig: StreamSocketConfig,
    var connectUserData: ConnectUserData,
)

/**
 * A websocket client for the Feeds service.
 * This class manages the connection to the Feeds service, its disconnections and handles incoming
 * events.
 *
 * It implements the [StreamSubscriptionManager] to manage subscriptions to socket events.
 *
 * @property logger The logger for logging events and errors.
 * @property config The configuration for the Feeds socket connection.
 * @property internalSocket The underlying WebSocket implementation used for communication.
 * @property jsonParser The JSON parser used to serialize and deserialize messages.
 * @property parser The parser used to decode and encode WebSocket messages.
 * @property healthMonitor The health monitor for the WebSocket connection.
 * @param debounceProcessor The processor for debouncing WebSocket events.
 * @param subscriptionManager The manager for handling subscriptions to socket events.
 */
internal class FeedsSocket(
    private val logger: TaggedLogger = provideLogger(tag = "FeedsSocket"),
    private var config: FeedsSocketConfig,
    private val internalSocket: StreamWebSocket<StreamWebSocketListener>,
    private val jsonParser: JsonParser,
    private val eventParser: FeedsEventParser = FeedsEventParser(jsonParser),
    private val healthMonitor: StreamHealthMonitor,
    private val debounceProcessor: DebounceProcessor<WSEvent>,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
) : StreamSubscriptionManager<FeedsSocketListener> by subscriptionManager {

    internal var connectionState: WebSocketConnectionState = WebSocketConnectionState.Initialized
        set(value) {
            field = value
            forEach { it.onState(value) }
        }

    private var messageSubscription: StreamSubscription? = null
    private var connectedEvent: ConnectedEvent? = null

    private val eventListener = object : StreamWebSocketListener {

        override fun onMessage(text: String) {
            logger.v { "[onMessage] Socket message: $text" }
            // Attempt to parse an event from the text message
            eventParser.decode(text)
                .onSuccess {
                    logger.d { "[onMessage] Received event: ${it.getWSEventType()}" }
                    debounceProcessor.onMessage(it)
                    if (it is ConnectionErrorEvent) {
                        connectionState = WebSocketConnectionState.Disconnecting(
                            DisconnectionSource.ServerInitiated(it.error)
                        )
                    }
                }
                .onFailure {
                    // Attempt to parse as APIError
                    jsonParser.fromJsonOrError(text, APIErrorContainer::class.java)
                        .onSuccess {
                            logger.e { "[onMessage] Received an error webSocket event: ${it.error}" }
                            connectionState = WebSocketConnectionState.Disconnecting(
                                DisconnectionSource.ServerInitiated(it.error)
                            )
                        }
                        .onFailure {
                            logger.i { "[onMessage] Failed to parse $text" }
                        }
                }
        }

        override fun onFailure(t: Throwable, response: Response?) {
            logger.e(t) { "[onFailure] Socket failure. ${t.message}" }
            val state = connectionState
            connectionState = if (state is WebSocketConnectionState.Disconnecting) {
                WebSocketConnectionState.Disconnected(state.source)
            } else {
                WebSocketConnectionState.Disconnected(DisconnectionSource.SystemInitiated)
            }
            cleanup(t)
        }

        override fun onClosed(code: Int, reason: String) {
            val error = if (code == StreamWebSocket.CLOSE_SOCKET_CODE) {
                null
            } else {
                IOException("Socket closed. Code: $code, Reason: $reason")
            }
            val state = connectionState
            connectionState = if (state is WebSocketConnectionState.Disconnecting) {
                WebSocketConnectionState.Disconnected(state.source)
            } else {
                WebSocketConnectionState.Disconnected(DisconnectionSource.SystemInitiated)
            }
            cleanup(error)
            logger.e { "[onClosed] Socket closed. Code: $code, Reason: $reason" }
        }
    }

    // region: API
    /**
     * Disconnect the socket.
     *
     * @param error Optional error that caused the disconnection.
     */
    fun disconnect(source: DisconnectionSource = DisconnectionSource.UserInitiated): Result<Unit> {
        connectionState = WebSocketConnectionState.Disconnecting(source)
        return internalSocket.close()
    }

    /**
     * Connects the user to the Feeds client.
     *
     * @return Result of the connection operation.
     */
    suspend fun connect(data: ConnectUserData = config.connectUserData): WebSocketConnectionState =
        suspendCancellableCoroutine { continuation ->
            logger.d { "[connect] Connecting to socket: $data" }

            if (connectionState is WebSocketConnectionState.Connected) {
                logger.d { "[connect] Already connected, returning current state." }
                continuation.resume(connectionState)
                return@suspendCancellableCoroutine
            }

            // Update config with connection data
            config.connectUserData = config.connectUserData.copy(token = data.token)

            // Do intiialization work
            init()

            var subscription: StreamSubscription? = null

            // Steps
            val success: (StreamConnectedUser, String) -> Unit = { user, connectionId ->
                subscription?.cancel()
                if (continuation.isActive && !continuation.isCompleted) {
                    connectionState = WebSocketConnectionState.Connected(connectionId)
                    healthMonitor.start()
                    continuation.resume(WebSocketConnectionState.Connected(connectionId))
                }
            }
            val failure: (Throwable) -> Unit = { throwable ->
                subscription?.cancel()
                if (continuation.isActive && !continuation.isCompleted) {
                    connectionState =
                        WebSocketConnectionState.Disconnected(DisconnectionSource.SystemInitiated)
                    continuation.resume(WebSocketConnectionState.Disconnected(DisconnectionSource.SystemInitiated))
                }
            }
            val apiFailure: (APIError) -> Unit = { apiError ->
                subscription?.cancel()
                if (continuation.isActive && !continuation.isCompleted) {
                    connectionState = WebSocketConnectionState.Disconnected(
                        DisconnectionSource.ServerInitiated(apiError)
                    )
                    continuation.resume(
                        WebSocketConnectionState.Disconnected(
                            DisconnectionSource.ServerInitiated(
                                apiError
                            )
                        )
                    )
                }
            }
            val openSocket: () -> Unit = {
                connectionState = WebSocketConnectionState.Connecting
                internalSocket.open(config.socketConfig)
                    .recover { throwable ->
                        logger.e { "[connect] Failed to open socket. ${throwable.message}" }
                        continuation.resumeWith(Result.failure(throwable))
                    }
            }
            val connect: () -> Unit = {
                connectionState = WebSocketConnectionState.Authenticating
                val authRequest = WSAuthMessageRequest(
                    products = listOf("feeds"),
                    token = data.token,
                    userDetails = ConnectUserDetailsRequest(
                        id = data.userId,
                        image = data.image,
                        invisible = data.invisible,
                        language = data.language,
                        name = data.name,
                        custom = data.custom
                    )
                )
                eventParser.encode(authRequest).mapCatching {
                    logger.v { "[onOpen] Sending auth request: $it" }
                    internalSocket.send(it)
                }.recover {
                    logger.e(it) { "[onOpen] Failed to serialize auth request. ${it.message}" }
                    failure(it)
                }
            }

            // Subscribe for events
            messageSubscription = internalSocket.subscribe(eventListener).onFailure { throwable ->
                logger.e { "[connect] Failed to subscribe for events, will not receive `ConnectedEvent`. ${throwable.message}" }
                failure(throwable)
            }.getOrNull()

            // Add socket listener that just handles the connect, after which we can remove it
            val connectListener = object : StreamWebSocketListener {

                override fun onOpen(response: Response) {
                    if (response.code == 101) {
                        logger.d { "[onOpen] Socket opened" }
                        connect()
                    } else {
                        val err =
                            IllegalStateException("Failed to open socket. Code: ${response.code}")
                        logger.e(err) { "[onOpen] Socket failed to open. Code: ${response.code}" }
                        failure(err)
                    }
                }

                override fun onMessage(text: String) {
                    logger.d { "[onMessage] Socket message (string): $text" }
                    eventParser.decode(text).map { authResponse ->
                        when (authResponse) {
                            // Handle `ConnectedEvent`
                            is ConnectedEvent -> {
                                logger.v { "[onMessage] Handling connected event: $authResponse" }
                                val me = authResponse.me
                                val connectedUser = StreamConnectedUser(
                                    me.createdAt,
                                    me.id,
                                    me.language,
                                    me.role,
                                    me.updatedAt,
                                    me.blockedUserIds ?: emptyList(),
                                    me.teams,
                                    me.custom,
                                    me.deactivatedAt,
                                    me.deletedAt,
                                    me.image,
                                    me.lastActive,
                                    me.name,
                                )
                                connectedEvent = authResponse
                                success(connectedUser, authResponse.connectionId)
                            }

                            // Handle `ConnectionErrorEvent`
                            is ConnectionErrorEvent -> {
                                logger.e { "[onMessage] Socket connection recoverable error: $authResponse" }
                                apiFailure(authResponse.error)
                            }
                        }
                    }.recover {
                        logger.e(it) { "[onMessage] Failed to deserialize socket message. ${it.message}" }
                        failure(it)
                    }
                }
            }

            subscription = internalSocket.subscribe(connectListener).onFailure { throwable ->
                logger.e { "[connect] Failed to subscribe for events, will not receive `ConnectedEvent`. ${throwable.message}" }
                failure(throwable)
            }.getOrNull()

            openSocket()
        }

    private fun init() {
        // Declare health check
        healthMonitor.onInterval {
            logger.v { "[onInterval] Socket health check" }
            val healthCheckEvent = connectedEvent?.copy()
            if (healthCheckEvent != null) {
                logger.v { "[onInterval] Socket health check sending: $connectedEvent" }
                eventParser.encode(healthCheckEvent)
                    .onSuccess { text ->
                        internalSocket.send(text)
                    }.onFailure {
                        logger.e(it) { "[onInterval] Socket health check failed. ${it.message}" }
                    }
            } else {
                logger.e { "[onInterval] Socket health check not run. Connected event is null" }
            }
        }

        healthMonitor.onLivenessThreshold {
            logger.e { "[onLivenessThreshold] Socket liveness threshold reached" }
            disconnect(DisconnectionSource.NoPongReceived)
        }

        // Declare batch processing
        debounceProcessor.start()
        debounceProcessor.onBatch { batch, delay, count ->
            logger.v { "[onBatch] Socket batch (delay: $delay ms, buffer size: $count): $batch" }
            healthMonitor.ack()
            batch.forEach { event ->
                // Skip heath.check events
                if (event !is HealthCheckEvent) {
                    subscriptionManager.forEach {
                        it.onEvent(event)
                    }
                }
            }
        }
    }

    private fun cleanup(error: Throwable?) {
        logger.v { "[cleanup] Socket cleanup (err: ${error?.message})" }
        healthMonitor.stop()
        debounceProcessor.stop()
        messageSubscription?.cancel()
        messageSubscription = null
        connectedEvent = null
    }
}
