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
package io.getstream.android.core.user

/**
 * Represents a user JWT token.
 *
 * @param rawValue The raw string value of the user token.
 */
public data class UserToken(public val rawValue: String) {

    public companion object {

        /** Represents an empty user token. */
        public val EMPTY: UserToken = UserToken(rawValue = "")
    }
}
