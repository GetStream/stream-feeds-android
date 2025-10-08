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
import io.getstream.feeds.android.client.internal.state.ActivityCommentListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class ActivityCommentListEventHandler(
    private val objectId: String,
    private val objectType: String,
    private val state: ActivityCommentListStateUpdates,
) : StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.CommentAdded -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentAdded(ThreadedCommentData(event.comment))
                }
            }

            is StateUpdateEvent.CommentDeleted -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentRemoved(event.comment.id)
                }
            }

            is StateUpdateEvent.CommentUpdated -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentUpdated(event.comment)
                }
            }

            is StateUpdateEvent.CommentReactionAdded -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentReactionUpserted(event.comment, event.reaction)
                }
            }

            is StateUpdateEvent.CommentReactionDeleted -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentReactionRemoved(event.comment, event.reaction)
                }
            }

            is StateUpdateEvent.CommentReactionUpdated -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentReactionUpserted(event.comment, event.reaction)
                }
            }

            else -> {}
        }
    }
}
