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
package io.getstream.feeds.android.client.internal.client.reconnect

import app.cash.turbine.test
import io.getstream.android.core.api.filter.`in`
import io.getstream.android.core.api.model.StreamRetryPolicy
import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.android.core.api.model.exceptions.StreamClientException
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.query.FeedsFilterField
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class FeedWatchHandlerTest {
    private val connectionEvents = MutableSharedFlow<StreamConnectionState>()
    private val feedsRepository: FeedsRepository = mockk()
    private val retryProcessor: StreamRetryProcessor = TestRetryProcessor()
    private val errorBus = MutableSharedFlow<StreamClientException>()
    private val scope = TestScope(UnconfinedTestDispatcher())

    private val handler =
        FeedWatchHandler(
            connectionState = connectionEvents,
            feedsRepository = feedsRepository,
            retryProcessor = retryProcessor,
            errorBus = errorBus,
            scope = scope,
        )

    @Test
    fun `on connected event, query feeds that are still being watched`() = runTest {
        coEvery { feedsRepository.queryFeeds(any()) } returns Result.success(mockk())
        val id1 = FeedId("group", "id1")
        val id2 = FeedId("group", "id2")
        val id3 = FeedId("group", "id3")
        val expectedQuery = FeedsQuery(FeedsFilterField.feed.`in`("group:id1", "group:id3"))

        handler.onStartWatching(id1)
        handler.onStartWatching(id2)
        handler.onStartWatching(id3)
        handler.onStopWatching(id2)

        connectionEvents.emit(StreamConnectionState.Connected(mockk(), "connection-id"))

        coVerify { feedsRepository.queryFeeds(expectedQuery) }
    }

    @Test
    fun `on connected event when query fails, post error`() = runTest {
        coEvery { feedsRepository.queryFeeds(any()) } returns Result.failure(Exception())
        val id1 = FeedId("group", "id1")
        val id2 = FeedId("group", "id2")
        handler.onStartWatching(id1)
        handler.onStartWatching(id2)

        errorBus.test {
            connectionEvents.emit(StreamConnectionState.Connected(mockk(), "connection-id"))

            assertEquals(setOf(id1, id2), (awaitItem() as? StreamFeedRewatchException)?.ids)
        }
    }

    @Test
    fun `on non-connected event, do nothing`() = runTest {
        val id1 = FeedId("group", "id1")
        handler.onStartWatching(id1)

        listOf(
                StreamConnectionState.Idle,
                StreamConnectionState.Connecting.Opening("user-id"),
                StreamConnectionState.Connecting.Authenticating("user-id"),
                StreamConnectionState.Disconnected(),
            )
            .forEach { connectionEvents.emit(it) }

        verify { feedsRepository wasNot called }
    }

    private class TestRetryProcessor : StreamRetryProcessor {
        override suspend fun <T> retry(
            policy: StreamRetryPolicy,
            block: suspend () -> T,
        ): Result<T> = runSafely { block() }
    }
}
