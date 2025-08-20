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
import io.getstream.feeds.android.core.generated.models.PollClosedFeedEvent
import io.getstream.feeds.android.core.generated.models.PollDeletedFeedEvent
import io.getstream.feeds.android.core.generated.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.core.generated.models.PollVoteCastedFeedEvent
import io.getstream.feeds.android.core.generated.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.core.generated.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

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
    private val state: ActivityStateUpdates,
) : StateEventHandler {

    /**
     * Processes a WebSocket event and updates the activity state.
     *
     * @param event The WebSocket event to process.
     */
    override fun handleEvent(event: WSEvent) {
        when (event) {
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
                val poll = event.poll.toModel()
                state.onPollVoteCasted(vote, poll)
            }

            is PollVoteChangedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                val vote = event.pollVote.toModel()
                val poll = event.poll.toModel()
                state.onPollVoteChanged(vote, poll)
            }

            is PollVoteRemovedFeedEvent -> {
                if (event.fid != fid.rawValue) return
                val vote = event.pollVote.toModel()
                val poll = event.poll.toModel()
                state.onPollVoteRemoved(vote, poll)
            }
        }
    }
}
