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

package io.getstream.feeds.android.client.internal.client.reconnect

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.internal.model.PaginationResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class PaginatedRefetchableTest {

    @Test
    fun `on refetch with nothing loaded, then do not fetch`() = runTest {
        val list = TestList(loadedCount = null)

        list.refetch()

        assertEquals(emptyList<String?>(), list.requestedPages)
        assertNull(list.refetched)
    }

    @Test
    fun `on refetch, then replace the items with the pages that cover the loaded count`() =
        runTest {
            val list = TestList(loadedCount = 3)

            list.refetch()

            assertEquals(listOf(null, "page-2"), list.requestedPages)
            val expected = PaginationResult(listOf("a", "b", "c", "d"), PaginationData(next = null))
            assertEquals(expected, list.refetched)
        }

    private class TestList(private val loadedCount: Int?) : PaginatedRefetchable<String> {
        val requestedPages = mutableListOf<String?>()
        var refetched: PaginationResult<String>? = null

        override fun loadedCount(): Int? = loadedCount

        override fun idOf(model: String): Any = model

        override suspend fun fetchPage(next: String?): PaginationResult<String> {
            requestedPages += next
            return if (next == null) {
                PaginationResult(listOf("a", "b"), PaginationData(next = "page-2"))
            } else {
                PaginationResult(listOf("c", "d"), PaginationData(next = null))
            }
        }

        override fun onRefetched(result: PaginationResult<String>) {
            refetched = result
        }
    }
}
