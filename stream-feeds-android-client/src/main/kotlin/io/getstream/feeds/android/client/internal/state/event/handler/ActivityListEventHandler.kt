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
import io.getstream.feeds.android.client.internal.state.ActivityListStateUpdates
import io.getstream.feeds.android.network.models.ActivityDeletedEvent
import io.getstream.feeds.android.network.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkAddedEvent
import io.getstream.feeds.android.network.models.BookmarkDeletedEvent
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.WSEvent

internal class ActivityListEventHandler(private val state: ActivityListStateUpdates) :
    StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is ActivityDeletedEvent -> state.onActivityRemoved(event.activity.toModel())
            is ActivityReactionAddedEvent -> state.onReactionAdded(event.reaction.toModel())
            is ActivityReactionDeletedEvent -> state.onReactionRemoved(event.reaction.toModel())
            is BookmarkAddedEvent -> state.onBookmarkAdded(event.bookmark.toModel())
            is BookmarkDeletedEvent -> state.onBookmarkRemoved(event.bookmark.toModel())
            is CommentAddedEvent -> state.onCommentAdded(event.comment.toModel())
            is CommentDeletedEvent -> state.onCommentRemoved(event.comment.toModel())
            else -> {
                // No action needed for other event types
            }
        }
    }
}
