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

package io.getstream.feeds.android.client.internal.state.query

import io.getstream.android.core.api.filter.equal
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.feeds.android.client.api.state.query.CollectionsFilterField
import io.getstream.feeds.android.client.api.state.query.CollectionsQuery
import io.getstream.feeds.android.client.api.state.query.CollectionsSort
import io.getstream.feeds.android.client.api.state.query.CollectionsSortField
import io.getstream.feeds.android.client.internal.test.TestData.collectionData
import io.getstream.feeds.android.network.models.QueryCollectionsRequest
import io.getstream.feeds.android.network.models.SortParamRequest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectionsQueryMappingTest {

    @Test
    fun `collections query maps to the request`() {
        val query =
            CollectionsQuery(
                filter = CollectionsFilterField.name.equal("movies"),
                sort = listOf(CollectionsSort(CollectionsSortField.Id, SortDirection.FORWARD)),
                limit = 5,
                next = "next-cursor",
                previous = "previous-cursor",
            )

        val expected =
            QueryCollectionsRequest(
                filter = mapOf("name" to mapOf("\$eq" to "movies")),
                sort = listOf(SortParamRequest(direction = 1, field = "id")),
                limit = 5,
                next = "next-cursor",
                prev = "previous-cursor",
            )
        assertEquals(expected, query.toRequest())
    }

    @Test
    fun `empty collections query maps to an empty request`() {
        assertEquals(QueryCollectionsRequest(), CollectionsQuery().toRequest())
    }

    @Test
    fun `collection reference joins the name and the id`() {
        assertEquals("movies:lotr", collectionData(id = "lotr", name = "movies").reference)
    }
}
