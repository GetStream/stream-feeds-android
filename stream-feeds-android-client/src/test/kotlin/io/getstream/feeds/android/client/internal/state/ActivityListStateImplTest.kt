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
package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class ActivityListStateImplTest {
    private val query = ActivitiesQuery(limit = 10)
    private val currentUserId = "user-1"
    private val activityListState = ActivityListStateImpl(query, currentUserId)

    @Test
    fun `on initial state, then return empty activities and null pagination`() = runTest {
        assertEquals(emptyList<ActivityData>(), activityListState.activities.value)
        assertNull(activityListState.pagination)
    }

    @Test
    fun `on queryMoreActivities, then update activities and pagination`() = runTest {
        val activities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = activities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)

        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        assertEquals(activities, activityListState.activities.value)
        assertEquals("next-cursor", activityListState.pagination?.next)
        assertEquals(queryConfig, activityListState.queryConfig)
    }

    @Test
    fun `on activityUpdated, then update specific activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        activityListState.onActivityUpdated(updatedActivity)

        val updatedActivities = activityListState.activities.value
        assertEquals(listOf(updatedActivity, initialActivities[1]), updatedActivities)
    }

    @Test
    fun `on activityRemoved, then remove specific activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        activityListState.onActivityRemoved(initialActivities[0])

        val remainingActivities = activityListState.activities.value
        assertEquals(listOf(initialActivities[1]), remainingActivities)
    }

    @Test
    fun `on bookmarkAdded, then add bookmark to activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        val bookmark = bookmarkData("activity-1", currentUserId)
        activityListState.onBookmarkAdded(bookmark)

        val activityWithBookmark = activityListState.activities.value.first()
        assertEquals(1, activityWithBookmark.bookmarkCount)
    }

    @Test
    fun `on bookmarkRemoved, then remove bookmark from activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        val bookmark = bookmarkData("activity-1", currentUserId)
        activityListState.onBookmarkAdded(bookmark)
        activityListState.onBookmarkRemoved(bookmark)

        val activityWithoutBookmark = activityListState.activities.value.first()
        assertEquals(0, activityWithoutBookmark.bookmarkCount)
    }

    @Test
    fun `on commentAdded, then add comment to activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        val comment = commentData("comment-1", objectId = "activity-1")
        activityListState.onCommentAdded(comment)

        val activityWithComment = activityListState.activities.value.first()
        assertEquals(1, activityWithComment.commentCount)
    }

    @Test
    fun `on commentRemoved, then remove comment from activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        val comment = commentData("comment-1", objectId = "activity-1")
        activityListState.onCommentAdded(comment)
        activityListState.onCommentRemoved(comment)

        val activityWithoutComment = activityListState.activities.value.first()
        assertEquals(0, activityWithoutComment.commentCount)
    }

    @Test
    fun `on reactionAdded, then add reaction to activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        val reaction = feedsReactionData("activity-1", "like", currentUserId)
        activityListState.onReactionAdded(reaction)

        val activityWithReaction = activityListState.activities.value.first()
        assertEquals(1, activityWithReaction.reactionCount)
    }

    @Test
    fun `on reactionRemoved, then remove reaction from activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        val reaction = feedsReactionData("activity-1", "like", currentUserId)
        activityListState.onReactionAdded(reaction)
        activityListState.onReactionRemoved(reaction)

        val updatedActivities = activityListState.activities.value
        assertEquals(0, updatedActivities.first().reactionCount)
    }
}
