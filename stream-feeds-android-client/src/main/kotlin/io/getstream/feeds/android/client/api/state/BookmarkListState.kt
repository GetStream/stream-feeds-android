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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a bookmark list.
 *
 * This class maintains the current list of bookmarks, pagination information, and provides
 * real-time updates when bookmarks or bookmark folders are added, removed, or modified. It
 * automatically handles WebSocket events to keep the bookmark list synchronized.
 */
public interface BookmarkListState {

    /** The query used to fetch the bookmarks. */
    public val query: BookmarksQuery

    /** All the paginated bookmarks. */
    public val bookmarks: StateFlow<List<BookmarkData>>

    /** Last pagination information. */
    public val pagination: PaginationData?

    /** Indicates whether there are more bookmarks available to load. */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
