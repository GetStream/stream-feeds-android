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
package io.getstream.feeds.android.client.api.query

import io.getstream.feeds.android.client.api.state.query.ActivitiesFilterField
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class LegacyFilterToRequestTest(
    private val filter: Filter,
    private val expectedRequest: Map<String, Any>,
    private val testName: String,
) {

    @Test
    fun `toRequest should convert filter to correct request map`() {
        val result = filter.toRequest()
        assertEquals("Test case: $testName", expectedRequest, result)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{2}")
        fun data(): Collection<Array<Any>> =
            listOf(
                // EqualFilter
                arrayOf(
                    EqualFilter("field1", "value1"),
                    mapOf("field1" to "value1"),
                    "EqualFilter with string value",
                ),
                arrayOf(
                    EqualFilter("id", 123),
                    mapOf("id" to 123),
                    "EqualFilter with numeric value",
                ),

                // GreaterThanFilter
                arrayOf(
                    GreaterThanFilter("created_at", 1234567890),
                    mapOf("created_at" to mapOf("\$gt" to 1234567890)),
                    "GreaterThanFilter with numeric value",
                ),

                // GreaterThanOrEqualFilter
                arrayOf(
                    GreaterThanOrEqualFilter("score", 75.5),
                    mapOf("score" to mapOf("\$gte" to 75.5)),
                    "GreaterThanOrEqualFilter with decimal value",
                ),

                // LessThanFilter
                arrayOf(
                    LessThanFilter("updated_at", 9876543210),
                    mapOf("updated_at" to mapOf("\$lt" to 9876543210)),
                    "LessThanFilter with numeric value",
                ),

                // LessThanOrEqualFilter
                arrayOf(
                    LessThanOrEqualFilter("rating", 4.0),
                    mapOf("rating" to mapOf("\$lte" to 4.0)),
                    "LessThanOrEqualFilter with decimal value",
                ),

                // InFilter
                arrayOf(
                    InFilter("tags", setOf("tag1", "tag2", "tag3")),
                    mapOf("tags" to mapOf("\$in" to setOf("tag1", "tag2", "tag3"))),
                    "InFilter with string set",
                ),
                arrayOf(
                    InFilter("ids", setOf(1, 2, 3)),
                    mapOf("ids" to mapOf("\$in" to setOf(1, 2, 3))),
                    "InFilter with numeric set",
                ),

                // QueryFilter
                arrayOf(
                    QueryFilter("content", "search term"),
                    mapOf("content" to mapOf("\$q" to "search term")),
                    "QueryFilter with search term",
                ),

                // AutocompleteFilter
                arrayOf(
                    AutocompleteFilter("name", "prefix"),
                    mapOf("name" to mapOf("\$autocomplete" to "prefix")),
                    "AutocompleteFilter with prefix",
                ),

                // ExistsFilter
                arrayOf(
                    ExistsFilter("optional_field", true),
                    mapOf("optional_field" to mapOf("\$exists" to true)),
                    "ExistsFilter with true value",
                ),
                arrayOf(
                    ExistsFilter("missing_field", false),
                    mapOf("missing_field" to mapOf("\$exists" to false)),
                    "ExistsFilter with false value",
                ),

                // ContainsFilter
                arrayOf(
                    ContainsFilter("categories", "sports"),
                    mapOf("categories" to mapOf("\$contains" to "sports")),
                    "ContainsFilter with string value",
                ),

                // PathExistsFilter
                arrayOf(
                    PathExistsFilter("metadata", "user.profile.avatar"),
                    mapOf("metadata" to mapOf("\$path_exists" to "user.profile.avatar")),
                    "PathExistsFilter with nested path",
                ),

                // AndFilter with multiple filters
                arrayOf(
                    AndFilter(
                        setOf(
                            EqualFilter("type", "post"),
                            GreaterThanFilter("created_at", 1234567890),
                        )
                    ),
                    mapOf(
                        "\$and" to
                            listOf(
                                mapOf("type" to "post"),
                                mapOf("created_at" to mapOf("\$gt" to 1234567890)),
                            )
                    ),
                    "AndFilter with equal and greater than filters",
                ),

                // OrFilter with multiple filters
                arrayOf(
                    OrFilter(
                        setOf(EqualFilter("status", "published"), EqualFilter("status", "draft"))
                    ),
                    mapOf(
                        "\$or" to listOf(mapOf("status" to "published"), mapOf("status" to "draft"))
                    ),
                    "OrFilter with multiple equal filters",
                ),

                // Nested AndFilter and OrFilter
                arrayOf(
                    AndFilter(
                        setOf(
                            EqualFilter("author", "john"),
                            OrFilter(
                                setOf(
                                    EqualFilter("category", "tech"),
                                    EqualFilter("category", "science"),
                                )
                            ),
                        )
                    ),
                    mapOf(
                        "\$and" to
                            listOf(
                                mapOf("author" to "john"),
                                mapOf(
                                    "\$or" to
                                        listOf(
                                            mapOf("category" to "tech"),
                                            mapOf("category" to "science"),
                                        )
                                ),
                            )
                    ),
                    "Nested AndFilter containing OrFilter",
                ),
            )
    }
}

