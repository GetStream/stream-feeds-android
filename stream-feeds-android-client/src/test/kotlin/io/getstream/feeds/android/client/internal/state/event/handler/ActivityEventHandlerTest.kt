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
import io.getstream.feeds.android.client.internal.test.TestData.pollResponseData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteResponseData
import io.getstream.feeds.android.network.models.PollClosedFeedEvent
import io.getstream.feeds.android.network.models.PollDeletedFeedEvent
import io.getstream.feeds.android.network.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteCastedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.Date

internal class ActivityEventHandlerTest {

    private val fid = FeedId("user", "activity-1")
    private val state: ActivityStateUpdates = mockk(relaxed = true)
    private val handler = ActivityEventHandler(fid, state)

    @Test
    fun `on PollClosedFeedEvent for matching feed, then call onPollClosed`() {
        val poll = pollResponseData()
        val event = PollClosedFeedEvent(
            createdAt = Date(),
            fid = fid.rawValue,
            poll = poll,
            type = "feeds.poll.closed"
        )

        handler.onEvent(event)

        verify { state.onPollClosed(poll.toModel()) }
    }

    @Test
    fun `on PollClosedFeedEvent for different feed, then do not call onPollClosed`() {
        val poll = pollResponseData()
        val event = PollClosedFeedEvent(
            createdAt = Date(),
            fid = "user:different-activity",
            poll = poll,
            type = "feeds.poll.closed"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onPollClosed(any()) }
    }

    @Test
    fun `on PollDeletedFeedEvent for matching feed, then call onPollDeleted`() {
        val poll = pollResponseData()
        val event = PollDeletedFeedEvent(
            createdAt = Date(),
            fid = fid.rawValue,
            poll = poll,
            type = "feeds.poll.deleted"
        )

        handler.onEvent(event)

        verify { state.onPollDeleted(poll.id) }
    }

    @Test
    fun `on PollDeletedFeedEvent for different feed, then do not call onPollDeleted`() {
        val poll = pollResponseData()
        val event = PollDeletedFeedEvent(
            createdAt = Date(),
            fid = "user:different-activity",
            poll = poll,
            type = "feeds.poll.deleted"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onPollDeleted(any()) }
    }

    @Test
    fun `on PollUpdatedFeedEvent for matching feed, then call onPollUpdated`() {
        val poll = pollResponseData()
        val event = PollUpdatedFeedEvent(
            createdAt = Date(),
            fid = fid.rawValue,
            poll = poll,
            type = "feeds.poll.updated"
        )

        handler.onEvent(event)

        verify { state.onPollUpdated(poll.toModel()) }
    }

    @Test
    fun `on PollUpdatedFeedEvent for different feed, then do not call onPollUpdated`() {
        val poll = pollResponseData()
        val event = PollUpdatedFeedEvent(
            createdAt = Date(),
            fid = "user:different-activity",
            poll = poll,
            type = "feeds.poll.updated"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onPollUpdated(any()) }
    }

    @Test
    fun `on PollVoteCastedFeedEvent for matching feed, then call onPollVoteCasted`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event = PollVoteCastedFeedEvent(
            createdAt = Date(),
            fid = fid.rawValue,
            poll = poll,
            pollVote = pollVote,
            type = "feeds.poll.vote.casted"
        )

        handler.onEvent(event)

        verify { state.onPollVoteCasted(pollVote.toModel(), poll.toModel()) }
    }

    @Test
    fun `on PollVoteCastedFeedEvent for different feed, then do not call onPollVoteCasted`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event = PollVoteCastedFeedEvent(
            createdAt = Date(),
            fid = "user:different-activity",
            poll = poll,
            pollVote = pollVote,
            type = "feeds.poll.vote.casted"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onPollVoteCasted(any(), any()) }
    }

    @Test
    fun `on PollVoteChangedFeedEvent for matching feed, then call onPollVoteChanged`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event = PollVoteChangedFeedEvent(
            createdAt = Date(),
            fid = fid.rawValue,
            poll = poll,
            pollVote = pollVote,
            type = "feeds.poll.vote.changed"
        )

        handler.onEvent(event)

        verify { state.onPollVoteChanged(pollVote.toModel(), poll.toModel()) }
    }

    @Test
    fun `on PollVoteChangedFeedEvent for different feed, then do not call onPollVoteChanged`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event = PollVoteChangedFeedEvent(
            createdAt = Date(),
            fid = "user:different-activity",
            poll = poll,
            pollVote = pollVote,
            type = "feeds.poll.vote.changed"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onPollVoteChanged(any(), any()) }
    }

    @Test
    fun `on PollVoteRemovedFeedEvent for matching feed, then call onPollVoteRemoved`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event = PollVoteRemovedFeedEvent(
            createdAt = Date(),
            fid = fid.rawValue,
            poll = poll,
            pollVote = pollVote,
            type = "feeds.poll.vote.removed"
        )

        handler.onEvent(event)

        verify { state.onPollVoteRemoved(pollVote.toModel(), poll.toModel()) }
    }

    @Test
    fun `on PollVoteRemovedFeedEvent for different feed, then do not call onPollVoteRemoved`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event = PollVoteRemovedFeedEvent(
            createdAt = Date(),
            fid = "user:different-activity",
            poll = poll,
            pollVote = pollVote,
            type = "feeds.poll.vote.removed"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onPollVoteRemoved(any(), any()) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent = object : WSEvent {
            override fun getWSEventType(): String = "unknown.event"
        }

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
