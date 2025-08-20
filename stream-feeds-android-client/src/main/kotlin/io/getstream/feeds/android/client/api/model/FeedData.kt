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

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.FeedResponse
import java.util.Date

/**
 * Model representing a feed.
 *
 * @property createdAt The date and time when the feed was created.
 * @property createdBy The user who created the feed.
 * @property custom A map of custom attributes associated with the feed.
 * @property deletedAt The date and time when the feed was deleted, if applicable.
 * @property description A description of the feed.
 * @property fid The unique identifier for the feed.
 * @property filterTags A list of tags used to filter the feed.
 * @property followerCount The number of followers for the feed.
 * @property followingCount The number of feeds that this feed is following.
 * @property groupId The group identifier for the feed.
 * @property id The unique identifier for the feed.
 * @property memberCount The number of members in the feed.
 * @property name The name of the feed.
 * @property pinCount The number of pinned items in the feed.
 * @property updatedAt The date and time when the feed was last updated.
 * @property visibility The visibility status of the feed.
 */
public data class FeedData(
    public val createdAt: Date,
    public val createdBy: UserData,
    public val custom: Map<String, Any?>?,
    public val deletedAt: Date?,
    public val description: String,
    public val fid: FeedId,
    public val filterTags: List<String>?,
    public val followerCount: Int,
    public val followingCount: Int,
    public val groupId: String,
    public val id: String,
    public val memberCount: Int,
    public val name: String,
    public val pinCount: Int,
    public val updatedAt: Date,
    public val visibility: String?,
)

/** Converts a [FeedResponse] to a [FeedData] model. */
public fun FeedResponse.toModel(): FeedData =
    FeedData(
        createdAt = createdAt.toDate(),
        createdBy = createdBy.toModel(),
        custom = custom,
        deletedAt = deletedAt?.toDate(),
        description = description,
        fid = FeedId(feed),
        filterTags = filterTags,
        followerCount = followerCount,
        followingCount = followingCount,
        groupId = groupId,
        id = id,
        memberCount = memberCount,
        name = name,
        pinCount = pinCount,
        updatedAt = updatedAt.toDate(),
        visibility = visibility,
    )
