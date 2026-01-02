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

package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.internal.state.CommentReactionListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class CommentReactionListEventHandler(
    private val commentId: String,
    private val state: CommentReactionListStateUpdates,
) : StateUpdateEventListener {
    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.CommentDeleted -> {
                if (event.comment.id == commentId) {
                    state.onCommentRemoved()
                }
            }

            is StateUpdateEvent.CommentReactionDeleted -> {
                if (event.comment.id == commentId) {
                    state.onReactionRemoved(event.reaction)
                }
            }

            is StateUpdateEvent.CommentReactionUpserted -> {
                if (event.comment.id == commentId) {
                    state.onReactionUpserted(event.reaction, event.enforceUnique)
                }
            }

            else -> {}
        }
    }
}
