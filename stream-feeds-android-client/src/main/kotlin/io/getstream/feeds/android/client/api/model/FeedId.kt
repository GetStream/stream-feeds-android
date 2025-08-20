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
 * A unique identifier for a feed in the Stream Feeds system. A `FeedId` consists of two components:
 * - `group`: The feed group identifier (e.g., "user", "timeline", "notification")
 * - `id`: The specific feed identifier within that group
 *
 * The complete feed identifier is represented as a colon-separated string: `"group:id"`
 *
 * For example: `"user:john"`, `"timeline:flat"`, `"notification:aggregated"`
 *
 * Example:
 * ```kotlin
 * val feedId = FeedId(group = "user", id = "john")
 * // Creates "user:john"
 * ```
 *
 * Creates a new feed identifier with the specified group and feed IDs.
 *
 * @param group The feed group identifier (e.g., "user", "timeline")
 * @param id The specific feed identifier within the group
 */
public data class FeedId(
    /**
     * The feed group identifier that categorizes the type of feed.
     *
     * Common group IDs include:
     * - `"user"`: User-specific feeds
     * - `"timeline"`: Timeline feeds
     * - `"notification"`: Notification feeds
     * - `"aggregated"`: Aggregated feeds
     */
    public val group: String,

    /**
     * The specific feed identifier within the group.
     *
     * This is typically a user ID, feed name, or other unique identifier that distinguishes this
     * feed from others in the same group.
     */
    public val id: String,
) {

    /**
     * Creates a feed identifier from a raw string value.
     *
     * The string should be in the format `"group:id"`. If the string doesn't contain a colon
     * separator, the entire string will be used as the [id] and [group] will be empty.
     *
     * Example:
     * ```kotlin
     * val feedId = FeedId("user:john")
     * // Creates FeedId(group = "user", id = "john")
     * ```
     *
     * @param rawValue The raw string representation of the feed ID
     */
    public constructor(
        rawValue: String
    ) : this(
        group = rawValue.substringBefore(':', missingDelimiterValue = ""),
        id = rawValue.substringAfter(':'),
    )

    /**
     * The complete feed identifier as a colon-separated string.
     *
     * This is the canonical string representation of the feed ID, formatted as `"group:id"`. This
     * value is used for API requests and serialization.
     */
    public val rawValue: String = "$group:$id"
}
