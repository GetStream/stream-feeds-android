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

import io.getstream.feeds.android.client.internal.state.CommentReplyListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class CommentReplyListEventHandler(private val state: CommentReplyListStateUpdates) :
    StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.CommentAdded -> {
                state.onCommentUpserted(event.comment)
            }

            is StateUpdateEvent.CommentDeleted -> {
                state.onCommentRemoved(event.comment.id)
            }

            is StateUpdateEvent.CommentUpdated -> {
                state.onCommentUpserted(event.comment)
            }

            is StateUpdateEvent.CommentReactionDeleted -> {
                state.onCommentReactionRemoved(event.comment, event.reaction)
            }

            is StateUpdateEvent.CommentReactionUpserted -> {
                state.onCommentReactionUpserted(event.comment, event.reaction, event.enforceUnique)
            }

            else -> {}
        }
    }
}
