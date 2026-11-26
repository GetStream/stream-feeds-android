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

import io.getstream.android.core.api.filter.equal
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.feeds.android.client.api.model.CollectionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.CollectionsFilterField
import io.getstream.feeds.android.client.api.state.query.CollectionsQuery
import io.getstream.feeds.android.client.api.state.query.CollectionsSort
import io.getstream.feeds.android.client.api.state.query.CollectionsSortField
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.collectionData
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CollectionListStateImplTest {
    private val state = CollectionListStateImpl(CollectionsQuery(limit = 10))

    private val newest = collectionData("collection-3", createdAt = 3000)
    private val oldest = collectionData("collection-1", createdAt = 1000)

    @Test
    fun `on initial state, then return empty collections and null pagination`() {
        assertEquals(emptyList<CollectionData>(), state.collections.value)
        assertNull(state.pagination)
        assertFalse(state.canLoadMore)
    }

    @Test
    fun `on onQueryCollections, then merge the page and update pagination`() {
        state.onQueryCollections(page(listOf(newest), next = "next-cursor"))
        state.onQueryCollections(page(listOf(oldest)))

        assertEquals(listOf(newest, oldest), state.collections.value)
        assertNull(state.pagination?.next)
    }

    @Test
    fun `on onQueryCollections with replace, then replace the collections`() {
        state.onQueryCollections(page(listOf(newest), next = "next-cursor"))
        state.onQueryCollections(page(listOf(oldest), next = "next-cursor"), replace = true)

        assertEquals(listOf(oldest), state.collections.value)
        assertTrue(state.canLoadMore)
    }

    @Test
    fun `on onCollectionsAdded, then insert by creation date, newest first`() {
        state.onQueryCollections(page(listOf(newest, oldest)))
        val middle = collectionData("collection-2", createdAt = 2000)

        state.onCollectionsAdded(listOf(middle))

        assertEquals(listOf(newest, middle, oldest), state.collections.value)
    }

    @Test
    fun `on onCollectionsAdded with several collections out of order, then keep the list sorted`() {
        state.onQueryCollections(page(listOf(newest, oldest)))
        val older = collectionData("collection-a", createdAt = 1500)
        val newer = collectionData("collection-b", createdAt = 2500)

        state.onCollectionsAdded(listOf(older, newer))

        assertEquals(listOf(newest, newer, older, oldest), state.collections.value)
    }

    @Test
    fun `on onCollectionsAdded with the same creation date, then order by id descending`() {
        val first = collectionData("collection-a", createdAt = 2000)
        val second = collectionData("collection-b", createdAt = 2000)

        state.onCollectionsAdded(listOf(first, second))

        assertEquals(listOf(second, first), state.collections.value)
    }

    @Test
    fun `on onCollectionsAdded for a loaded reference, then replace it`() {
        state.onQueryCollections(page(listOf(newest, oldest)))
        val replacement = oldest.copy(custom = mapOf("key" to "value"))

        state.onCollectionsAdded(listOf(replacement))

        assertEquals(listOf(newest, replacement), state.collections.value)
    }

    @Test
    fun `on onCollectionsUpdated, then update the collection and keep its creation date`() {
        state.onQueryCollections(page(listOf(newest, oldest)))
        val update = oldest.copy(createdAt = Date(9000), custom = mapOf("key" to "value"))

        state.onCollectionsUpdated(listOf(update))

        val expected = update.copy(createdAt = oldest.createdAt)
        assertEquals(listOf(newest, expected), state.collections.value)
    }

    @Test
    fun `on onCollectionsUpdated, then keep the list sorted`() {
        val sort = listOf(CollectionsSort(CollectionsSortField.UpdatedAt, SortDirection.REVERSE))
        val state = CollectionListStateImpl(CollectionsQuery(sort = sort))
        val first = collectionData("collection-1", updatedAt = 2000)
        val second = collectionData("collection-2", updatedAt = 1000)
        state.onQueryCollections(page(listOf(first, second)))
        val update = second.copy(updatedAt = Date(3000))

        state.onCollectionsUpdated(listOf(update))

        assertEquals(listOf(update, first), state.collections.value)
    }

    @Test
    fun `on onCollectionsUpdated that swaps the order of two collections, then keep the list sorted`() {
        val sort = listOf(CollectionsSort(CollectionsSortField.UpdatedAt, SortDirection.REVERSE))
        val state = CollectionListStateImpl(CollectionsQuery(sort = sort))
        val first = collectionData("collection-1", updatedAt = 3000)
        val second = collectionData("collection-2", updatedAt = 2000)
        val third = collectionData("collection-3", updatedAt = 1000)
        state.onQueryCollections(page(listOf(first, second, third)))
        val secondUpdated = second.copy(updatedAt = Date(4000))
        val thirdUpdated = third.copy(updatedAt = Date(5000))

        state.onCollectionsUpdated(listOf(secondUpdated, thirdUpdated))

        assertEquals(listOf(thirdUpdated, secondUpdated, first), state.collections.value)
    }

    @Test
    fun `on onCollectionsUpdated for a collection that is not loaded, then ignore it`() {
        state.onQueryCollections(page(listOf(newest)))

        state.onCollectionsUpdated(listOf(oldest))

        assertEquals(listOf(newest), state.collections.value)
    }

    @Test
    fun `on onCollectionsUpdated that no longer matches the filter, then remove it`() {
        val filter = CollectionsFilterField.updatedAt.equal(Date(1000))
        val state = CollectionListStateImpl(CollectionsQuery(filter = filter))
        state.onQueryCollections(page(listOf(oldest)))

        state.onCollectionsUpdated(listOf(oldest.copy(updatedAt = Date(2000))))

        assertEquals(emptyList<CollectionData>(), state.collections.value)
    }

    @Test
    fun `on onCollectionsUpdated, then check the filter against the stored creation date`() {
        val filter = CollectionsFilterField.createdAt.equal(Date(1000))
        val state = CollectionListStateImpl(CollectionsQuery(filter = filter))
        state.onQueryCollections(page(listOf(oldest)))

        state.onCollectionsUpdated(listOf(oldest.copy(createdAt = Date(9000))))

        assertEquals(listOf(oldest), state.collections.value)
    }

    @Test
    fun `on onCollectionsRemoved, then remove the collections by reference`() {
        val sameIdOtherName = collectionData("collection-1", name = "books", createdAt = 2000)
        state.onQueryCollections(page(listOf(newest, sameIdOtherName, oldest)))

        state.onCollectionsRemoved(listOf(oldest.reference, "movies:unknown"))

        assertEquals(listOf(newest, sameIdOtherName), state.collections.value)
    }

    private fun page(collections: List<CollectionData>, next: String? = null) =
        PaginationResult(
            models = collections,
            pagination = PaginationData(next = next, previous = null),
        )
}
