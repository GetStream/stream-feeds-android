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

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.state.ActivityStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * This class handles activity-related WebSocket events and updates the activity state accordingly.
 * It is responsible for processing incoming events related to polls, such as poll updates, poll
 * votes, and poll closures.
 *
 * @param fid The unique identifier for the feed this handler is associated with.
 * @property state The instance that manages updates to the activity state.
 */
internal class ActivityEventHandler(
    private val fid: FeedId,
    private val activityId: String,
    private val state: ActivityStateUpdates,
) : StateUpdateEventListener {

    /**
     * Processes a state update event and updates the activity state.
     *
     * @param event The state update event to process.
     */
    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.ActivityReactionAdded -> {
                if (event.fid != fid.rawValue || event.reaction.activityId != activityId) return
                state.onReactionUpserted(event.reaction, event.activity)
            }

            is StateUpdateEvent.ActivityReactionDeleted -> {
                if (event.fid != fid.rawValue || event.reaction.activityId != activityId) return
                state.onReactionRemoved(event.reaction, event.activity)
            }

            is StateUpdateEvent.ActivityReactionUpdated -> {
                if (event.fid != fid.rawValue || event.reaction.activityId != activityId) return
                state.onReactionUpserted(event.reaction, event.activity)
            }

            is StateUpdateEvent.ActivityUpdated -> {
                if (event.fid != fid.rawValue || event.activity.id != activityId) return
                state.onActivityUpdated(event.activity)
            }

            is StateUpdateEvent.BookmarkAdded -> {
                val eventActivity = event.bookmark.activity
                if (fid.rawValue !in eventActivity.feeds || eventActivity.id != activityId) return
                state.onBookmarkUpserted(event.bookmark)
            }

            is StateUpdateEvent.BookmarkDeleted -> {
                val eventActivity = event.bookmark.activity
                if (fid.rawValue !in eventActivity.feeds || eventActivity.id != activityId) return
                state.onBookmarkRemoved(event.bookmark)
            }

            is StateUpdateEvent.BookmarkUpdated -> {
                val eventActivity = event.bookmark.activity
                if (fid.rawValue !in eventActivity.feeds || eventActivity.id != activityId) return
                state.onBookmarkUpserted(event.bookmark)
            }

            is StateUpdateEvent.PollClosed -> {
                if (event.fid != fid.rawValue) return
                state.onPollClosed(event.poll)
            }

            is StateUpdateEvent.PollDeleted -> {
                if (event.fid != fid.rawValue) return
                state.onPollDeleted(event.pollId)
            }

            is StateUpdateEvent.PollUpdated -> {
                if (event.fid != fid.rawValue) return
                state.onPollUpdated(event.poll)
            }

            is StateUpdateEvent.PollVoteCasted -> {
                if (event.fid != fid.rawValue) return
                state.onPollVoteCasted(event.vote, event.pollId)
            }

            is StateUpdateEvent.PollVoteChanged -> {
                if (event.fid != fid.rawValue) return
                state.onPollVoteChanged(event.vote, event.pollId)
            }

            is StateUpdateEvent.PollVoteRemoved -> {
                if (event.fid != fid.rawValue) return
                state.onPollVoteRemoved(event.vote, event.pollId)
            }

            else -> {}
        }
    }
}
