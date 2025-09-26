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

import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.network.models.BookmarkResponse
import java.util.Date

/**
 * A data model representing a bookmark in the Stream Feeds system.
 *
 * This class contains all the information about a bookmark, including the bookmarked activity,
 * metadata, folder organization, and user information. Bookmarks allow users to save and organize
 * activities for later reference.
 *
 * Features:
 * - Activity Bookmarking: References the bookmarked activity
 * - Folder Organization: Supports organizing bookmarks into folders
 * - Custom Metadata: Allows storing additional bookmark-specific data
 * - User Association: Tracks which user created the bookmark
 * - Timestamps: Tracks creation and update times
 *
 * @property activity The activity that has been bookmarked. This property contains the full
 *   activity data for the bookmarked item, allowing access to all activity information without
 *   additional lookups.
 * @property createdAt The date and time when the bookmark was created.
 * @property custom Custom data associated with the bookmark. This property allows for storing
 *   additional metadata or custom fields specific to your application's bookmark functionality. It
 *   may be `null` if no custom data is associated with the bookmark.
 * @property folder The bookmark folder this bookmark belongs to, if any. This property allows for
 *   organizing bookmarks into folders or collections. If the bookmark is not organized into a
 *   folder, this property is `null`.
 * @property updatedAt The date and time when the bookmark was last updated.
 * @property user The user who created the bookmark. This property contains the full user data for
 *   the user who bookmarked the activity.
 */
public data class BookmarkData(
    val activity: ActivityData,
    val createdAt: Date,
    val custom: Map<String, Any?>?,
    val folder: BookmarkFolderData?,
    val updatedAt: Date,
    val user: UserData,
) {

    /**
     * Unique identifier for the bookmark, generated from the activity ID and the user ID. This
     * identifier is used for simpler identification of bookmarks.
     */
    public val id: String
        get() = "${activity.id}${user.id}"
}

/** Converts a [BookmarkResponse] to a [BookmarkData] model. */
internal fun BookmarkResponse.toModel(): BookmarkData =
    BookmarkData(
        activity = activity.toModel(),
        createdAt = createdAt,
        custom = custom,
        folder = folder?.toModel(),
        updatedAt = updatedAt,
        user = user.toModel(),
    )
