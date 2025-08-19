package io.getstream.feeds.android.client.internal.client

/**
 * Configuration for Stream Feeds API endpoints.
 * 
 * This data class holds the necessary URLs for connecting to Stream Feeds services,
 * including both HTTP REST API endpoints and WebSocket connections.
 *
 * @property httpUrl The base URL for HTTP REST API requests
 * @property wsUrl The base URL for WebSocket connections
 */
internal data class EndpointConfig(
    /**
     * The base URL for HTTP REST API requests.
     * Used for standard API calls like creating feeds, activities, and other operations.
     */
    val httpUrl: String,
    /**
     * The base URL for WebSocket connections.
     * Used for real-time subscriptions and live updates from Stream Feeds.
     */
    val wsUrl: String,
) {

    companion object {

        /**
         * Staging environment (frankfurt:c2).
         */
        val STAGING = EndpointConfig(
            httpUrl = "https://chat-edge-frankfurt-ce1.stream-io-api.com",
            wsUrl = "wss://chat-edge-frankfurt-ce1.stream-io-api.com/api/v2/connect",
        )

        /**
         * Production environment.
         */
        val PRODUCTION = EndpointConfig(
            httpUrl = "https://feeds.stream-io-api.com",
            wsUrl = "wss://feeds.stream-io-api.com/api/v2/connect",
        )
    }
}
