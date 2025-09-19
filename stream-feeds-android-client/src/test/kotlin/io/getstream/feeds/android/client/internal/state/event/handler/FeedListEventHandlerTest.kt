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

import io.getstream.feeds.android.client.internal.state.FeedListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedUpdated
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.mockk.MockKVerificationScope
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class FeedListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(FeedListStateUpdates) -> Unit,
) : BaseEventHandlerTest<FeedListStateUpdates>(testName, event, verifyBlock) {

    override val state: FeedListStateUpdates = mockk(relaxed = true)
    override val handler = FeedListEventHandler(state)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<FeedListStateUpdates>(
                    name = "FeedUpdated calls onFeedUpdated",
                    event = FeedUpdated(feedData()),
                    verifyBlock = { state -> state.onFeedUpdated(feedData()) },
                ),
                testParams<FeedListStateUpdates>(
                    name = "FeedDeleted calls onFeedRemoved",
                    event = FeedDeleted("user:feed-1"),
                    verifyBlock = { state -> state.onFeedRemoved("user:feed-1") },
                ),
            )
    }
}
