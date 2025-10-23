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

import io.getstream.feeds.android.client.internal.state.ActivityReactionListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionUpdated
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class ActivityReactionListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(ActivityReactionListStateUpdates) -> Unit,
) : BaseEventHandlerTest<ActivityReactionListStateUpdates>(testName, event, verifyBlock) {

    override val state: ActivityReactionListStateUpdates = mockk(relaxed = true)
    override val handler = ActivityReactionListEventHandler(activityId, state)

    companion object {
        private const val activityId = "activity-1"
        private const val differentActivityId = "different-activity"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityDeleted matching activity",
                    event = ActivityDeleted("feed-1", activityId),
                    verifyBlock = { state -> state.onActivityRemoved() },
                ),
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityDeleted non-matching activity",
                    event = ActivityDeleted("feed-1", differentActivityId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityReactionAdded for matching activity",
                    event =
                        ActivityReactionAdded(
                            "feed-1",
                            activityData(activityId),
                            feedsReactionData(activityId),
                        ),
                    verifyBlock = { state ->
                        state.onReactionUpserted(feedsReactionData(activityId))
                    },
                ),
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityReactionAdded for different activity",
                    event =
                        ActivityReactionAdded(
                            "feed-1",
                            activityData(differentActivityId),
                            feedsReactionData(differentActivityId),
                        ),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityReactionDeleted for matching activity",
                    event =
                        ActivityReactionDeleted(
                            "feed-1",
                            activityData(activityId),
                            feedsReactionData(activityId),
                        ),
                    verifyBlock = { state ->
                        state.onReactionRemoved(feedsReactionData(activityId))
                    },
                ),
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityReactionDeleted for different activity",
                    event =
                        ActivityReactionDeleted(
                            "feed-1",
                            activityData(differentActivityId),
                            feedsReactionData(differentActivityId),
                        ),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityReactionUpdated for matching activity",
                    event =
                        ActivityReactionUpdated(
                            "feed-1",
                            activityData(activityId),
                            feedsReactionData(activityId),
                        ),
                    verifyBlock = { state ->
                        state.onReactionUpserted(feedsReactionData(activityId))
                    },
                ),
                testParams<ActivityReactionListStateUpdates>(
                    name = "ActivityReactionUpdated for different activity",
                    event =
                        ActivityReactionUpdated(
                            "feed-1",
                            activityData(differentActivityId),
                            feedsReactionData(differentActivityId),
                        ),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
