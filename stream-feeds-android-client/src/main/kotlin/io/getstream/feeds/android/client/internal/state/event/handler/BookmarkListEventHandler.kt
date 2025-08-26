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
package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.BookmarkListStateUpdates
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.network.models.BookmarkFolderDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkFolderUpdatedEvent
import io.getstream.feeds.android.network.models.BookmarkUpdatedEvent
import io.getstream.feeds.android.network.models.WSEvent

internal class BookmarkListEventHandler(private val state: BookmarkListStateUpdates) :
    FeedsEventListener {

    override fun onEvent(event: WSEvent) {
        when (event) {
            is BookmarkFolderDeletedEvent -> state.onBookmarkFolderRemoved(event.bookmarkFolder.id)
            is BookmarkFolderUpdatedEvent ->
                state.onBookmarkFolderUpdated(event.bookmarkFolder.toModel())
            is BookmarkUpdatedEvent -> state.onBookmarkUpdated(event.bookmark.toModel())
        }
    }
}
