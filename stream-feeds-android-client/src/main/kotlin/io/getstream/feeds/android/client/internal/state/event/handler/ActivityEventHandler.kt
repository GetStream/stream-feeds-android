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
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.ActivityStateUpdates
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.network.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.ActivityUpdatedEvent
import io.getstream.feeds.android.network.models.BookmarkAddedEvent
import io.getstream.feeds.android.network.models.BookmarkDeletedEvent
import io.getstream.feeds.android.network.models.PollClosedFeedEvent
import io.getstream.feeds.android.network.models.PollDeletedFeedEvent
import io.getstream.feeds.android.network.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteCastedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.network.models.WSEvent

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
) : FeedsEventListener {

    /**
     * Processes a WebSocket event and updates the activity state.
     *
     * @param event The WebSocket event to process.
     */
    override fun onEvent(event: WSEvent) {
        when (event) {
            is ActivityReactionAddedEvent -> {
                if (event.fid != fid.rawValue || event.activity.id != activityId) return
                state.onReactionAdded(event.reaction.toModel())
            }

            is ActivityReactionDeletedEvent -> {
                if (event.fid != fid.rawValue || event.activity.id != activityId) return
                state.onReactionRemoved(event.reaction.toModel())
            }

            is ActivityUpdatedEvent -> {
                if (event.fid != fid.rawValue || event.activity.id != activityId) return
                state.onActivityUpdated(event.activity.toModel())
            }

            is BookmarkAddedEvent -> {
                val eventActivity = event.bookmark.activity
                if (fid.rawValue !in eventActivity.feeds || eventActivity.id != activityId) return
                state.onBookmarkAdded(event.bookmark.toModel())
            }

            is BookmarkDeletedEvent -> {
                val eventActivity = event.bookmark.activity
                if (fid.rawValue !in eventActivity.feeds || eventActivity.id != activityId) return
                state.onBookmarkRemoved(event.bookmark.toModel())
            }

            is PollClosedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                state.onPollClosed(event.poll.toModel())
            }

            is PollDeletedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                state.onPollDeleted(event.poll.id)
            }

            is PollUpdatedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                state.onPollUpdated(event.poll.toModel())
            }

            is PollVoteCastedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                val vote = event.pollVote.toModel()
                state.onPollVoteCasted(vote, event.poll.id)
            }

            is PollVoteChangedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                val vote = event.pollVote.toModel()
                state.onPollVoteChanged(vote, event.poll.id)
            }

            is PollVoteRemovedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                val vote = event.pollVote.toModel()
                state.onPollVoteRemoved(vote, event.poll.id)
            }
        }
    }
}
