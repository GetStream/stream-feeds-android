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

package io.getstream.feeds.android.client.internal.state.query

import io.getstream.android.core.api.filter.Filter
import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilterField
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class FilterUtilsTest(
    private val testName: String,
    private val input: ModelUpdates<ActivityData>,
    private val filter: Filter<ActivityData, ActivitiesFilterField>?,
    private val expected: ModelUpdates<ActivityData>,
) {
    @Test
    fun applyFilter() {
        val result = input.applyFilter(filter, ActivityData::id)

        assertEquals(expected, result)
    }

    companion object {
        private val filter = ActivitiesFilterField.type.equal("post")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<out Any?>> =
            listOf(
                arrayOf(
                    "when filter is null, return unchanged ModelUpdates",
                    ModelUpdates(
                        added = listOf(activityData(id = "activity-1", type = "post")),
                        updated = listOf(activityData(id = "activity-2", type = "story")),
                        removedIds = setOf("activity-3"),
                    ),
                    null,
                    ModelUpdates(
                        added = listOf(activityData(id = "activity-1", type = "post")),
                        updated = listOf(activityData(id = "activity-2", type = "story")),
                        removedIds = setOf("activity-3"),
                    ),
                ),
                arrayOf(
                    "when added items do not match filter, filter out non-matching items",
                    ModelUpdates(
                        added =
                            listOf(
                                activityData(id = "activity-1", type = "post"),
                                activityData(id = "activity-2", type = "story"),
                            ),
                        updated = emptyList(),
                        removedIds = emptySet(),
                    ),
                    filter,
                    ModelUpdates(
                        added = listOf(activityData(id = "activity-1", type = "post")),
                        updated = emptyList(),
                        removedIds = emptySet(),
                    ),
                ),
                arrayOf(
                    "when updated items do not match filter, move non-matching to removedIds",
                    ModelUpdates(
                        added = emptyList(),
                        updated =
                            listOf(
                                activityData(id = "activity-1", type = "post"),
                                activityData(id = "activity-2", type = "story"),
                            ),
                        removedIds = emptySet(),
                    ),
                    filter,
                    ModelUpdates(
                        added = emptyList(),
                        updated = listOf(activityData(id = "activity-1", type = "post")),
                        removedIds = setOf("activity-2"),
                    ),
                ),
                arrayOf(
                    "when removedIds already exist, preserve existing removedIds",
                    ModelUpdates(
                        added = emptyList(),
                        updated =
                            listOf(
                                activityData(id = "activity-1", type = "post"),
                                activityData(id = "activity-2", type = "story"),
                            ),
                        removedIds = setOf("activity-3"),
                    ),
                    filter,
                    ModelUpdates(
                        added = emptyList(),
                        updated = listOf(activityData(id = "activity-1", type = "post")),
                        removedIds = setOf("activity-2", "activity-3"),
                    ),
                ),
            )
    }
}
