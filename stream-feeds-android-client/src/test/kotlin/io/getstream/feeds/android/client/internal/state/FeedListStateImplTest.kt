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
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.api.state.query.FeedsSort
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
        val feeds = listOf(feedData(), feedData("feed-2", "user", "Test Feed 2"))
        val paginationResult = PaginationResult(
            models = feeds,
            pagination = PaginationData(next = "next-cursor", previous = null)
        )
        val queryConfig = QueryConfiguration(filter = null, sort = FeedsSort.Default)

        feedListState.onQueryMoreFeeds(paginationResult, queryConfig)

        assertEquals(feeds, feedListState.feeds.value)
        assertEquals("next-cursor", feedListState.pagination?.next)
        assertEquals(queryConfig, feedListState.queryConfig)
    }

    @Test
    fun `on feedUpdated, then update specific feed`() = runTest {
        val initialFeeds = listOf(feedData(), feedData("feed-2", "user", "Test Feed 2"))
        val paginationResult = PaginationResult(
            models = initialFeeds,
            pagination = PaginationData(next = "next-cursor", previous = null)
        )
        val queryConfig = QueryConfiguration(filter = null, sort = FeedsSort.Default)
        feedListState.onQueryMoreFeeds(paginationResult, queryConfig)

        val updatedFeed =
            feedData("user-1", "user", "Updated Feed", description = "Updated description")
        feedListState.onFeedUpdated(updatedFeed)

        val updatedFeeds = feedListState.feeds.value
        assertEquals(listOf(updatedFeed, initialFeeds[1]), updatedFeeds)
    }

    @Test
    fun `on feedUpdated with non-existent feed, then keep existing feeds unchanged`() = runTest {
        val initialFeeds = listOf(feedData(), feedData("feed-2", "user", "Test Feed 2"))
        val paginationResult = PaginationResult(
            models = initialFeeds,
            pagination = PaginationData(next = "next-cursor", previous = null)
        )
        val queryConfig = QueryConfiguration(filter = null, sort = FeedsSort.Default)
        feedListState.onQueryMoreFeeds(paginationResult, queryConfig)

        val nonExistentFeed = feedData("non-existent", "user", "Non-existent Feed")
        feedListState.onFeedUpdated(nonExistentFeed)

        assertEquals(initialFeeds, feedListState.feeds.value)
    }
}
