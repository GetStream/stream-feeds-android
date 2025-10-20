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
import io.getstream.android.core.result.runSafely
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
    fun `on cache when pending requests exist, complete them with new capabilities`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        val deferred = async { repository.fetch(feedId) }
        advanceUntilIdle()

        repository.cache(mapOf(feedId to capabilities))

        assertEquals(Result.success(capabilities), deferred.await())
    }

    @Test
    fun `on fetch when cached, return capabilities without offering to batcher`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)
        repository.cache(mapOf(feedId to capabilities))

        val result = repository.fetch(feedId)

        assertEquals(Result.success(capabilities), result)
        verify(exactly = 0) { batcher.offer(any()) }
    }

    @Test
    fun `on fetch when cache miss, return result after batch completes`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        val deferred = async { repository.fetch(feedId) }
        advanceUntilIdle()

        mockSuccessfulBatch(mapOf(feedId to capabilities))
        batchCallbackSlot.captured.invoke(listOf(feedId), 0L, 1)

        assertEquals(Result.success(capabilities), deferred.await())
        verify(exactly = 1) { batcher.offer(feedId) }
    }

    @Test
    fun `on fetch when concurrent calls for same ID, share single deferred`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        val fetch1 = async { repository.fetch(feedId) }
        val fetch2 = async { repository.fetch(feedId) }
        val fetch3 = async { repository.fetch(feedId) }
        advanceUntilIdle()

        mockSuccessfulBatch(mapOf(feedId to capabilities))
        batchCallbackSlot.captured.invoke(listOf(feedId), 0L, 1)

        val expectedResult = Result.success(capabilities)
        assertEquals(expectedResult, fetch1.await())
        assertEquals(expectedResult, fetch2.await())
        assertEquals(expectedResult, fetch3.await())
        verify(exactly = 1) { batcher.offer(feedId) }
    }

    @Test
    fun `on fetch when cache populated during pending request, return cached result`() = runTest {
        val feedId = FeedId("user:123")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        val deferred = async { repository.fetch(feedId) }
        advanceUntilIdle()

        // Simulate race condition: another source caches while fetch is pending
        repository.cache(mapOf(feedId to capabilities))

        assertEquals(Result.success(capabilities), deferred.await())
    }

    @Test
    fun `on processBatch when some IDs cached, only fetch uncached ones`() = runTest {
        val cachedId = FeedId("user:cached")
        val uncachedId = FeedId("user:uncached")
        val cachedCapabilities = listOf(FeedOwnCapability.ReadFeed)
        val uncachedCapabilities = listOf(FeedOwnCapability.AddActivity)

        repository.cache(mapOf(cachedId to cachedCapabilities))
        mockSuccessfulBatch(mapOf(uncachedId to uncachedCapabilities))

        batchCallbackSlot.captured.invoke(listOf(cachedId, uncachedId), 0L, 2)

        coVerify(exactly = 1) {
            api.ownCapabilitiesBatch(
                connectionId = null,
                ownCapabilitiesBatchRequest = match { it.feeds == listOf(uncachedId.rawValue) },
            )
        }
    }

    @Test
    fun `on processBatch when all IDs already cached, skip API call`() = runTest {
        val feedId1 = FeedId("user:123")
        val feedId2 = FeedId("user:456")
        val capabilities = listOf(FeedOwnCapability.ReadFeed)

        repository.cache(mapOf(feedId1 to capabilities, feedId2 to capabilities))

        batchCallbackSlot.captured.invoke(listOf(feedId1, feedId2), 0L, 2)

        coVerify(exactly = 0) { api.ownCapabilitiesBatch(any(), any()) }
    }

    @Test
    fun `on processBatch when successful, complete pending requests`() = runTest {
        val feedId1 = FeedId("user:123")
        val feedId2 = FeedId("user:456")
        val capabilities1 = listOf(FeedOwnCapability.ReadFeed)
        val capabilities2 = listOf(FeedOwnCapability.AddActivity, FeedOwnCapability.AddComment)

        val fetch1 = async { repository.fetch(feedId1) }
        val fetch2 = async { repository.fetch(feedId2) }
        advanceUntilIdle()

        mockSuccessfulBatch(mapOf(feedId1 to capabilities1, feedId2 to capabilities2))
        batchCallbackSlot.captured.invoke(listOf(feedId1, feedId2), 0L, 2)

        assertEquals(Result.success(capabilities1), fetch1.await())
        assertEquals(Result.success(capabilities2), fetch2.await())
    }

    @Test
    fun `on processBatch when failure, complete pending requests exceptionally`() = runTest {
        val feedId = FeedId("user:123")
        val exception = RuntimeException("API failed")

        val deferred = async { repository.fetch(feedId) }
        advanceUntilIdle()

        mockFailedBatch(exception)

        // Use launch to avoid blocking on batch callback execution
        launch { batchCallbackSlot.captured.invoke(listOf(feedId), 0L, 1) }

        val result = deferred.await()
        assertEquals("API failed", result.exceptionOrNull()?.message)
    }

    private fun mockSuccessfulBatch(capabilities: Map<FeedId, List<FeedOwnCapability>>) {
        val response =
            OwnCapabilitiesBatchResponse(
                duration = "10ms",
                capabilities = capabilities.mapKeys { it.key.rawValue },
            )
        coEvery { api.ownCapabilitiesBatch(any(), any()) } returns response
        mockRetryProcessor()
    }

    private fun mockFailedBatch(exception: Throwable) {
        coEvery { api.ownCapabilitiesBatch(any(), any()) } throws exception
        mockRetryProcessor()
    }

    private fun mockRetryProcessor() {
        coEvery { retryProcessor.retry(any(), any<RetryBlock>()) } coAnswers
            {
                runSafely { secondArg<RetryBlock>()() }
            }
    }
}

private typealias RetryBlock = suspend () -> Map<FeedId, List<FeedOwnCapability>>
