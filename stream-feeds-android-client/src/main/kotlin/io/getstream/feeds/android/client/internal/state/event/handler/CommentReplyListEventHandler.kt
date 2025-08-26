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

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.CommentReplyListStateUpdates
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.CommentReactionAddedEvent
import io.getstream.feeds.android.network.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.network.models.CommentUpdatedEvent
import io.getstream.feeds.android.network.models.WSEvent

internal class CommentReplyListEventHandler(private val state: CommentReplyListStateUpdates) :
    FeedsEventListener {

    override fun onEvent(event: WSEvent) {
        when (event) {
            is CommentAddedEvent -> {
                state.onCommentAdded(ThreadedCommentData(event.comment.toModel()))
            }
            is CommentDeletedEvent -> {
                state.onCommentRemoved(event.comment.id)
            }
            is CommentUpdatedEvent -> {
                state.onCommentUpdated(event.comment.toModel())
            }
            is CommentReactionAddedEvent -> {
                state.onCommentReactionAdded(event.comment.id, event.reaction.toModel())
            }
            is CommentReactionDeletedEvent -> {
                state.onCommentReactionRemoved(event.comment.id, event.reaction.toModel())
            }
        }
    }
}
