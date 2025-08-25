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
package io.getstream.feeds.android.client.api.model

/**
 * Represents a user in the Stream system.
 *
 * @property id The unique identifier for the user.
 * @property name The name of the user (optional).
 * @property imageURL The URL of the user's image (optional).
 * @property role The role of the user (default "user").
 * @property type The type of authentication used by the user (default [UserAuthType.REGULAR]).
 * @property customData Custom data associated with the user, represented as a map (default empty
 *   map).
 */
public data class User(
    public val id: String,
    public val name: String? = null,
    public val imageURL: String? = null,
    public val role: String = "user",
    public val type: UserAuthType = UserAuthType.REGULAR,
    public val customData: Map<String, Any> = emptyMap(),
) {

    public companion object {

        /**
         * Creates an anonymous user.
         *
         * @return an anonymous user.
         */
        public fun anonymous(): User = User(id = "!anon", type = UserAuthType.ANONYMOUS)
    }
}

/** Represents the type of user authentication. */
public enum class UserAuthType(public val rawValue: String) {
    REGULAR("jwt"),
    ANONYMOUS("anonymous"),
}
