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

import io.getstream.feeds.android.client.internal.state.PollVoteListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteCasted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteChanged
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteRemoved
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class PollVoteListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(PollVoteListStateUpdates) -> Unit,
) : BaseEventHandlerTest<PollVoteListStateUpdates>(testName, event, verifyBlock) {

    override val state: PollVoteListStateUpdates = mockk(relaxed = true)
    override val handler = PollVoteListEventHandler(pollId, state)

    companion object {
        private const val pollId = "poll-1"
        private const val otherPollId = "other-poll"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<PollVoteListStateUpdates>(
                    name = "PollDeleted matching poll",
                    event = PollDeleted(pollId),
                    verifyBlock = { state -> state.onPollDeleted() },
                ),
                testParams<PollVoteListStateUpdates>(
                    name = "PollDeleted non-matching poll",
                    event = PollDeleted(otherPollId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<PollVoteListStateUpdates>(
                    name = "PollVoteCasted matching poll",
                    event = PollVoteCasted(pollData(pollId), pollVoteData()),
                    verifyBlock = { state -> state.pollVoteUpserted(pollVoteData()) },
                ),
                testParams<PollVoteListStateUpdates>(
                    name = "PollVoteCasted non-matching poll",
                    event = PollVoteCasted(pollData(otherPollId), pollVoteData()),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<PollVoteListStateUpdates>(
                    name = "PollVoteChanged matching poll",
                    event = PollVoteChanged(pollData(pollId), pollVoteData()),
                    verifyBlock = { state -> state.pollVoteUpserted(pollVoteData()) },
                ),
                testParams<PollVoteListStateUpdates>(
                    name = "PollVoteChanged non-matching poll",
                    event = PollVoteChanged(pollData(otherPollId), pollVoteData()),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<PollVoteListStateUpdates>(
                    name = "PollVoteRemoved matching poll",
                    event = PollVoteRemoved(pollData(pollId), pollVoteData("poll-vote-1")),
                    verifyBlock = { state -> state.pollVoteRemoved("poll-vote-1") },
                ),
                testParams<PollVoteListStateUpdates>(
                    name = "PollVoteRemoved non-matching poll",
                    event = PollVoteRemoved(pollData(otherPollId), pollVoteData("poll-vote-1")),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
