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
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class FeedsCapabilityRepositoryTest {
    private val batchCallbackSlot = slot<suspend (List<FeedId>, Long, Int) -> Unit>()
    private val batcher: StreamBatcher<FeedId> =
        mockk(relaxed = true) { justRun { onBatch(capture(batchCallbackSlot)) } }
    private val retryProcessor: StreamRetryProcessor = mockk(relaxed = true)
    private val api: FeedsApi = mockk(relaxed = true)

    private val repository =
        FeedsCapabilityRepository(batcher = batcher, retryProcessor = retryProcessor, api = api)

    @Test
    fun `cache when given capabilities, store them in cache`() = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val capabilities =
            mapOf(
                feedId1 to listOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity),
                feedId2 to listOf(FeedOwnCapability.ReadFeed),
            )

        repository.cache(capabilities)

        // Verify cached values are returned immediately
        val result1 = repository.fetch(feedId1)
        val result2 = repository.fetch(feedId2)

        assertEquals(listOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity), result1)
        assertEquals(listOf(FeedOwnCapability.ReadFeed), result2)

        // Verify no API calls were made since values were cached
        coVerify(exactly = 0) { api.ownCapabilitiesBatch(any(), any()) }
    }

    @Test
    fun `fetch when given new feed ID, offer it to batcher and await result`() = runTest {
        val feedId = FeedId("user:1")
        val expectedCapabilities = listOf(FeedOwnCapability.ReadFeed)

        // Setup API response
        val response =
            OwnCapabilitiesBatchResponse("1ms", mapOf(feedId.rawValue to expectedCapabilities))
        coEvery {
            api.ownCapabilitiesBatch(connectionId = null, ownCapabilitiesBatchRequest = any())
        } returns response
        coEvery {
            retryProcessor.retry<Map<FeedId, List<FeedOwnCapability>>>(any(), any())
        } coAnswers
            {
                val block = secondArg<suspend () -> Map<FeedId, List<FeedOwnCapability>>>()
                Result.success(block())
            }

        // Start fetch in background (it will suspend)
        var fetchStarted = false
        val deferred = async {
            fetchStarted = true
            repository.fetch(feedId)
        }

        // Advance until idle to let async block start
        advanceUntilIdle()

        // Verify fetch started and offered to batcher
        assertEquals(true, fetchStarted)
        verify { batcher.offer(feedId) }

        // Simulate batch processing
        batchCallbackSlot.captured(listOf(feedId), 0L, 0)

        // Verify fetch completes with correct result
        val result = deferred.await()
        assertEquals(expectedCapabilities, result)
    }

    @Test
    fun `fetch when feed ID is already cached, return immediately without batching`() = runTest {
        val feedId = FeedId("user:1")
        val capabilities = mapOf(feedId to listOf(FeedOwnCapability.ReadFeed))

        repository.cache(capabilities)

        val result = repository.fetch(feedId)

        assertEquals(listOf(FeedOwnCapability.ReadFeed), result)
        verify(exactly = 0) { batcher.offer(any()) }
    }

    @Test
    fun `fetch with multiple concurrent requests for same ID, batch together`() = runTest {
        val feedId = FeedId("user:1")
        val expectedCapabilities = listOf(FeedOwnCapability.ReadFeed)

        // Setup API response
        val response =
            OwnCapabilitiesBatchResponse("1ms", mapOf(feedId.rawValue to expectedCapabilities))
        coEvery {
            api.ownCapabilitiesBatch(connectionId = null, ownCapabilitiesBatchRequest = any())
        } returns response
        coEvery {
            retryProcessor.retry<Map<FeedId, List<FeedOwnCapability>>>(any(), any())
        } coAnswers
            {
                val block = secondArg<suspend () -> Map<FeedId, List<FeedOwnCapability>>>()
                Result.success(block())
            }

        // Start multiple concurrent fetches
        val deferred1 = async { repository.fetch(feedId) }
        val deferred2 = async { repository.fetch(feedId) }
        val deferred3 = async { repository.fetch(feedId) }

        // Advance until idle to let async blocks start
        advanceUntilIdle()

        // Verify only one offer to batcher (all three share the same CompletableDeferred)
        verify(exactly = 1) { batcher.offer(feedId) }

        // Simulate batch processing
        batchCallbackSlot.captured(listOf(feedId), 0L, 0)

        // All fetches complete with same result
        assertEquals(expectedCapabilities, deferred1.await())
        assertEquals(expectedCapabilities, deferred2.await())
        assertEquals(expectedCapabilities, deferred3.await())
    }

    @Test
    fun `on batch callback, fetch capabilities and cache them`() = runTest {
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
        coEvery {
            retryProcessor.retry<Map<FeedId, List<FeedOwnCapability>>>(any(), any())
        } coAnswers
            {
                val block = secondArg<suspend () -> Map<FeedId, List<FeedOwnCapability>>>()
                Result.success(block())
            }

        // Trigger batch callback
        launch { batchCallbackSlot.captured(feedIds, 0L, 0) }
        advanceUntilIdle()

        // Verify API was called
        coVerify {
            api.ownCapabilitiesBatch(connectionId = null, ownCapabilitiesBatchRequest = any())
        }
    }

    @Test
    fun `on batch callback with api failure, retry logic is applied`() = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val feedIds = listOf(feedId1, feedId2)

        coEvery {
            api.ownCapabilitiesBatch(connectionId = null, ownCapabilitiesBatchRequest = any())
        } throws Exception("Network error")
        coEvery {
            retryProcessor.retry<Map<FeedId, List<FeedOwnCapability>>>(any(), any())
        } coAnswers
            {
                val block = secondArg<suspend () -> Map<FeedId, List<FeedOwnCapability>>>()
                Result.failure(Exception("Network error"))
            }

        // Trigger batch callback
        launch { batchCallbackSlot.captured(feedIds, 0L, 0) }
        advanceUntilIdle()

        // Verify retry processor was invoked
        coVerify { retryProcessor.retry<Map<FeedId, List<FeedOwnCapability>>>(any(), any()) }
    }

    @Test
    fun `processBatch filters already cached IDs before fetching`() = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val feedId3 = FeedId("user:3")

        // Pre-cache feedId1
        repository.cache(mapOf(feedId1 to listOf(FeedOwnCapability.ReadFeed)))

        val feedIds = listOf(feedId1, feedId2, feedId3)
        val capabilities =
            mapOf(
                feedId2.rawValue to listOf(FeedOwnCapability.AddActivity),
                feedId3.rawValue to listOf(FeedOwnCapability.UpdateFeed),
            )
        val response = OwnCapabilitiesBatchResponse("1ms", capabilities)

        coEvery {
            api.ownCapabilitiesBatch(connectionId = null, ownCapabilitiesBatchRequest = any())
        } returns response
        coEvery {
            retryProcessor.retry<Map<FeedId, List<FeedOwnCapability>>>(any(), any())
        } coAnswers
            {
                val block = secondArg<suspend () -> Map<FeedId, List<FeedOwnCapability>>>()
                Result.success(block())
            }

        // Trigger batch callback
        launch { batchCallbackSlot.captured(feedIds, 0L, 0) }
        advanceUntilIdle()

        // Verify API was called with only uncached IDs
        coVerify {
            api.ownCapabilitiesBatch(
                connectionId = null,
                ownCapabilitiesBatchRequest =
                    match { request ->
                        request.feeds.size == 2 &&
                            request.feeds.containsAll(listOf(feedId2.rawValue, feedId3.rawValue))
                    },
            )
        }
    }
}
