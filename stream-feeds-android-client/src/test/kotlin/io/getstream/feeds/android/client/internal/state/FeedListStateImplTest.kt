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

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQueryConfig
import io.getstream.feeds.android.client.api.state.query.FeedsSort
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class FeedListStateImplTest {
    private val query = FeedsQuery(limit = 10)
    private val feedListState = FeedListStateImpl(query)

    @Test
    fun `on initial state, then return empty feeds and null pagination`() = runTest {
        assertEquals(emptyList<FeedData>(), feedListState.feeds.value)
        assertNull(feedListState.pagination)
    }

    @Test
    fun `on queryMoreFeeds, then update feeds and pagination`() = runTest {
        val feed1 = feedData(id = "feed-1", groupId = "user", name = "First Feed")
        val feed2 = feedData(id = "feed-2", groupId = "user", name = "Second Feed")
        val feeds = listOf(feed1, feed2)
        val paginationResult = defaultPaginationResult(feeds)

        feedListState.onQueryMoreFeeds(paginationResult, defaultQueryConfig)

        assertEquals(feeds, feedListState.feeds.value)
        assertEquals("next-cursor", feedListState.pagination?.next)
        assertEquals(defaultQueryConfig, feedListState.queryConfig)
    }

    @Test
    fun `on feedUpdated, then update specific feed`() = runTest {
        val feed1 = feedData(id = "feed-1", groupId = "user", name = "First Feed")
        val feed2 = feedData(id = "feed-2", groupId = "user", name = "Second Feed")
        val initialFeeds = listOf(feed1, feed2)
        val paginationResult = defaultPaginationResult(initialFeeds)
        feedListState.onQueryMoreFeeds(paginationResult, defaultQueryConfig)

        val updatedFeed =
            feedData(
                id = "feed-1",
                groupId = "user",
                name = "Updated Feed",
                description = "Updated description",
            )
        feedListState.onFeedUpdated(updatedFeed)

        val updatedFeeds = feedListState.feeds.value
        assertEquals(listOf(updatedFeed, feed2), updatedFeeds)
    }

    @Test
    fun `on feedUpdated with non-existent feed, then keep existing feeds unchanged`() = runTest {
        val feed1 = feedData(id = "feed-1", groupId = "user", name = "First Feed")
        val feed2 = feedData(id = "feed-2", groupId = "user", name = "Second Feed")
        val initialFeeds = listOf(feed1, feed2)
        val paginationResult = defaultPaginationResult(initialFeeds)
        feedListState.onQueryMoreFeeds(paginationResult, defaultQueryConfig)

        val nonExistentFeed =
            feedData(id = "non-existent", groupId = "user", name = "Non-existent Feed")
        feedListState.onFeedUpdated(nonExistentFeed)

        assertEquals(initialFeeds, feedListState.feeds.value)
    }

    @Test
    fun `on feedRemoved, then remove specific feed`() = runTest {
        val feed1 = feedData(id = "feed-1", groupId = "user", name = "First Feed")
        val feed2 = feedData(id = "feed-2", groupId = "user", name = "Second Feed")
        val initialFeeds = listOf(feed1, feed2)
        val paginationResult = defaultPaginationResult(initialFeeds)
        val queryConfig = FeedsQueryConfig(filter = null, sort = FeedsSort.Default)
        feedListState.onQueryMoreFeeds(paginationResult, queryConfig)

        feedListState.onFeedRemoved("user:feed-1")

        val remainingFeeds = feedListState.feeds.value
        assertEquals(listOf(feed2), remainingFeeds)
    }

    companion object {
        private val defaultQueryConfig = FeedsQueryConfig(filter = null, sort = FeedsSort.Default)
    }
}
