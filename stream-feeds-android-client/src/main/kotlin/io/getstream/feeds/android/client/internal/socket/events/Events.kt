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
package io.getstream.feeds.android.client.internal.socket.events

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.getstream.android.core.error.APIError
import io.getstream.feeds.android.core.generated.models.OwnUserResponse
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * Represents the "connection.ok" event type.
 *
 * Note: This event is not specified in the OpenAPI spec, so we define it manually.
 */
internal const val EVENT_TYPE_CONNECTION_OK = "connection.ok"

/**
 * Represents the "connection.error" event type.
 *
 * Note: This event is not specified in the OpenAPI spec, so we define it manually.
 */
internal const val EVENT_TYPE_CONNECTION_ERROR = "connection.error"

/**
 * Represents a WebSocket event that is sent when the connection is established successfully.
 *
 * Note: This event is not specified in the OpenAPI spec, so we define it manually.
 *
 * @property connectionId The unique identifier for the connection.
 * @property me The own user response containing user details.
 */
@JsonClass(generateAdapter = true)
internal data class ConnectedEvent(
    @Json(name = "connection_id") val connectionId: String,
    @Json(name = "me") val me: OwnUserResponse,
) : WSEvent {
    override fun getWSEventType(): String = EVENT_TYPE_CONNECTION_OK
}

/**
 * Represents a WebSocket event that is sent when there is an error in the connection.
 *
 * Note: This event is not specified in the OpenAPI spec, so we define it manually.
 *
 * @property connectionId The unique identifier for the connection.
 * @property createdAt The timestamp when the error occurred.
 * @property error The API error details.
 * @property type The type of the event.
 */
@JsonClass(generateAdapter = true)
internal data class ConnectionErrorEvent(
    @Json(name = "connection_id") val connectionId: String,
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "error") val error: APIError,
    @Json(name = "type") val type: String,
) : WSEvent {
    override fun getWSEventType(): String = EVENT_TYPE_CONNECTION_ERROR
}
