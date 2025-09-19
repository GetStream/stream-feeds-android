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
package io.getstream.feeds.android.client.internal.state.event

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.network.models.BookmarkDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkFolderDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkFolderUpdatedEvent
import io.getstream.feeds.android.network.models.BookmarkUpdatedEvent
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.CommentReactionAddedEvent
import io.getstream.feeds.android.network.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.network.models.CommentUpdatedEvent
import io.getstream.feeds.android.network.models.FeedDeletedEvent
import io.getstream.feeds.android.network.models.FeedUpdatedEvent
import io.getstream.feeds.android.network.models.WSEvent

/**
 * Represents an event that may trigger a state update. These events are typically the result of
 * receiving a WebSocket event or having executed a successful API call that can modify the state.
 */
internal sealed interface StateUpdateEvent {

    data class BookmarkDeleted(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkUpdated(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkFolderDeleted(val folderId: String) : StateUpdateEvent

    data class BookmarkFolderUpdated(val folder: BookmarkFolderData) : StateUpdateEvent

    data class CommentAdded(val comment: CommentData) : StateUpdateEvent

    data class CommentDeleted(val comment: CommentData) : StateUpdateEvent

    data class CommentUpdated(val comment: CommentData) : StateUpdateEvent

    data class CommentReactionAdded(val comment: CommentData, val reaction: FeedsReactionData) :
        StateUpdateEvent

    data class CommentReactionDeleted(val comment: CommentData, val reaction: FeedsReactionData) :
        StateUpdateEvent

    data class FeedUpdated(val feed: FeedData) : StateUpdateEvent

    data class FeedDeleted(val fid: String) : StateUpdateEvent
}

internal fun WSEvent.toModel(): StateUpdateEvent? =
    when (this) {
        is BookmarkDeletedEvent -> StateUpdateEvent.BookmarkDeleted(bookmark.toModel())

        is BookmarkUpdatedEvent -> StateUpdateEvent.BookmarkUpdated(bookmark.toModel())

        is BookmarkFolderDeletedEvent -> StateUpdateEvent.BookmarkFolderDeleted(bookmarkFolder.id)

        is BookmarkFolderUpdatedEvent ->
            StateUpdateEvent.BookmarkFolderUpdated(bookmarkFolder.toModel())

        is CommentAddedEvent -> StateUpdateEvent.CommentAdded(comment.toModel())

        is CommentUpdatedEvent -> StateUpdateEvent.CommentUpdated(comment.toModel())

        is CommentDeletedEvent -> StateUpdateEvent.CommentDeleted(comment.toModel())

        is CommentReactionAddedEvent ->
            StateUpdateEvent.CommentReactionAdded(comment.toModel(), reaction.toModel())

        is CommentReactionDeletedEvent ->
            StateUpdateEvent.CommentReactionDeleted(comment.toModel(), reaction.toModel())

        is FeedUpdatedEvent -> StateUpdateEvent.FeedUpdated(feed.toModel())

        is FeedDeletedEvent -> StateUpdateEvent.FeedDeleted(fid)

        else -> null
    }
