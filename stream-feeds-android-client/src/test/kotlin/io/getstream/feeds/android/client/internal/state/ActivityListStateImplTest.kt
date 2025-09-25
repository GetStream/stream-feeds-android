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
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
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
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")

        val paginationResult = defaultPaginationResult(listOf(activity1, activity2))
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)

        assertEquals(listOf(activity1, activity2), activityListState.activities.value)
        assertEquals("next-cursor", activityListState.pagination?.next)
        assertEquals(queryConfig, activityListState.queryConfig)
    }

    @Test
    fun `on onActivityUpdated, then update specific activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        activityListState.onActivityUpdated(updatedActivity)

        assertEquals(listOf(updatedActivity, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onActivityRemoved, then remove specific activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        activityListState.onActivityRemoved(activity1.id)

        assertEquals(listOf(activity2), activityListState.activities.value)
    }

    @Test
    fun `on onBookmarkUpserted, then add bookmark to activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val bookmark = bookmarkData("activity-1", currentUserId)
        val expected = bookmark.activity.copy(ownBookmarks = listOf(bookmark))

        activityListState.onBookmarkUpserted(bookmark)

        assertEquals(listOf(expected, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onBookmarkRemoved, then remove bookmark from activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val bookmark = bookmarkData("activity-1", currentUserId)
        activityListState.onBookmarkUpserted(bookmark)
        activityListState.onBookmarkRemoved(bookmark)

        val activityWithoutBookmark = activityListState.activities.value.first()
        assertEquals(0, activityWithoutBookmark.bookmarkCount)
    }

    @Test
    fun `on onCommentAdded, then add comment to activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val comment = commentData("comment-1", objectId = "activity-1")
        activityListState.onCommentAdded(comment)

        val activityWithComment = activityListState.activities.value.first()
        assertEquals(1, activityWithComment.commentCount)
    }

    @Test
    fun `on onCommentRemoved, then remove comment from activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val comment = commentData("comment-1", objectId = "activity-1")
        activityListState.onCommentAdded(comment)
        activityListState.onCommentRemoved(comment)

        val activityWithoutComment = activityListState.activities.value.first()
        assertEquals(0, activityWithoutComment.commentCount)
    }

    @Test
    fun `on onReactionUpserted, then add reaction to activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val reaction = feedsReactionData("activity-1", "like", currentUserId)
        val updatedActivity = activityData("activity-1", text = "Updated activity")
        activityListState.onReactionUpserted(reaction, updatedActivity)

        val expected = updatedActivity.copy(ownReactions = listOf(reaction))
        assertEquals(listOf(expected, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onReactionRemoved, then remove reaction from activity`() = runTest {
        val reaction = feedsReactionData("activity-1", "like", currentUserId)
        val activity1 = activityData("activity-1", ownReactions = listOf(reaction))
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        activityListState.onReactionRemoved(reaction, updatedActivity)

        val expected = updatedActivity.copy(ownReactions = emptyList())
        assertEquals(listOf(expected, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onCommentReactionRemoved, then remove comment reaction from activity`() = runTest {
        val reaction = feedsReactionData(commentId = "comment-1", userId = currentUserId)
        val comment =
            commentData("comment-1", objectId = "activity-1", ownReactions = listOf(reaction))
        val activity1 = activityData("activity-1", comments = listOf(comment))
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val updatedComment = commentData("comment-1", objectId = "activity-1", text = "Updated")
        activityListState.onCommentReactionUpserted(updatedComment, reaction)
        activityListState.onCommentReactionRemoved(updatedComment, reaction)

        val expectedComment = updatedComment.copy(ownReactions = emptyList())
        val expectedActivity = activity1.copy(comments = listOf(expectedComment))
        assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onCommentReactionUpserted, then upsert comment reaction in activity`() = runTest {
        val comment = commentData("comment-1", objectId = "activity-1")
        val activity1 = activityData("activity-1", comments = listOf(comment))
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val reaction = feedsReactionData(commentId = "comment-1", userId = currentUserId)
        val updatedComment = commentData("comment-1", objectId = "activity-1", text = "Updated")
        activityListState.onCommentReactionUpserted(updatedComment, reaction)

        val expectedComment = updatedComment.copy(ownReactions = listOf(reaction))
        val expectedActivity = activity1.copy(comments = listOf(expectedComment))
        assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
    }

    private fun setupInitialActivities(vararg activities: ActivityData) {
        val paginationResult = defaultPaginationResult(activities.toList())
        activityListState.onQueryMoreActivities(paginationResult, queryConfig)
    }

    companion object {
        private val queryConfig =
            ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)
    }
}
