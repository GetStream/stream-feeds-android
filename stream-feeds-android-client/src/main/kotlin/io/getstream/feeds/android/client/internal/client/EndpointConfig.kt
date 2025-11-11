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

/**
 * Configuration for Stream Feeds API endpoints.
 *
 * This data class holds the necessary URLs for connecting to Stream Feeds services, including both
 * HTTP REST API endpoints and WebSocket connections.
 *
 * @property httpUrl The base URL for HTTP REST API requests
 * @property wsUrl The base URL for WebSocket connections
 */
internal data class EndpointConfig(
    /**
     * The base URL for HTTP REST API requests. Used for standard API calls like creating feeds,
     * activities, and other operations.
     */
    val httpUrl: String,
    /**
     * The base URL for WebSocket connections. Used for real-time subscriptions and live updates
     * from Stream Feeds.
     */
    val wsUrl: String,
) {

    companion object {

        /** Staging environment (frankfurt:c2). */
        val STAGING =
            EndpointConfig(
                httpUrl = "https://chat-edge-frankfurt-ce1.stream-io-api.com",
                wsUrl = "wss://chat-edge-frankfurt-ce1.stream-io-api.com/api/v2/connect",
            )

        /** Production environment. */
        val PRODUCTION =
            EndpointConfig(
                httpUrl = "https://feeds.stream-io-api.com",
                wsUrl = "wss://feeds.stream-io-api.com/api/v2/connect",
            )
    }
}
