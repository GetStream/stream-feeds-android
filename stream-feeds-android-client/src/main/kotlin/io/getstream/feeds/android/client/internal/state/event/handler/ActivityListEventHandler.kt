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

import io.getstream.feeds.android.client.internal.state.ActivityListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class ActivityListEventHandler(private val state: ActivityListStateUpdates) :
    StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.ActivityDeleted -> state.onActivityRemoved(event.activityId)
            is StateUpdateEvent.ActivityReactionAdded -> state.onReactionAdded(event.reaction)
            is StateUpdateEvent.ActivityReactionDeleted -> state.onReactionRemoved(event.reaction)
            is StateUpdateEvent.BookmarkAdded -> state.onBookmarkAdded(event.bookmark)
            is StateUpdateEvent.BookmarkDeleted -> state.onBookmarkRemoved(event.bookmark)
            is StateUpdateEvent.CommentAdded -> state.onCommentAdded(event.comment)
            is StateUpdateEvent.CommentDeleted -> state.onCommentRemoved(event.comment)
            else -> {
                // No action needed for other event types
            }
        }
    }
}
