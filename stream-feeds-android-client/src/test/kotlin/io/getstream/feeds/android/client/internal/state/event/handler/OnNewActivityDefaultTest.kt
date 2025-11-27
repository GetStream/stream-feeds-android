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

package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.InsertionAction
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilterField
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.userData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class OnNewActivityDefaultTest(
    private val testName: String,
    private val query: FeedQuery,
    private val activity: ActivityData,
    private val currentUserId: String,
    private val expectedAction: InsertionAction,
) {
    @Test
    fun `on defaultOnNewActivity, return expected action`() {
        val result = defaultOnNewActivity(query, activity, currentUserId)

        assertEquals(testName, expectedAction, result)
    }

    companion object {
        private val feedId = FeedId("group", "feed-1")
        private const val currentUserId = "current-user"
        private const val otherUserId = "other-user"
        private val filter = ActivitiesFilterField.type.equal("post")
        private val matchingActivity = activityData(type = "post", user = userData(currentUserId))
        private val nonMatchingUserActivity =
            activityData(type = "post", user = userData(otherUserId))
        private val nonMatchingFilterActivity =
            activityData(type = "comment", user = userData(currentUserId))
        private val nonMatchingActivity =
            activityData(type = "comment", user = userData(otherUserId))

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                arrayOf(
                    "should return AddToStart when matching user and matching filter",
                    FeedQuery(fid = feedId, activityFilter = filter),
                    matchingActivity,
                    currentUserId,
                    InsertionAction.AddToStart,
                ),
                arrayOf(
                    "should return AddToStart when matching user and null filter",
                    FeedQuery(fid = feedId, activityFilter = null),
                    matchingActivity,
                    currentUserId,
                    InsertionAction.AddToStart,
                ),
                arrayOf(
                    "should return Ignore when matching user but non-matching filter",
                    FeedQuery(fid = feedId, activityFilter = filter),
                    nonMatchingFilterActivity,
                    currentUserId,
                    InsertionAction.Ignore,
                ),
                arrayOf(
                    "should return Ignore when non-matching user but matching filter",
                    FeedQuery(fid = feedId, activityFilter = filter),
                    nonMatchingUserActivity,
                    currentUserId,
                    InsertionAction.Ignore,
                ),
                arrayOf(
                    "should return Ignore when non-matching user and non-matching filter",
                    FeedQuery(fid = feedId, activityFilter = filter),
                    nonMatchingActivity,
                    currentUserId,
                    InsertionAction.Ignore,
                ),
                arrayOf(
                    "should return Ignore when non-matching user and null filter",
                    FeedQuery(fid = feedId, activityFilter = null),
                    nonMatchingUserActivity,
                    currentUserId,
                    InsertionAction.Ignore,
                ),
            )
    }
}
