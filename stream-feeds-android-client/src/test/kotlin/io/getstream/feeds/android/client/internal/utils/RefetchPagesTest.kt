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

package io.getstream.feeds.android.client.internal.utils

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.internal.model.PaginationResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class RefetchPagesTest {
    private val requested = mutableListOf<String?>()

    @Test
    fun `on nothing loaded, then load the first page only`() = runTest {
        val result = refetch(loadedCount = 0, pages("a", "b"), pages("c"))

        assertEquals(PaginationResult(listOf("a", "b"), PaginationData(next = "1")), result)
        assertEquals(listOf<String?>(null), requested)
    }

    @Test
    fun `on several pages loaded, then load pages until they cover the loaded count`() = runTest {
        val result = refetch(loadedCount = 3, pages("a", "b"), pages("c", "d"), pages("e"))

        assertEquals(
            PaginationResult(listOf("a", "b", "c", "d"), PaginationData(next = "2")),
            result,
        )
        assertEquals(listOf(null, "1"), requested)
    }

    @Test
    fun `on fewer items than loaded, then stop at the last page`() = runTest {
        val result = refetch(loadedCount = 10, pages("a", "b"), pages("c"))

        assertEquals(PaginationResult(listOf("a", "b", "c"), PaginationData()), result)
        assertEquals(listOf(null, "1"), requested)
    }

    @Test
    fun `on an item repeated on a later page, then keep its later position`() = runTest {
        val result = refetch(loadedCount = 4, pages("a", "b"), pages("a", "c"), pages("d"))

        assertEquals(PaginationResult(listOf("b", "a", "c", "d"), PaginationData()), result)
    }

    @Test
    fun `on a page with no new items, then stop`() = runTest {
        val result = refetch(loadedCount = 10, pages("a", "b"), pages("b", "a"), pages("c"))

        assertEquals(PaginationResult(listOf("b", "a"), PaginationData(next = "2")), result)
        assertEquals(listOf(null, "1"), requested)
    }

    private fun pages(vararg items: String) = items.toList()

    /** Serves [pages] in order, with the index of the following page as the next page token. */
    private suspend fun refetch(loadedCount: Int, vararg pages: List<String>) =
        refetchPages(loadedCount, idSelector = { it }) { next ->
            requested += next
            val index = next?.toInt() ?: 0
            val following = (index + 1).takeIf { it < pages.size }?.toString()
            PaginationResult(pages[index], PaginationData(next = following))
        }
}
