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

import io.getstream.feeds.android.client.api.state.query.ActivitiesFilter
import io.getstream.feeds.android.client.internal.state.ActivityListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.query.matches
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class ActivityListEventHandler(
    private val filter: ActivitiesFilter?,
    private val state: ActivityListStateUpdates,
) : StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.ActivityAdded -> {
                if (event.activity matches filter) {
                    state.onActivityUpserted(event.activity)
                }
            }

            is StateUpdateEvent.ActivityDeleted -> state.onActivityRemoved(event.activityId)
            is StateUpdateEvent.ActivityUpdated -> {
                if (event.activity matches filter) {
                    state.onActivityUpserted(event.activity)
                }
            }

            is StateUpdateEvent.ActivityReactionDeleted ->
                state.onReactionRemoved(event.reaction, event.activity)

            is StateUpdateEvent.ActivityReactionUpserted ->
                state.onReactionUpserted(event.reaction, event.activity)

            is StateUpdateEvent.BookmarkAdded -> state.onBookmarkUpserted(event.bookmark)
            is StateUpdateEvent.BookmarkDeleted -> state.onBookmarkRemoved(event.bookmark)
            is StateUpdateEvent.BookmarkUpdated -> state.onBookmarkUpserted(event.bookmark)
            is StateUpdateEvent.CommentAdded -> state.onCommentUpserted(event.comment)
            is StateUpdateEvent.CommentDeleted -> state.onCommentRemoved(event.comment)
            is StateUpdateEvent.CommentUpdated -> state.onCommentUpserted(event.comment)
            is StateUpdateEvent.CommentReactionDeleted ->
                state.onCommentReactionRemoved(event.comment, event.reaction)

            is StateUpdateEvent.CommentReactionUpserted ->
                state.onCommentReactionUpserted(event.comment, event.reaction)

            is StateUpdateEvent.PollDeleted -> state.onPollDeleted(event.pollId)
            is StateUpdateEvent.PollUpdated -> state.onPollUpdated(event.poll)
            is StateUpdateEvent.PollVoteCasted -> state.onPollVoteUpserted(event.pollId, event.vote)

            is StateUpdateEvent.PollVoteChanged ->
                state.onPollVoteUpserted(event.pollId, event.vote)

            is StateUpdateEvent.PollVoteRemoved -> state.onPollVoteRemoved(event.pollId, event.vote)

            else -> {
                // No action needed for other event types
            }
        }
    }
}
