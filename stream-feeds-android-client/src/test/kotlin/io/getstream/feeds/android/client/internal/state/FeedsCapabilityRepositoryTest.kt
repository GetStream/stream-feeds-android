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
package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.processing.StreamBatcher
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedCapabilitiesUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestSubscriptionManager
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchResponse
import io.mockk.coEvery
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class FeedsCapabilityRepositoryTest {
    private val batchCallbackSlot = slot<suspend (List<FeedId>, Long, Int) -> Unit>()
    private val batcher: StreamBatcher<FeedId> =
        mockk(relaxed = true) { justRun { onBatch(capture(batchCallbackSlot)) } }
    private val api: FeedsApi = mockk(relaxed = true)
    private val stateEventListener: StateUpdateEventListener = mockk(relaxed = true)
    private val subscriptionManager = TestSubscriptionManager(stateEventListener)

    private val repository =
        FeedsCapabilityRepository(
            batcher = batcher,
            api = api,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `cache when given capabilities, store them in cache and notify`() {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val capabilities =
            mapOf(
                feedId1 to listOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity),
                feedId2 to listOf(FeedOwnCapability.ReadFeed),
            )

        repository.cache(capabilities)

        verify { stateEventListener.onEvent(FeedCapabilitiesUpdated(capabilities)) }
    }

    @Test
    fun `request when given new feed ID, offer it to batcher`() {
        val feedId = FeedId("user:1")

        repository.request(feedId)

        verify { batcher.offer(feedId) }
    }

    @Test
    fun `request when feed ID is already cached, do not offer to batcher`() {
        val feedId = FeedId("user:1")
        val capabilities = mapOf(feedId to listOf(FeedOwnCapability.ReadFeed))

        repository.cache(capabilities)
        repository.request(feedId)

        verify(exactly = 0) { batcher.offer(any()) }
    }

    @Test
    fun `on batch callback, fetch capabilities and cache them`(): Unit = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val feedIds = listOf(feedId1, feedId2)
        val capabilities =
            mapOf(
                feedId1.rawValue to listOf(FeedOwnCapability.ReadFeed),
                feedId2.rawValue to listOf(FeedOwnCapability.AddActivity),
            )
        val response = OwnCapabilitiesBatchResponse("1ms", capabilities)

        coEvery {
            api.ownCapabilitiesBatch(connectionId = null, ownCapabilitiesBatchRequest = any())
        } returns response

        batchCallbackSlot.captured(feedIds, 0L, 0)

        verify {
            stateEventListener.onEvent(
                FeedCapabilitiesUpdated(
                    mapOf(
                        feedId1 to listOf(FeedOwnCapability.ReadFeed),
                        feedId2 to listOf(FeedOwnCapability.AddActivity),
                    )
                )
            )
        }
    }

    @Test
    fun `on batch callback with api failure, re-queue feed IDs`(): Unit = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val feedIds = listOf(feedId1, feedId2)

        coEvery {
            api.ownCapabilitiesBatch(connectionId = null, ownCapabilitiesBatchRequest = any())
        } throws Exception("Network error")

        batchCallbackSlot.captured(feedIds, 0L, 0)

        verify {
            batcher.offer(feedId1)
            batcher.offer(feedId2)
        }
    }
}
