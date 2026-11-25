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

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.state.query.CollectionsFilterField
import io.getstream.feeds.android.client.internal.state.CollectionListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CollectionsAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CollectionsDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CollectionsUpdated
import io.getstream.feeds.android.client.internal.test.TestData.collectionData
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class CollectionListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(CollectionListStateUpdates) -> Unit,
) : BaseEventHandlerTest<CollectionListStateUpdates>(testName, event, verifyBlock) {

    override val state: CollectionListStateUpdates = mockk(relaxed = true)

    override val handler = CollectionListEventHandler(filter, state)

    companion object {
        private val filter = CollectionsFilterField.name.equal("movies")
        private val matching = collectionData(name = "movies")
        private val nonMatching = collectionData(name = "books")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<CollectionListStateUpdates>(
                    name = "CollectionsAdded passes only the collections matching the filter",
                    event = CollectionsAdded(listOf(matching, nonMatching)),
                    verifyBlock = { state -> state.onCollectionsAdded(listOf(matching)) },
                ),
                testParams<CollectionListStateUpdates>(
                    name = "CollectionsAdded with no matching collection",
                    event = CollectionsAdded(listOf(nonMatching)),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<CollectionListStateUpdates>(
                    name = "CollectionsUpdated passes every collection to the state",
                    event = CollectionsUpdated(listOf(matching, nonMatching)),
                    verifyBlock = { state ->
                        state.onCollectionsUpdated(listOf(matching, nonMatching))
                    },
                ),
                testParams<CollectionListStateUpdates>(
                    name = "CollectionsDeleted calls onCollectionsRemoved",
                    event = CollectionsDeleted(listOf("movies:collection-1")),
                    verifyBlock = { state ->
                        state.onCollectionsRemoved(listOf("movies:collection-1"))
                    },
                ),
            )
    }
}
