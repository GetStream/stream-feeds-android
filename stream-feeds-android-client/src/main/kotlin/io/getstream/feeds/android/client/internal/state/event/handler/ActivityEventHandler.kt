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

import io.getstream.feeds.android.client.internal.state.ActivityStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * This class handles activity-related WebSocket events and updates the activity state accordingly.
 * It is responsible for processing incoming events related to activities, including reactions,
 * comments, bookmarks, polls (such as poll updates, poll votes, and poll closures), and other
 * activity updates or deletions.
 *
 * @param activityId The ID of the activity this handler is associated with.
 * @param currentUserId The ID of the current user, used to filter user-specific events.
 * @property state The instance that manages updates to the activity state.
 */
internal class ActivityEventHandler(
    private val activityId: String,
    private val currentUserId: String,
    private val state: ActivityStateUpdates,
) : StateUpdateEventListener {

    /**
     * Processes a state update event and updates the activity state.
     *
     * @param event The state update event to process.
     */
    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.ActivityDeleted -> {
                if (event.activityId != activityId) return
                state.onActivityRemoved()
            }

            is StateUpdateEvent.ActivityUpdated -> {
                if (event.activity.id != activityId) return
                state.onActivityUpdated(event.activity)
            }

            is StateUpdateEvent.ActivityHidden -> {
                if (event.activityId != activityId || event.userId != currentUserId) return
                state.onActivityHidden(event.activityId, event.hidden)
            }

            is StateUpdateEvent.ActivityReactionDeleted -> {
                if (event.reaction.activityId != activityId) return
                state.onReactionRemoved(event.reaction, event.activity)
            }

            is StateUpdateEvent.ActivityReactionUpserted -> {
                if (event.reaction.activityId != activityId) return
                state.onReactionUpserted(event.reaction, event.activity, event.enforceUnique)
            }

            is StateUpdateEvent.BookmarkAdded -> {
                if (event.bookmark.activity.id != activityId) return
                state.onBookmarkUpserted(event.bookmark)
            }

            is StateUpdateEvent.BookmarkDeleted -> {
                if (event.bookmark.activity.id != activityId) return
                state.onBookmarkRemoved(event.bookmark)
            }

            is StateUpdateEvent.BookmarkUpdated -> {
                if (event.bookmark.activity.id != activityId) return
                state.onBookmarkUpserted(event.bookmark)
            }

            is StateUpdateEvent.CommentAdded -> {
                if (event.comment.objectId != activityId) return
                state.onCommentUpserted(event.comment)
            }

            is StateUpdateEvent.CommentDeleted -> {
                if (event.comment.objectId != activityId) return
                state.onCommentRemoved(event.comment.id)
            }

            is StateUpdateEvent.CommentUpdated -> {
                if (event.comment.objectId != activityId) return
                state.onCommentUpserted(event.comment)
            }

            is StateUpdateEvent.CommentReactionDeleted -> {
                if (event.comment.objectId != activityId) return
                state.onCommentReactionRemoved(event.comment, event.reaction)
            }

            is StateUpdateEvent.CommentReactionUpserted -> {
                if (event.comment.objectId != activityId) return
                state.onCommentReactionUpserted(event.comment, event.reaction, event.enforceUnique)
            }

            is StateUpdateEvent.FeedCapabilitiesUpdated ->
                state.onFeedCapabilitiesUpdated(event.capabilities)

            // The fid in poll events doesn't necessarily match all the feeds that contain the poll
            // so we can't early return here based on that.
            is StateUpdateEvent.PollDeleted -> state.onPollDeleted(event.pollId)

            is StateUpdateEvent.PollUpdated -> state.onPollUpdated(event.poll)

            is StateUpdateEvent.PollVoteCasted -> state.onPollVoteUpserted(event.vote, event.pollId)

            is StateUpdateEvent.PollVoteChanged ->
                state.onPollVoteUpserted(event.vote, event.pollId)

            is StateUpdateEvent.PollVoteRemoved -> state.onPollVoteRemoved(event.vote, event.pollId)

            else -> {}
        }
    }
}
