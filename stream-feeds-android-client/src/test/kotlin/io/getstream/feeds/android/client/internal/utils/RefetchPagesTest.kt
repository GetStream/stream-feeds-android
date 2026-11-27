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

    @Test
    fun `on refetch around, then load both directions until each side is covered`() = runTest {
        val result =
            refetchAround(
                loadedBefore = 4,
                loadedAfter = 2,
                around = pages("d", "e", "f"),
                previous = listOf(pages("b", "c"), pages("a"), pages("z")),
                next = listOf(pages("g", "h"), pages("i")),
            )

        // One item before and one after the anchor come with the first page
        assertEquals(
            PaginationResult(
                listOf("a", "b", "c", "d", "e", "f", "g", "h"),
                PaginationData(next = "next-1", previous = "previous-2"),
            ),
            result,
        )
        assertEquals(listOf("around", "previous-0", "previous-1", "next-0"), requested)
    }

    @Test
    fun `on refetch around with the first page covering both sides, then load it only`() = runTest {
        val result =
            refetchAround(
                loadedBefore = 1,
                loadedAfter = 1,
                around = pages("d", "e", "f"),
                previous = listOf(pages("c")),
                next = listOf(pages("g")),
            )

        assertEquals(
            PaginationResult(
                listOf("d", "e", "f"),
                PaginationData(next = "next-0", previous = "previous-0"),
            ),
            result,
        )
        assertEquals(listOf<String?>("around"), requested)
    }

    @Test
    fun `on refetch around with one side running out, then stop that side only`() = runTest {
        val result =
            refetchAround(
                loadedBefore = 10,
                loadedAfter = 3,
                around = pages("d", "e", "f"),
                previous = listOf(pages("c")),
                next = listOf(pages("g"), pages("h"), pages("i")),
            )

        assertEquals(
            PaginationResult(
                listOf("c", "d", "e", "f", "g", "h"),
                PaginationData(next = "next-2", previous = null),
            ),
            result,
        )
        assertEquals(listOf("around", "previous-0", "next-0", "next-1"), requested)
    }

    @Test
    fun `on refetch around with a page adding nothing new, then stop that side only`() = runTest {
        val result =
            refetchAround(
                loadedBefore = 10,
                loadedAfter = 10,
                around = pages("d", "e", "f"),
                previous = listOf(pages("c"), pages("b")),
                next = listOf(pages("g", "e"), pages("f", "g"), pages("h")),
            )

        // The repeated "e" keeps its place on the first page
        assertEquals(
            PaginationResult(
                listOf("b", "c", "d", "e", "f", "g"),
                PaginationData(next = "next-2", previous = null),
            ),
            result,
        )
        assertEquals(listOf("around", "previous-0", "previous-1", "next-0", "next-1"), requested)
    }

    @Test
    fun `on refetch around without the anchor in the first page, then load forward only`() =
        runTest {
            val result =
                refetchAround(
                    loadedBefore = 2,
                    loadedAfter = 2,
                    around = pages("a", "b"),
                    previous = listOf(pages("z")),
                    next = listOf(pages("c", "d"), pages("f"), pages("g")),
                )

            // Five items were loaded, so the pages from the start cover five
            assertEquals(
                PaginationResult(
                    listOf("a", "b", "c", "d", "f"),
                    PaginationData(next = "next-2", previous = "previous-0"),
                ),
                result,
            )
            assertEquals(listOf("around", "next-0", "next-1"), requested)
        }

    private fun pages(vararg items: String) = items.toList()

    /**
     * Serves [around] for the anchor "e", then the [previous] and [next] pages in order. The tokens
     * are the direction and the index of the page they lead to.
     */
    private suspend fun refetchAround(
        loadedBefore: Int,
        loadedAfter: Int,
        around: List<String>,
        previous: List<List<String>>,
        next: List<List<String>>,
    ): PaginationResult<String> {
        fun token(direction: String, index: Int, pages: List<List<String>>) =
            "$direction-$index".takeIf { index < pages.size }
        return refetchPagesAround("e", loadedBefore, loadedAfter, idSelector = { it }) {
            previousToken,
            nextToken ->
            requested += previousToken ?: nextToken ?: "around"
            val index = (previousToken ?: nextToken)?.substringAfter('-')?.toInt()
            when {
                index == null ->
                    PaginationResult(
                        around,
                        PaginationData(
                            next = token("next", 0, next),
                            previous = token("previous", 0, previous),
                        ),
                    )
                previousToken != null ->
                    PaginationResult(
                        previous[index],
                        PaginationData(previous = token("previous", index + 1, previous)),
                    )
                else ->
                    PaginationResult(
                        next[index],
                        PaginationData(next = token("next", index + 1, next)),
                    )
            }
        }
    }

    /** Serves [pages] in order, with the index of the following page as the next page token. */
    private suspend fun refetch(loadedCount: Int, vararg pages: List<String>) =
        refetchPages(loadedCount, idSelector = { it }) { next ->
            requested += next
            val index = next?.toInt() ?: 0
            val following = (index + 1).takeIf { it < pages.size }?.toString()
            PaginationResult(pages[index], PaginationData(next = following))
        }
}
