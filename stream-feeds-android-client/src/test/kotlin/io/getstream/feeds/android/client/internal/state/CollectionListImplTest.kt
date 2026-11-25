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

package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.CollectionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.CollectionsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.CollectionsRepository
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.subscribe.onEvent
import io.getstream.feeds.android.client.internal.test.TestData.collectionData
import io.getstream.feeds.android.client.internal.test.TestSubscriptionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectionListImplTest {
    private val collectionsRepository: CollectionsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        TestSubscriptionManager()
    private val query = CollectionsQuery(limit = 10)

    private val collectionList =
        CollectionListImpl(
            query = query,
            collectionsRepository = collectionsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return collections and update state`() = runTest {
        val collections = listOf(collectionData("collection-1"), collectionData("collection-2"))
        coEvery { collectionsRepository.queryCollections(query) } returns
            Result.success(paginationResult(collections, next = "next-cursor"))

        val result = collectionList.get()

        assertEquals(collections, result.getOrNull())
        assertEquals(collections, collectionList.state.collections.value)
        assertEquals("next-cursor", collectionList.state.pagination?.next)
    }

    @Test
    fun `on queryMoreCollections with next cursor, then query the next page`() = runTest {
        setupInitialState()
        val moreCollections = listOf(collectionData("collection-2"))
        val nextQuery = query.copy(next = "next-cursor")
        coEvery { collectionsRepository.queryCollections(nextQuery) } returns
            Result.success(paginationResult(moreCollections))

        val result = collectionList.queryMoreCollections()

        assertEquals(moreCollections, result.getOrNull())
        assertEquals(2, collectionList.state.collections.value.size)
        coVerify { collectionsRepository.queryCollections(nextQuery) }
    }

    @Test
    fun `on queryMoreCollections with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = collectionList.queryMoreCollections()

        assertEquals(emptyList<CollectionData>(), result.getOrNull())
        coVerify(exactly = 1) { collectionsRepository.queryCollections(any()) }
    }

    @Test
    fun `on queryMoreCollections with custom limit, then use custom limit`() = runTest {
        setupInitialState()
        val nextQuery = query.copy(limit = 5, next = "next-cursor")
        coEvery { collectionsRepository.queryCollections(nextQuery) } returns
            Result.success(paginationResult(emptyList()))

        collectionList.queryMoreCollections(limit = 5)

        coVerify { collectionsRepository.queryCollections(nextQuery) }
    }

    @Test
    fun `on get called twice, collections are replaced not merged`() = runTest {
        val page1 = listOf(collectionData("collection-1"), collectionData("collection-2"))
        val page2 = listOf(collectionData("collection-3"))
        coEvery { collectionsRepository.queryCollections(query) } returnsMany
            listOf(Result.success(paginationResult(page1)), Result.success(paginationResult(page2)))

        collectionList.get()
        collectionList.get()

        assertEquals(page2, collectionList.state.collections.value)
    }

    @Test
    fun `on collection events, then update state`() = runTest {
        setupInitialState()
        val added = collectionData("collection-2", createdAt = 2000)

        subscriptionManager.onEvent(StateUpdateEvent.CollectionsAdded(listOf(added)))
        assertEquals(
            listOf(added, collectionData("collection-1")),
            collectionList.state.collections.value,
        )

        subscriptionManager.onEvent(StateUpdateEvent.CollectionsDeleted(listOf(added.reference)))
        assertEquals(listOf(collectionData("collection-1")), collectionList.state.collections.value)
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        coEvery { collectionsRepository.queryCollections(query) } returns
            Result.success(paginationResult(listOf(collectionData("collection-1")), nextCursor))
        collectionList.get()
    }

    private fun paginationResult(collections: List<CollectionData>, next: String? = null) =
        PaginationResult(
            models = collections,
            pagination = PaginationData(next = next, previous = null),
        )
}
