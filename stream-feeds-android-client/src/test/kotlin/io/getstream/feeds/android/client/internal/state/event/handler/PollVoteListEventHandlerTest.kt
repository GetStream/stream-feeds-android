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

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.PollVoteListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.pollResponseData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteResponseData
import io.getstream.feeds.android.network.models.PollVoteCastedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import org.junit.Test

internal class PollVoteListEventHandlerTest {
    private val pollId = "poll-1"
    private val state: PollVoteListStateUpdates = mockk(relaxed = true)

    private val handler = PollVoteListEventHandler(pollId, state)

    @Test
    fun `on PollVoteChangedFeedEvent for matching poll, then call pollVoteUpdated`() {
        val poll = pollResponseData().copy(id = pollId)
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteChangedFeedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote_changed",
            )

        handler.onEvent(event)

        verify { state.pollVoteUpdated(pollVote.toModel()) }
    }

    @Test
    fun `on PollVoteChangedFeedEvent for different poll, then do not call pollVoteUpdated`() {
        val poll = pollResponseData().copy(id = "different-poll")
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteChangedFeedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote_changed",
            )

        handler.onEvent(event)

        verify(exactly = 0) { state.pollVoteUpdated(any()) }
    }

    @Test
    fun `on PollVoteRemovedFeedEvent for matching poll, then call pollVoteRemoved`() {
        val poll = pollResponseData().copy(id = pollId)
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteRemovedFeedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote_removed",
            )

        handler.onEvent(event)

        verify { state.pollVoteRemoved(pollVote.id) }
    }

    @Test
    fun `on PollVoteRemovedFeedEvent for different poll, then do not call pollVoteRemoved`() {
        val poll = pollResponseData().copy(id = "different-poll")
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteRemovedFeedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote_removed",
            )

        handler.onEvent(event)

        verify(exactly = 0) { state.pollVoteRemoved(any()) }
    }

    @Test
    fun `on PollVoteCastedFeedEvent for matching poll, then call pollVoteAdded`() {
        val poll = pollResponseData().copy(id = pollId)
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteCastedFeedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote_casted",
            )

        handler.onEvent(event)

        verify { state.pollVoteAdded(pollVote.toModel()) }
    }

    @Test
    fun `on PollVoteCastedFeedEvent for different poll, then do not call pollVoteAdded`() {
        val poll = pollResponseData().copy(id = "different-poll")
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteCastedFeedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote_casted",
            )

        handler.onEvent(event)

        verify(exactly = 0) { state.pollVoteAdded(any()) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent =
            object : WSEvent {
                override fun getWSEventType(): String = "unknown.event"
            }

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
