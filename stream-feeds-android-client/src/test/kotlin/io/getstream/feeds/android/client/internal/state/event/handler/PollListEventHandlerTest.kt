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

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.state.query.PollsFilterField
import io.getstream.feeds.android.client.internal.state.PollListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteCasted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteChanged
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteRemoved
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.mockk.MockKVerificationScope
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class PollListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(PollListStateUpdates) -> Unit,
) : BaseEventHandlerTest<PollListStateUpdates>(testName, event, verifyBlock) {

    override val state: PollListStateUpdates = mockk(relaxed = true)
    override val handler = PollListEventHandler(filter, state)

    companion object {
        private val filter = PollsFilterField.name.equal("Test Poll")
        private val matchingPoll = pollData(name = "Test Poll")
        private val nonMatchingPoll = pollData(name = "Different Poll")
        private val pollVote = pollVoteData()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<PollListStateUpdates>(
                    name = "PollDeleted",
                    event = PollDeleted("feed-1", "poll-1"),
                    verifyBlock = { state -> state.onPollDeleted("poll-1") },
                ),
                testParams<PollListStateUpdates>(
                    name = "PollUpdated matching filter",
                    event = PollUpdated("feed-1", matchingPoll),
                    verifyBlock = { state -> state.onPollUpdated(matchingPoll) },
                ),
                testParams<PollListStateUpdates>(
                    name = "PollUpdated non-matching filter",
                    event = PollUpdated("feed-1", nonMatchingPoll),
                    verifyBlock = { state -> state.onPollDeleted(nonMatchingPoll.id) },
                ),
                testParams<PollListStateUpdates>(
                    name = "PollVoteCasted",
                    event = PollVoteCasted("feed-1", "poll-1", pollVote),
                    verifyBlock = { state -> state.onPollVoteUpserted("poll-1", pollVote) },
                ),
                testParams<PollListStateUpdates>(
                    name = "PollVoteChanged",
                    event = PollVoteChanged("feed-1", "poll-1", pollVote),
                    verifyBlock = { state -> state.onPollVoteUpserted("poll-1", pollVote) },
                ),
                testParams<PollListStateUpdates>(
                    name = "PollVoteRemoved",
                    event = PollVoteRemoved("feed-1", "poll-1", pollVote),
                    verifyBlock = { state -> state.onPollVoteRemoved("poll-1", pollVote) },
                ),
            )
    }
}
