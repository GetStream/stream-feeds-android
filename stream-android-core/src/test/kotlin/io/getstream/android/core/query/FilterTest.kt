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
package io.getstream.android.core.query

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import org.junit.Assert
import org.junit.Test

@OptIn(StreamInternalApi::class)
internal class FilterTest {

    @Test
    fun testEqualFilter() {
        val filter = EqualFilter("field", "value")
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("field" to "value"), map)
    }

    @Test
    fun testGreaterThanFilter() {
        val filter = GreaterThanFilter("age", 18)
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("age" to mapOf("\$gt" to 18)), map)
    }

    @Test
    fun testGreaterThanOrEqualFilter() {
        val filter = GreaterThanOrEqualFilter("score", 100)
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("score" to mapOf("\$gte" to 100)), map)
    }

    @Test
    fun testLessThanFilter() {
        val filter = LessThanFilter("price", 50.0)
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("price" to mapOf("\$lt" to 50.0)), map)
    }

    @Test
    fun testLessThanOrEqualFilter() {
        val filter = LessThanOrEqualFilter("quantity", 10)
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("quantity" to mapOf("\$lte" to 10)), map)
    }

    @Test
    fun testInFilter() {
        val filter = InFilter("status", setOf("active", "pending", "completed"))
        val map = filter.toRequest()
        Assert.assertEquals(
            mapOf("status" to mapOf("\$in" to setOf("active", "pending", "completed"))),
            map,
        )
    }

    @Test
    fun testQueryFilter() {
        val filter = QueryFilter("content", "search term")
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("content" to mapOf("\$q" to "search term")), map)
    }

    @Test
    fun testAutocompleteFilter() {
        val filter = AutocompleteFilter("name", "john")
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("name" to mapOf("\$autocomplete" to "john")), map)
    }

    @Test
    fun testExistsFilter() {
        val filter = ExistsFilter("optional_field", true)
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("optional_field" to mapOf("\$exists" to true)), map)
    }

    @Test
    fun testExistsFilterFalse() {
        val filter = ExistsFilter("missing_field", false)
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("missing_field" to mapOf("\$exists" to false)), map)
    }

    @Test
    fun testContainsFilter() {
        val filter = ContainsFilter("tags", "important")
        val map = filter.toRequest()
        Assert.assertEquals(mapOf("tags" to mapOf("\$contains" to "important")), map)
    }

    @Test
    fun testPathExistsFilter() {
        val filter = PathExistsFilter("metadata", "user.preferences.theme")
        val map = filter.toRequest()
        Assert.assertEquals(
            mapOf("metadata" to mapOf("\$path_exists" to "user.preferences.theme")),
            map,
        )
    }

    @Test
    fun testAndFilter() {
        val filter1 = EqualFilter("type", "post")
        val filter2 = GreaterThanFilter("likes", 10)
        val andFilter = AndFilter(setOf(filter1, filter2))
        val map = andFilter.toRequest()

        val expected =
            mapOf("\$and" to listOf(mapOf("type" to "post"), mapOf("likes" to mapOf("\$gt" to 10))))
        Assert.assertEquals(expected, map)
    }

    @Test
    fun testOrFilter() {
        val filter1 = EqualFilter("status", "published")
        val filter2 = EqualFilter("status", "draft")
        val orFilter = OrFilter(setOf(filter1, filter2))
        val map = orFilter.toRequest()

        val expected =
            mapOf("\$or" to listOf(mapOf("status" to "published"), mapOf("status" to "draft")))
        Assert.assertEquals(expected, map)
    }

    @Test
    fun testNestedAndOrFilter() {
        val filter1 = EqualFilter("type", "article")
        val filter2 = GreaterThanFilter("views", 100)
        val filter3 = ContainsFilter("tags", "featured")

        val andFilter = AndFilter(setOf(filter1, filter2))
        val orFilter = OrFilter(setOf(andFilter, filter3))
        val map = orFilter.toRequest()

        val expected =
            mapOf(
                "\$or" to
                    listOf(
                        mapOf(
                            "\$and" to
                                listOf(
                                    mapOf("type" to "article"),
                                    mapOf("views" to mapOf("\$gt" to 100)),
                                )
                        ),
                        mapOf("tags" to mapOf("\$contains" to "featured")),
                    )
            )
        Assert.assertEquals(expected, map)
    }
}
