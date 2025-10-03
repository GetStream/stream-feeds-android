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

import io.getstream.feeds.android.client.api.state.query.BookmarksFilter
import io.getstream.feeds.android.client.internal.state.BookmarkListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.query.matches
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class BookmarkListEventHandler(
    private val filter: BookmarksFilter?,
    private val state: BookmarkListStateUpdates,
) : StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.BookmarkFolderDeleted ->
                state.onBookmarkFolderRemoved(event.folderId)

            is StateUpdateEvent.BookmarkFolderUpdated -> state.onBookmarkFolderUpdated(event.folder)
            is StateUpdateEvent.BookmarkAdded -> {
                if (event.bookmark matches filter) {
                    state.onBookmarkUpserted(event.bookmark)
                }
            }

            is StateUpdateEvent.BookmarkDeleted -> state.onBookmarkRemoved(event.bookmark)
            is StateUpdateEvent.BookmarkUpdated -> {
                if (event.bookmark matches filter) {
                    state.onBookmarkUpserted(event.bookmark)
                } else {
                    // We remove elements that used to match the filter but no longer do
                    state.onBookmarkRemoved(event.bookmark)
                }
            }

            else -> {}
        }
    }
}
