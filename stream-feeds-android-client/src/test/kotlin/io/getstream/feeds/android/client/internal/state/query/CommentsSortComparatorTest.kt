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

import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentsSortComparatorTest {

    @Test
    fun `on Controversial, sort by descending controversy score with missing scores last`() {
        val noScore = commentData("no-score")
        val low = commentData("low").copy(controversyScore = 0.2)
        val high = commentData("high").copy(controversyScore = 0.8)

        val sorted =
            listOf(noScore, low, high).sortedWith(CommentsSort.Controversial.toComparator())

        assertEquals(listOf(high, low, noScore), sorted)
    }

    @Test
    fun `on Best, sort by descending confidence score`() {
        val low = commentData("low").copy(confidenceScore = 0.2)
        val high = commentData("high").copy(confidenceScore = 0.8)

        val sorted = listOf(low, high).sortedWith(CommentsSort.Best.toComparator())

        assertEquals(listOf(high, low), sorted)
    }
}
