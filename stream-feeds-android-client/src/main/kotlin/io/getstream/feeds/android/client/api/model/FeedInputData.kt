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
 * A data model representing input for creating or updating a feed in the Stream Feeds system.
 *
 * @property description A description of the feed. This property provides a human-readable
 *   description of the feed's purpose or content. It may be `null` if no description is provided.
 * @property name The name of the feed. This property provides a human-readable name for the feed.
 *   It may be `null` if no name is specified.
 * @property visibility The visibility settings for the feed. This property controls who can access
 *   and interact with the feed. It may be `null` to use default visibility settings.
 * @property filterTags A list of tags used for content filtering. This property contains tags that
 *   can be used to filter content within the feed. An empty list means no tag-based filtering is
 *   applied.
 * @property members A list of initial members to add to the feed. This property contains the member
 *   configurations including user IDs, roles, and invitation settings. An empty list means no
 *   initial members are configured.
 * @property custom Custom data associated with the feed. This property allows for storing
 *   additional metadata or custom fields specific to your application's needs. An empty map means
 *   no custom data is associated with the feed.
 */
public data class FeedInputData(
    public val description: String? = null,
    public val name: String? = null,
    public val visibility: FeedVisibility? = null,
    public val filterTags: List<String> = emptyList(),
    public val members: List<FeedMemberRequestData> = emptyList(),
    public val custom: Map<String, Any?> = emptyMap(),
)

/**
 * Sealed class representing the visibility settings for a feed.
 *
 * This sealed class defines the different visibility levels that can be applied to a feed,
 * controlling who can access, view, and interact with the feed content.
 *
 * @property value The string representation of the visibility setting.
 */
public sealed class FeedVisibility(public val value: String) {
    override fun toString(): String = value

    /** Feed is visible only to followers. */
    public object Followers : FeedVisibility("followers")

    /** Feed is visible only to members. */
    public object Members : FeedVisibility("members")

    /** Feed is private and only visible to the owner. */
    public object Private : FeedVisibility("private")

    /** Feed is publicly visible to everyone. */
    public object Public : FeedVisibility("public")

    /** Feed is visible (general visibility setting). */
    public object Visible : FeedVisibility("visible")

    /** Represents an unknown visibility setting. */
    public data class Unknown(val unknownValue: String) : FeedVisibility(unknownValue)
}