@RunWith(Parameterized::class)
internal class TypedFilterToRequestTest(
    private val filter: TypedFilter<*>,
    private val expectedRequest: Map<String, Any>,
    private val testName: String,
) {

    @Test
    fun `toRequest should convert typed filter to correct request map`() {
        val result = filter.toRequest()
        assertEquals("Test case: $testName", expectedRequest, result)
    }

    companion object {
        private val testField = ActivitiesFilterField.Id
        private val testTextField = ActivitiesFilterField.Text
        private val testCreatedAtField = ActivitiesFilterField.CreatedAt

        @JvmStatic
        @Parameterized.Parameters(name = "{2}")
        fun data(): Collection<Array<Any>> =
            listOf(
                // BinaryOperationFilter - EQUAL
                arrayOf(
                    BinaryOperationFilter(FilterOperator.EQUAL, testField, "activity-123"),
                    mapOf("id" to mapOf("\$eq" to "activity-123")),
                    "BinaryOperationFilter with EQUAL operator",
                ),

                // BinaryOperationFilter - GREATER
                arrayOf(
                    BinaryOperationFilter(FilterOperator.GREATER, testCreatedAtField, 1234567890),
                    mapOf("created_at" to mapOf("\$gt" to 1234567890)),
                    "BinaryOperationFilter with GREATER operator",
                ),

                // BinaryOperationFilter - GREATER_OR_EQUAL
                arrayOf(
                    BinaryOperationFilter(
                        FilterOperator.GREATER_OR_EQUAL,
                        testCreatedAtField,
                        1000000000,
                    ),
                    mapOf("created_at" to mapOf("\$gte" to 1000000000)),
                    "BinaryOperationFilter with GREATER_OR_EQUAL operator",
                ),

                // BinaryOperationFilter - LESS
                arrayOf(
                    BinaryOperationFilter(FilterOperator.LESS, testCreatedAtField, 9999999999),
                    mapOf("created_at" to mapOf("\$lt" to 9999999999)),
                    "BinaryOperationFilter with LESS operator",
                ),

                // BinaryOperationFilter - LESS_OR_EQUAL
                arrayOf(
                    BinaryOperationFilter(
                        FilterOperator.LESS_OR_EQUAL,
                        testCreatedAtField,
                        8888888888,
                    ),
                    mapOf("created_at" to mapOf("\$lte" to 8888888888)),
                    "BinaryOperationFilter with LESS_OR_EQUAL operator",
                ),

                // BinaryOperationFilter - IN
                arrayOf(
                    BinaryOperationFilter(
                        FilterOperator.IN,
                        testField,
                        listOf("id1", "id2", "id3"),
                    ),
                    mapOf("id" to mapOf("\$in" to listOf("id1", "id2", "id3"))),
                    "BinaryOperationFilter with IN operator",
                ),

                // BinaryOperationFilter - QUERY
                arrayOf(
                    BinaryOperationFilter(FilterOperator.QUERY, testTextField, "search term"),
                    mapOf("text" to mapOf("\$q" to "search term")),
                    "BinaryOperationFilter with QUERY operator",
                ),

                // BinaryOperationFilter - AUTOCOMPLETE
                arrayOf(
                    BinaryOperationFilter(
                        FilterOperator.AUTOCOMPLETE,
                        testTextField,
                        "auto prefix",
                    ),
                    mapOf("text" to mapOf("\$autocomplete" to "auto prefix")),
                    "BinaryOperationFilter with AUTOCOMPLETE operator",
                ),

                // BinaryOperationFilter - EXISTS
                arrayOf(
                    BinaryOperationFilter(FilterOperator.EXISTS, testField, true),
                    mapOf("id" to mapOf("\$exists" to true)),
                    "BinaryOperationFilter with EXISTS operator (true)",
                ),
                arrayOf(
                    BinaryOperationFilter(FilterOperator.EXISTS, testField, false),
                    mapOf("id" to mapOf("\$exists" to false)),
                    "BinaryOperationFilter with EXISTS operator (false)",
                ),

                // BinaryOperationFilter - CONTAINS
                arrayOf(
                    BinaryOperationFilter(
                        FilterOperator.CONTAINS,
                        ActivitiesFilterField.FilterTags,
                        "tag1",
                    ),
                    mapOf("filter_tags" to mapOf("\$contains" to "tag1")),
                    "BinaryOperationFilter with CONTAINS operator",
                ),

                // BinaryOperationFilter - PATH_EXISTS
                arrayOf(
                    BinaryOperationFilter(
                        FilterOperator.PATH_EXISTS,
                        ActivitiesFilterField.SearchData,
                        "user.profile",
                    ),
                    mapOf("search_data" to mapOf("\$path_exists" to "user.profile")),
                    "BinaryOperationFilter with PATH_EXISTS operator",
                ),

                // CollectionOperationFilter - AND
                arrayOf(
                    CollectionOperationFilter(
                        FilterOperator.AND,
                        setOf(
                            BinaryOperationFilter(FilterOperator.EQUAL, testField, "test1"),
                            BinaryOperationFilter(
                                FilterOperator.GREATER,
                                testCreatedAtField,
                                1234567890,
                            ),
                        ),
                    ),
                    mapOf(
                        "\$and" to
                            listOf(
                                mapOf("id" to mapOf("\$eq" to "test1")),
                                mapOf("created_at" to mapOf("\$gt" to 1234567890)),
                            )
                    ),
                    "CollectionOperationFilter with AND operator",
                ),

                // CollectionOperationFilter - OR
                arrayOf(
                    CollectionOperationFilter(
                        FilterOperator.OR,
                        setOf(
                            BinaryOperationFilter(FilterOperator.EQUAL, testTextField, "content1"),
                            BinaryOperationFilter(FilterOperator.EQUAL, testTextField, "content2"),
                        ),
                    ),
                    mapOf(
                        "\$or" to
                            listOf(
                                mapOf("text" to mapOf("\$eq" to "content1")),
                                mapOf("text" to mapOf("\$eq" to "content2")),
                            )
                    ),
                    "CollectionOperationFilter with OR operator",
                ),

                // Nested CollectionOperationFilter
                arrayOf(
                    CollectionOperationFilter(
                        FilterOperator.AND,
                        setOf(
                            BinaryOperationFilter(FilterOperator.EQUAL, testField, "main-id"),
                            CollectionOperationFilter(
                                FilterOperator.OR,
                                setOf(
                                    BinaryOperationFilter(
                                        FilterOperator.EQUAL,
                                        testTextField,
                                        "option1",
                                    ),
                                    BinaryOperationFilter(
                                        FilterOperator.EQUAL,
                                        testTextField,
                                        "option2",
                                    ),
                                ),
                            ),
                        ),
                    ),
                    mapOf(
                        "\$and" to
                            listOf(
                                mapOf("id" to mapOf("\$eq" to "main-id")),
                                mapOf(
                                    "\$or" to
                                        listOf(
                                            mapOf("text" to mapOf("\$eq" to "option1")),
                                            mapOf("text" to mapOf("\$eq" to "option2")),
                                        )
                                ),
                            )
                    ),
                    "Nested CollectionOperationFilter with AND containing OR",
                ),
            )
    }
}
