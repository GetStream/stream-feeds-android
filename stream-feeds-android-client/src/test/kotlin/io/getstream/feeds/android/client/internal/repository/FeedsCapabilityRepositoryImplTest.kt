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

package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.api.model.StreamRetryPolicy
import io.getstream.android.core.api.processing.StreamBatcher
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedOwnValuesUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberResponse
import io.getstream.feeds.android.client.internal.test.TestData.followResponse
import io.getstream.feeds.android.client.internal.test.TestSubscriptionManager
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.getstream.feeds.android.network.models.FeedOwnData
import io.getstream.feeds.android.network.models.OwnBatchResponse
import io.mockk.coEvery
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class FeedsCapabilityRepositoryImplTest {
    private val batchCallbackSlot = slot<suspend (List<FeedId>, Long, Int) -> Unit>()
    private val batcher: StreamBatcher<FeedId> =
        mockk(relaxed = true) { justRun { onBatch(capture(batchCallbackSlot)) } }
    private val retryProcessor: StreamRetryProcessor = TestRetryProcessor()
    private val api: FeedsApi = mockk(relaxed = true)
    private val stateEventListener: StateUpdateEventListener = mockk(relaxed = true)
    private val subscriptionManager = TestSubscriptionManager(stateEventListener)

    private val repository =
        FeedOwnValuesRepositoryImpl(
            batcher = batcher,
            retryProcessor = retryProcessor,
            api = api,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `cache when given same capabilities, notify only on changes`() {
        val ownValues1 = mapOf(FeedId("user:1") to testFeedOwnValues1)
        val ownValues2 = mapOf(FeedId("user:2") to testFeedOwnValues2)

        repository.cache(ownValues1)
        verify(exactly = 1) { stateEventListener.onEvent(FeedOwnValuesUpdated(ownValues1)) }

        repository.cache(ownValues1)
        verify(exactly = 1) { stateEventListener.onEvent(any()) }

        repository.cache(ownValues2)
        val allCapabilities = ownValues1 + ownValues2
        verify(exactly = 1) { stateEventListener.onEvent(FeedOwnValuesUpdated(allCapabilities)) }
        verify(exactly = 2) { stateEventListener.onEvent(any()) }
    }

    @Test
    fun `getOrRequest when given new feed ID, return null and offer it to batcher`() {
        val feedId = FeedId("user:1")

        val result = repository.getOrRequest(feedId)

        assertNull(result)
        verify { batcher.offer(feedId) }
    }

    @Test
    fun `getOrRequest when feed ID is already cached, return cached and do not offer to batcher`() {
        val feedId = FeedId("user:1")
        repository.cache(mapOf(feedId to testFeedOwnValues1))

        val result = repository.getOrRequest(feedId)

        assertEquals(testFeedOwnValues1, result)
        verify(exactly = 0) { batcher.offer(any()) }
    }

    @Test
    fun `on batch callback, fetch capabilities and cache them`(): Unit = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val feedIds = listOf(feedId1, feedId2)
        val ownData =
            mapOf(feedId1.rawValue to testFeedOwnData1, feedId2.rawValue to testFeedOwnData2)
        val response = OwnBatchResponse("1ms", ownData)

        coEvery { api.ownBatch(connectionId = null, ownBatchRequest = any()) } returns response

        batchCallbackSlot.captured(feedIds, 0L, 0)

        verify {
            stateEventListener.onEvent(
                FeedOwnValuesUpdated(
                    mapOf(feedId1 to testFeedOwnValues1, feedId2 to testFeedOwnValues2)
                )
            )
        }
    }

    @Test
    fun `on batch callback with api failure then retry`(): Unit = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val feedIds = listOf(feedId1, feedId2)
        val ownData =
            mapOf(feedId1.rawValue to testFeedOwnData1, feedId2.rawValue to testFeedOwnData2)
        val response = OwnBatchResponse("1ms", ownData)
        var callCount = 0

        coEvery { api.ownBatch(null, ownBatchRequest = any()) } coAnswers
            {
                callCount++
                if (callCount < 3) {
                    throw Exception("Network error")
                }
                response
            }

        batchCallbackSlot.captured(feedIds, 0L, 0)

        assertEquals(3, callCount)
        verify {
            stateEventListener.onEvent(
                FeedOwnValuesUpdated(
                    mapOf(feedId1 to testFeedOwnValues1, feedId2 to testFeedOwnValues2)
                )
            )
        }
    }

    private class TestRetryProcessor : StreamRetryProcessor {
        override suspend fun <T> retry(
            policy: StreamRetryPolicy,
            block: suspend () -> T,
        ): Result<T> {
            var lastException: Throwable? = null

            repeat(policy.maxRetries) {
                val result = runSafely { block() }
                if (result.isSuccess) {
                    return result
                }
                lastException = result.exceptionOrNull()
            }
            return Result.failure(lastException ?: Exception("Retry exhausted"))
        }
    }

    companion object {
        private val testFeedOwnData1 =
            FeedOwnData(
                ownCapabilities = listOf(FeedOwnCapability.ReadFeed),
                ownFollows = listOf(followResponse()),
                ownMembership = feedMemberResponse(),
            )
        private val testFeedOwnData2 =
            FeedOwnData(
                ownCapabilities = listOf(FeedOwnCapability.AddActivity),
                ownFollows = listOf(followResponse()),
                ownMembership = feedMemberResponse(),
            )
        private val testFeedOwnValues1 = testFeedOwnData1.toModel()
        private val testFeedOwnValues2 = testFeedOwnData2.toModel()
    }
}
