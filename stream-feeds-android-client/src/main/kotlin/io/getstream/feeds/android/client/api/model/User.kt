/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

package io.getstream.feeds.android.client.api.model

/**
 * Represents a user in the Stream system.
 *
 * @property id The unique identifier for the user.
 * @property name The name of the user (optional).
 * @property imageURL The URL of the user's image (optional).
 * @property customData Custom data associated with the user, represented as a map (default empty
 *   map).
 * @property type The authentication type for this user.
 */
public data class User(
    public val id: String,
    public val name: String? = null,
    public val imageURL: String? = null,
    public val customData: Map<String, Any> = emptyMap(),
    public val type: UserType = UserType.Authenticated,
)

/** Defines the type of user authentication for connecting to Stream. */
public sealed class UserType {

    /** A user authenticated via your backend with a server-generated token. */
    public data object Authenticated : UserType()

    /** A temporary guest user. The SDK automatically fetches a token via the guest API. */
    public data object Guest : UserType()

    /** An anonymous user. Cannot establish a WebSocket connection (REST-only). */
    public data object Anonymous : UserType()
}
