package io.getstream.feeds.android.client.internal.socket.common.reconnect

import io.getstream.android.core.lifecycle.StreamLifecycleObserver
import io.getstream.android.core.network.NetworkStateProvider
import io.getstream.feeds.android.client.internal.socket.FeedsSocket

/**
 * Interface that defines a policy for determining whether the WebSocket should attempt
 * an automatic reconnection based on specific conditions.
 */
internal interface AutomaticReconnectionPolicy {

    /**
     * Determines whether the WebSocket should attempt to reconnect automatically.
     *
     * @return `true` if the WebSocket should attempt to reconnect, `false` otherwise
     */
    fun shouldReconnect(): Boolean
}

/**
 * A reconnection policy that checks if automatic reconnection is enabled based
 * on the current connection state.
 *
 * @param socket The [FeedsSocket] instance to check for reconnection settings
 */
internal class WebSocketAutomaticReconnectionPolicy(
    private val socket: FeedsSocket
) : AutomaticReconnectionPolicy {

    override fun shouldReconnect(): Boolean {
        return socket.connectionState.isAutomaticReconnectionEnabled
    }
}

/**
 * A reconnection policy that checks internet connectivity before allowing reconnection.
 * This prevents unnecessary reconnection attempts when there's no network available.
 *
 * @param networkStateProvider Provider for checking network connectivity status
 */
internal class InternetAvailabilityReconnectionPolicy(
    private val networkStateProvider: NetworkStateProvider
) : AutomaticReconnectionPolicy {

    override fun shouldReconnect(): Boolean {
        return networkStateProvider.isConnected()
    }
}

/**
 * A reconnection policy that checks the application's lifecycle state before allowing reconnection.
 * This prevents reconnection when the app is in the background to save battery and resources.
 *
 * @param lifecycleObserver Observer for monitoring the application's lifecycle state
 */
internal class BackgroundStateReconnectionPolicy(
    private val lifecycleObserver: StreamLifecycleObserver,
): AutomaticReconnectionPolicy {

    override fun shouldReconnect(): Boolean {
        return lifecycleObserver.isResumed()
    }
}

/**
 * A composite reconnection policy that combines multiple [AutomaticReconnectionPolicy] instances
 * using a logical operator (AND/OR). This allows for complex reconnection logic by combining
 * multiple conditions.
 *
 * @param operator The logical operator to use when combining policies ([Operator.AND] or [Operator.OR])
 * @param policies List of reconnection policies to evaluate
 */
internal class CompositeReconnectionPolicy(
    private val operator: Operator,
    private val policies: List<AutomaticReconnectionPolicy>,
): AutomaticReconnectionPolicy {

    /**
     * Defines logical operators for combining multiple reconnection policies.
     */
    internal enum class Operator {
        /** 
         * Requires ALL policies to return `true` for reconnection to be allowed.
         * If any policy returns `false`, reconnection will be prevented.
         */
        AND,
        
        /** 
         * Requires ANY policy to return `true` for reconnection to be allowed.
         * If at least one policy returns `true`, reconnection will be attempted.
         */
        OR;
    }

    override fun shouldReconnect(): Boolean {
        return when (operator) {
            Operator.AND -> policies.all(AutomaticReconnectionPolicy::shouldReconnect)
            Operator.OR -> policies.any(AutomaticReconnectionPolicy::shouldReconnect)
        }
    }
}
