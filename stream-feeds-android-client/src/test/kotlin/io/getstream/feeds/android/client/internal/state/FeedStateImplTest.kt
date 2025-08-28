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
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class FeedStateImplTest {
    private val currentUserId = "user-1"
    private val feedId = FeedId("user:test")
    private val feedQuery = FeedQuery(fid = feedId)
    private val mockMemberListState: MemberListMutableState = mockk(relaxed = true)
    private val feedState = FeedStateImpl(feedQuery, currentUserId, mockMemberListState)

    @Test
    fun `on initial state, then return empty state`() = runTest {
        assertEquals(emptyList<ActivityData>(), feedState.activities.value)
        assertEquals(emptyList<FollowData>(), feedState.followers.value)
        assertEquals(emptyList<FollowData>(), feedState.following.value)
        assertEquals(emptyList<FollowData>(), feedState.followRequests.value)
        assertNull(feedState.feed.value)
        assertNull(feedState.activitiesPagination)
    }

    @Test
    fun `on queryFeed, then update all state`() = runTest {
        val activities = listOf(activityData(), activityData("activity-2"))
        val feed = feedData()
        val followers = listOf(followData())
        val following = listOf(followData("user-2", "user-3"))

        val result = createGetOrCreateInfo(
            activities = activities,
            feed = feed,
            followers = followers,
            following = following
        )

        feedState.onQueryFeed(result)

        assertEquals(activities, feedState.activities.value)
        assertEquals(feed, feedState.feed.value)
        assertEquals(followers, feedState.followers.value)
        assertEquals(following, feedState.following.value)
        assertEquals("next-cursor", feedState.activitiesPagination?.next)
    }

    @Test
    fun `on queryMoreActivities, then merge activities`() = runTest {
        val initialActivities = listOf(activityData())
        setupInitialState(initialActivities)

        val newActivities = listOf(activityData("activity-2"), activityData("activity-3"))
        val newPaginationResult = PaginationResult(
            models = newActivities,
            pagination = PaginationData(next = "next-cursor-2", previous = null)
        )

        feedState.onQueryMoreActivities(newPaginationResult, createQueryConfig())

        assertEquals(3, feedState.activities.value.size)
        assertEquals("next-cursor-2", feedState.activitiesPagination?.next)
    }

    @Test
    fun `on activityAdded, then add activity`() = runTest {
        val initialActivity = activityData()
        setupInitialState(listOf(initialActivity))

        val newActivity = activityData("activity-2")
        feedState.onActivityAdded(newActivity)

        val activities = feedState.activities.value
        assertEquals(listOf(initialActivity, newActivity), activities)
    }

    @Test
    fun `on activityUpdated, then update activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onActivityUpdated(updatedActivity)

        val activities = feedState.activities.value
        assertEquals(listOf(updatedActivity), activities)
    }

    @Test
    fun `on activityRemoved, then remove activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        setupInitialState(initialActivities)

        feedState.onActivityRemoved("activity-1")

        val activities = feedState.activities.value
        assertEquals(initialActivities.drop(1), activities)
    }

    @Test
    fun `on bookmarkAdded, then add bookmark to activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val bookmark = bookmarkData("activity-1", currentUserId)
        feedState.onBookmarkAdded(bookmark)

        val activityWithBookmark = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(1, activityWithBookmark?.bookmarkCount)
    }

    @Test
    fun `on bookmarkRemoved, then remove bookmark from activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val bookmark = bookmarkData("activity-1", currentUserId)
        feedState.onBookmarkAdded(bookmark)
        feedState.onBookmarkRemoved(bookmark)

        val activityWithoutBookmark = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(0, activityWithoutBookmark?.bookmarkCount)
    }

    @Test
    fun `on commentAdded, then add comment to activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val comment = commentData("comment-1", objectId = "activity-1")
        feedState.onCommentAdded(comment)

        val activityWithComment = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(1, activityWithComment?.commentCount)
    }

    @Test
    fun `on commentRemoved, then remove comment from activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val comment = commentData("comment-1", objectId = "activity-1")
        feedState.onCommentAdded(comment)
        feedState.onCommentRemoved(comment)

        val activityWithoutComment = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(0, activityWithoutComment?.commentCount)
    }

    @Test
    fun `on reactionAdded, then add reaction to activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val reaction = feedsReactionData("activity-1", currentUserId)
        feedState.onReactionAdded(reaction)

        val activityWithReaction = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(1, activityWithReaction?.reactionCount)
    }

    @Test
    fun `on reactionRemoved, then remove reaction from activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val reaction = feedsReactionData("activity-1", currentUserId)
        feedState.onReactionAdded(reaction)
        feedState.onReactionRemoved(reaction)

        val activityWithoutReaction = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(0, activityWithoutReaction?.reactionCount)
    }

    @Test
    fun `on feedUpdated, then update feed`() = runTest {
        val initialFeed = feedData()
        setupInitialState(emptyList(), feed = initialFeed)

        val updatedFeed = feedData("feed-1", "user", "Updated Feed")
        feedState.onFeedUpdated(updatedFeed)

        assertEquals(updatedFeed, feedState.feed.value)
    }

    @Test
    fun `on feedDeleted, then clear all state`() = runTest {
        setupInitialState(
            activities = listOf(activityData()),
            followers = listOf(followData()),
            following = listOf(followData()),
            followRequests = listOf(followData())
        )

        feedState.onFeedDeleted()

        assertEquals(emptyList<ActivityData>(), feedState.activities.value)
        assertNull(feedState.feed.value)
        assertEquals(emptyList<FollowData>(), feedState.followers.value)
        assertEquals(emptyList<FollowData>(), feedState.following.value)
        assertEquals(emptyList<FollowData>(), feedState.followRequests.value)
        assertEquals(emptyList<FeedOwnCapability>(), feedState.ownCapabilities.value)
    }

    // Helper functions
    private fun setupInitialState(
        activities: List<ActivityData> = listOf(activityData("activity-1")),
        feed: io.getstream.feeds.android.client.api.model.FeedData = feedData(),
        followers: List<FollowData> = emptyList(),
        following: List<FollowData> = emptyList(),
        followRequests: List<FollowData> = emptyList()
    ) {
        val result = createGetOrCreateInfo(
            activities = activities,
            feed = feed,
            followers = followers,
            following = following,
            followRequests = followRequests
        )
        feedState.onQueryFeed(result)
    }

    private fun createGetOrCreateInfo(
        activities: List<ActivityData>,
        feed: io.getstream.feeds.android.client.api.model.FeedData = feedData(),
        followers: List<FollowData> = emptyList(),
        following: List<FollowData> = emptyList(),
        followRequests: List<FollowData> = emptyList()
    ): GetOrCreateInfo {
        val paginationResult = PaginationResult(
            models = activities,
            pagination = PaginationData(next = "next-cursor", previous = null)
        )
        val queryConfig = createQueryConfig()

        return GetOrCreateInfo(
            activities = paginationResult,
            activitiesQueryConfig = queryConfig,
            feed = feed,
            followers = followers,
            following = following,
            followRequests = followRequests,
            ownCapabilities = emptyList(),
            pinnedActivities = emptyList(),
            aggregatedActivities = emptyList(),
            notificationStatus = null,
            members = PaginationResult(
                models = emptyList(),
                pagination = PaginationData(next = null, previous = null)
            )
        )
    }

    private fun createQueryConfig() = QueryConfiguration(
        filter = null,
        sort = ActivitiesSort.Default
    )
}
