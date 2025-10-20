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
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    // Cache operations

    @Test
    fun `return null from getCached when ID not in cache`() {
        assertNull(repository.getCached(FeedId("user:123")))
    }

    @Test
    fun `store and retrieve capabilities via cache and getCached`() {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)

        repository.cache(mapOf(feedId to capabilities))

        assertEquals(capabilities, repository.getCached(feedId))
    }

    @Test
    fun `complete pending requests when caching new capabilities`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        // Start a fetch that creates a pending request
        val deferred = async { repository.fetch(feedId) }

        // Cache the capabilities, which should complete the pending request
        repository.cache(mapOf(feedId to capabilities))

        assertEquals(Result.success(capabilities), deferred.await())
    }

    @Test
    fun `return cached capabilities immediately without offering to batcher`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)
        repository.cache(mapOf(feedId to capabilities))

        val result = repository.fetch(feedId)

        assertTrue(result.isSuccess)
        assertEquals(capabilities, result.getOrNull())
        verify(exactly = 0) { batcher.offer(any()) }
    }

    @Test
    fun `offer ID to batcher and create pending request on cache miss`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        // Start fetch in background
        val deferred = async { repository.fetch(feedId) }

        // Complete the batch to finish the test
        mockSuccessfulBatch(mapOf(feedId to capabilities))
        batchCallbackSlot.captured.invoke(listOf(feedId), 0L, 1)

        // Verify the fetch completed successfully
        val result = deferred.await()
        assertTrue(result.isSuccess)
        assertEquals(capabilities, result.getOrNull())
    }

    @Test
    fun `share single deferred across concurrent fetch calls for same ID`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        // Start multiple concurrent fetches
        val fetch1 = async { repository.fetch(feedId) }
        val fetch2 = async { repository.fetch(feedId) }
        val fetch3 = async { repository.fetch(feedId) }

        // Complete the batch
        mockSuccessfulBatch(mapOf(feedId to capabilities))
        batchCallbackSlot.captured.invoke(listOf(feedId), 0L, 1)

        // All fetches should succeed with same result
        assertEquals(capabilities, fetch1.await().getOrNull())
        assertEquals(capabilities, fetch2.await().getOrNull())
        assertEquals(capabilities, fetch3.await().getOrNull())
    }

    @Test
    fun `handle cache populated during fetch (race condition)`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        // Start fetch
        val deferred = async { repository.fetch(feedId) }

        // Simulate cache populated by another source during fetch
        repository.cache(mapOf(feedId to capabilities))

        val result = deferred.await()
        assertTrue(result.isSuccess)
        assertEquals(capabilities, result.getOrNull())
    }

    // Batch processing

    @Test
    fun `filter cached IDs and only fetch uncached ones in batch`() = runTest {
        val cachedId = FeedId("user:cached")
        val uncachedId = FeedId("user:uncached")
        val cachedCapabilities = listOf(FeedOwnCapability.ReadFeed)
        val uncachedCapabilities = listOf(FeedOwnCapability.AddActivity)

        repository.cache(mapOf(cachedId to cachedCapabilities))
        mockSuccessfulBatch(mapOf(uncachedId to uncachedCapabilities))

        batchCallbackSlot.captured.invoke(listOf(cachedId, uncachedId), 0L, 2)

        // Should only call API for uncached ID
        coVerify(exactly = 1) {
            api.ownCapabilitiesBatch(
                connectionId = null,
                ownCapabilitiesBatchRequest = match { it.feeds == listOf(uncachedId.rawValue) },
            )
        }
    }

    @Test
    fun `skip API call when all batch IDs already cached`() = runTest {
        val feedId1 = FeedId("user:123")
        val feedId2 = FeedId("user:456")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        repository.cache(mapOf(feedId1 to capabilities, feedId2 to capabilities))

        batchCallbackSlot.captured.invoke(listOf(feedId1, feedId2), 0L, 2)

        // Should not call API at all
        coVerify(exactly = 0) { api.ownCapabilitiesBatch(any(), any()) }
    }

    @Test
    fun `cache results and complete pending requests on successful batch`() = runTest {
        val feedId1 = FeedId("user:123")
        val feedId2 = FeedId("user:456")
        val capabilities1 = listOf(FeedOwnCapability.ReadFeed)
        val capabilities2 = listOf(FeedOwnCapability.AddActivity, FeedOwnCapability.AddComment)

        val fetch1 = async { repository.fetch(feedId1) }
        val fetch2 = async { repository.fetch(feedId2) }

        mockSuccessfulBatch(mapOf(feedId1 to capabilities1, feedId2 to capabilities2))
        batchCallbackSlot.captured.invoke(listOf(feedId1, feedId2), 0L, 2)

        // Fetches should complete successfully
        assertEquals(capabilities1, fetch1.await().getOrNull())
        assertEquals(capabilities2, fetch2.await().getOrNull())

        // Results should be cached
        assertEquals(capabilities1, repository.getCached(feedId1))
        assertEquals(capabilities2, repository.getCached(feedId2))
    }

    @Test
    fun `complete pending requests exceptionally on failed batch`() = runTest {
        val feedId = FeedId("user:123")
        val exception = RuntimeException("API failed")

        // Start fetch in background
        val deferred = async { repository.fetch(feedId) }

        // Mock failure
        mockFailedBatch(exception)

        // Invoke batch callback in a launch block
        launch { batchCallbackSlot.captured.invoke(listOf(feedId), 0L, 1) }

        val result = deferred.await()
        assertTrue(result.isFailure)
        assertEquals("API failed", result.exceptionOrNull()?.message)

        // Should not be cached
        assertNull(repository.getCached(feedId))
    }

    // Helper methods

    private fun mockSuccessfulBatch(capabilities: Map<FeedId, List<FeedOwnCapability>>) {
        val response =
            OwnCapabilitiesBatchResponse(
                duration = "10ms",
                capabilities = capabilities.mapKeys { it.key.rawValue },
            )
        coEvery {
            retryProcessor.retry(any(), any<suspend () -> Map<FeedId, List<FeedOwnCapability>>>())
        } coAnswers
            {
                val block = secondArg<suspend () -> Map<FeedId, List<FeedOwnCapability>>>()
                Result.success(block())
            }
        coEvery { api.ownCapabilitiesBatch(any(), any()) } returns response
    }

    private fun mockFailedBatch(exception: Throwable) {
        coEvery {
            retryProcessor.retry(any(), any<suspend () -> Map<FeedId, List<FeedOwnCapability>>>())
        } returns Result.failure(exception)
        // Also mock the API in case it gets called
        coEvery { api.ownCapabilitiesBatch(any(), any()) } throws exception
    }
}
