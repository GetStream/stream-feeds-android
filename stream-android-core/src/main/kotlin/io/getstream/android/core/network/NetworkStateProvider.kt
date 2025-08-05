package io.getstream.android.core.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Provides network connectivity state monitoring and change notifications.
 *
 * This class monitors the device's network connectivity state and notifies registered listeners
 * when the connection state changes. It uses Android's ConnectivityManager to track network
 * availability and validates that networks have internet access capabilities.
 *
 * The provider automatically starts monitoring when the first listener is subscribed and stops
 * when the last listener is unsubscribed to optimize battery usage.
 *
 * @param scope The CoroutineScope in which network state changes will be handled
 * @param connectivityManager The system's ConnectivityManager service
 */
@StreamInternalApi
public class NetworkStateProvider(
    private val scope: CoroutineScope,
    private val connectivityManager: ConnectivityManager,
) {

    private val lock: Any = Any()

    private val availableNetworks: MutableSet<Network> = mutableSetOf()
    private val listeners: MutableSet<NetworkStateListener> = mutableSetOf()

    /**
     * Indicator if the [NetworkStateProvider] is currently active and listening to
     */
    private val isActive: AtomicBoolean = AtomicBoolean(false)

    /**
     * Internally keeps track of the current network connection state.
     */
    private var isConnected: Boolean = isConnected()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            availableNetworks.add(network)
            notifyNetworkStateChanged()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            notifyNetworkStateChanged()
        }

        override fun onLost(network: Network) {
            availableNetworks.remove(network)
            notifyNetworkStateChanged()
            if (availableNetworks.isEmpty()) {
                // No available networks, notify listeners about disconnection
                scope.launch {
                    listeners.forEach { it.onDisconnected() }
                }
            }
        }
    }

    /**
     * Checks if the device is currently connected to a network with internet access.
     *
     * For API level 23 (Android M) and above, this method validates that the network has
     * both internet capability and is validated by the system. For older versions, it
     * falls back to checking if the active network info indicates a connection.
     *
     * @return `true` if the device is connected to the internet, `false` otherwise
     */
    public fun isConnected(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            runCatching {
                connectivityManager.run {
                    getNetworkCapabilities(activeNetwork)?.run {
                        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    }
                }
            }.getOrNull() ?: false
        } else {
            connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    /**
     * Subscribes to network state changes.
     *
     * When the first listener is subscribed, the provider automatically starts monitoring
     * network changes. The listener will be notified immediately if there are any subsequent
     * changes in network connectivity.
     *
     * @param listener The listener to be notified about network state changes
     */
    public fun subscribe(listener: NetworkStateListener) {
        synchronized(lock) {
            listeners.add(listener)
            if (isActive.compareAndSet(false, true)) {
                startListening()
            }
        }
    }

    /**
     * Unsubscribes from network state changes.
     *
     * When the last listener is unsubscribed, the provider automatically stops monitoring
     * network changes to conserve battery and system resources.
     *
     * @param listener The listener to be removed
     */
    public fun unsubscribe(listener: NetworkStateListener) {
        synchronized(lock) {
            listeners.remove(listener)
            if (listeners.isEmpty() && isActive.compareAndSet(true, false)) {
                stopListening()
            }
        }
    }

    private fun startListening() {
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun stopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun notifyNetworkStateChanged() {
        val isNowConnected = isConnected()
        if (!isConnected && isNowConnected) {
            isConnected = true
            scope.launch {
                listeners.forEach { it.onConnected() }
            }
        } else if (isConnected && !isNowConnected) {
            isConnected = false
            scope.launch {
                listeners.forEach { it.onDisconnected() }
            }
        }
    }

    /**
     * Listener interface for network state changes.
     *
     * Implementations of this interface will be notified when the device's network
     * connectivity state changes between connected and disconnected states.
     */
    public interface NetworkStateListener {
        /**
         * Called when the device becomes connected to a network with internet access.
         *
         * This method is invoked when the device transitions from a disconnected state
         * to a connected state, or when network capabilities change to indicate
         * internet access is now available.
         */
        public suspend fun onConnected()

        /**
         * Called when the device becomes disconnected from the network.
         *
         * This method is invoked when the device loses network connectivity or when
         * the network no longer has internet access capabilities.
         */
        public suspend fun onDisconnected()
    }
}
