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

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilterField
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
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

        val result =
            createGetOrCreateInfo(
                activities = activities,
                feed = feed,
                followers = followers,
                following = following,
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
        val newPaginationResult =
            PaginationResult(
                models = newActivities,
                pagination = PaginationData(next = "next-cursor-2", previous = null),
            )

        feedState.onQueryMoreActivities(newPaginationResult, createQueryConfig())

        assertEquals(3, feedState.activities.value.size)
        assertEquals("next-cursor-2", feedState.activitiesPagination?.next)
    }

    @Test
    fun `on onActivityAdded, then add activity`() = runTest {
        val initialActivity = activityData()
        setupInitialState(listOf(initialActivity))

        val newActivity = activityData("activity-2")
        feedState.onActivityAdded(newActivity)

        val activities = feedState.activities.value
        assertEquals(listOf(initialActivity, newActivity), activities)
    }

    @Test
    fun `on onActivityUpdated, then update activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onActivityUpdated(updatedActivity)

        assertEquals(listOf(updatedActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve ownBookmarks when updating activity`() = runTest {
        val initialBookmark = bookmarkData("activity-1", currentUserId)
        val initialActivity =
            activityData("activity-1", text = "Original", ownBookmarks = listOf(initialBookmark))
        setupInitialState(listOf(initialActivity))

        val updatedActivity =
            activityData("activity-1", text = "Updated", ownBookmarks = emptyList())
        feedState.onActivityUpdated(updatedActivity)

        val expectedActivity = updatedActivity.copy(ownBookmarks = listOf(initialBookmark))
        assertEquals(listOf(expectedActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve ownReactions when updating activity`() = runTest {
        val initialReaction = feedsReactionData("activity-1", "like", currentUserId)
        val initialActivity =
            activityData("activity-1", text = "Original", ownReactions = listOf(initialReaction))
        setupInitialState(listOf(initialActivity))

        val updatedActivity =
            activityData("activity-1", text = "Updated", ownReactions = emptyList())
        feedState.onActivityUpdated(updatedActivity)

        val expectedActivity = updatedActivity.copy(ownReactions = listOf(initialReaction))
        assertEquals(listOf(expectedActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve poll ownVotes when updating activity`() = runTest {
        val ownVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(ownVote))
        val initialActivity = activityData("activity-1", text = "Original", poll = initialPoll)
        setupInitialState(listOf(initialActivity))

        val updatedPoll = pollData("poll-1", "Updated Poll", ownVotes = emptyList())
        val updatedActivity = activityData("activity-1", text = "Updated", poll = updatedPoll)
        feedState.onActivityUpdated(updatedActivity)

        val expectedPoll = updatedPoll.copy(ownVotes = listOf(ownVote))
        val expectedActivity = updatedActivity.copy(poll = expectedPoll)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve all own properties together in feed`() = runTest {
        val initialBookmark = bookmarkData("activity-1", currentUserId)
        val initialReaction = feedsReactionData("activity-1", "like", currentUserId)
        val ownVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(ownVote))
        val initialActivity =
            activityData(
                "activity-1",
                text = "Original",
                poll = initialPoll,
                ownBookmarks = listOf(initialBookmark),
                ownReactions = listOf(initialReaction),
            )
        setupInitialState(listOf(initialActivity))

        // Backend sends update with empty "own" properties
        val updatedPoll = pollData("poll-1", "Updated Poll", ownVotes = emptyList())
        val updatedActivity =
            activityData(
                "activity-1",
                text = "Updated",
                poll = updatedPoll,
                ownBookmarks = emptyList(),
                ownReactions = emptyList(),
            )
        feedState.onActivityUpdated(updatedActivity)

        // Verify all "own" properties are preserved
        val expectedPoll = updatedPoll.copy(ownVotes = listOf(ownVote))
        val expectedActivity =
            updatedActivity.copy(
                poll = expectedPoll,
                ownBookmarks = listOf(initialBookmark),
                ownReactions = listOf(initialReaction),
            )
        assertEquals(listOf(expectedActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityUpdated with new poll in feed, then set new poll`() = runTest {
        val initialActivity = activityData("activity-1", text = "Original", poll = null)
        setupInitialState(listOf(initialActivity))

        val newPoll = pollData("poll-1", "New Poll")
        val updatedActivity = activityData("activity-1", text = "Updated", poll = newPoll)
        feedState.onActivityUpdated(updatedActivity)

        assertEquals(listOf(updatedActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityRemoved, then remove activity`() = runTest {
        val initialActivities = listOf(activityData("activity-1"), activityData("activity-2"))
        setupInitialState(initialActivities)

        feedState.onActivityRemoved("activity-1")

        val activities = feedState.activities.value
        assertEquals(initialActivities.drop(1), activities)
    }

    @Test
    fun `on onBookmarkUpserted, then add bookmark to activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val bookmark = bookmarkData("activity-1", currentUserId)
        feedState.onBookmarkUpserted(bookmark)

        val expected = bookmark.activity.copy(ownBookmarks = listOf(bookmark))
        assertEquals(listOf(expected), feedState.activities.value)
    }

    @Test
    fun `on onBookmarkRemoved, then remove bookmark from activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val bookmark = bookmarkData("activity-1", currentUserId)
        feedState.onBookmarkUpserted(bookmark)
        feedState.onBookmarkRemoved(bookmark)

        val activityWithoutBookmark = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(0, activityWithoutBookmark?.bookmarkCount)
    }

    @Test
    fun `on onCommentAdded, then add comment to activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val comment = commentData("comment-1", objectId = "activity-1")
        feedState.onCommentAdded(comment)

        val activityWithComment = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(1, activityWithComment?.commentCount)
    }

    @Test
    fun `on onCommentRemoved, then remove comment from activity`() = runTest {
        setupInitialState(listOf(activityData("activity-1")))

        val comment = commentData("comment-1", objectId = "activity-1")
        feedState.onCommentAdded(comment)
        feedState.onCommentRemoved(comment)

        val activityWithoutComment = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(0, activityWithoutComment?.commentCount)
    }

    @Test
    fun `on onCommentReactionRemoved, then remove comment reaction from activity`() = runTest {
        val reaction = feedsReactionData(commentId = "comment-1")
        val comment =
            commentData("comment-1", objectId = "activity-1", ownReactions = listOf(reaction))
        val activity = activityData("activity-1").copy(comments = listOf(comment))
        setupInitialState(listOf(activity))

        val updatedComment = commentData("comment-1", objectId = "activity-1", text = "Updated")
        feedState.onCommentReactionUpserted(updatedComment, reaction)
        feedState.onCommentReactionRemoved(updatedComment, reaction)

        val expectedActivity =
            activity.copy(comments = listOf(updatedComment.copy(ownReactions = emptyList())))
        assertEquals(listOf(expectedActivity), feedState.activities.value)
    }

    @Test
    fun `on onCommentReactionUpserted, then upsert comment reaction in activity`() = runTest {
        val comment = commentData("comment-1", objectId = "activity-1")
        val activity = activityData("activity-1").copy(comments = listOf(comment))
        setupInitialState(listOf(activity))

        val reaction = feedsReactionData(commentId = "comment-1")
        val updatedComment = commentData("comment-1", objectId = "activity-1", text = "Updated")
        feedState.onCommentReactionUpserted(updatedComment, reaction)

        val expectedActivity =
            activity.copy(comments = listOf(updatedComment.copy(ownReactions = listOf(reaction))))
        assertEquals(listOf(expectedActivity), feedState.activities.value)
    }

    @Test
    fun `on onReactionUpserted, then add reaction to activity`() = runTest {
        val activity = activityData("activity-1")
        setupInitialState(listOf(activity))

        val reaction = feedsReactionData("activity-1", currentUserId)
        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onReactionUpserted(reaction, updatedActivity)

        val expected = updatedActivity.copy(ownReactions = listOf(reaction))
        assertEquals(listOf(expected), feedState.activities.value)
    }

    @Test
    fun `on onReactionRemoved, then remove reaction from activity`() = runTest {
        val reaction = feedsReactionData("activity-1", currentUserId)
        val activity = activityData("activity-1", ownReactions = listOf(reaction))
        setupInitialState(listOf(activity))

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onReactionRemoved(reaction, updatedActivity)

        val expected = updatedActivity.copy(ownReactions = emptyList())
        assertEquals(listOf(expected), feedState.activities.value)
    }

    @Test
    fun `on onFeedUpdated, then update feed`() = runTest {
        val initialFeed = feedData()
        setupInitialState(emptyList(), feed = initialFeed)

        val updatedFeed = feedData("feed-1", "user", "Updated Feed")
        feedState.onFeedUpdated(updatedFeed)

        assertEquals(updatedFeed, feedState.feed.value)
    }

    @Test
    fun `on onFeedDeleted, then clear all state`() = runTest {
        setupInitialState(
            activities = listOf(activityData()),
            followers = listOf(followData()),
            following = listOf(followData()),
            followRequests = listOf(followData()),
        )

        feedState.onFeedDeleted()

        assertEquals(emptyList<ActivityData>(), feedState.activities.value)
        assertNull(feedState.feed.value)
        assertEquals(emptyList<FollowData>(), feedState.followers.value)
        assertEquals(emptyList<FollowData>(), feedState.following.value)
        assertEquals(emptyList<FollowData>(), feedState.followRequests.value)
    }

    @Test
    fun `on activityAdded with matching filter, then add activity`() = runTest {
        val filter = ActivitiesFilterField.type.equal("post")
        val feedStateWithFilter =
            FeedStateImpl(
                feedQuery.copy(activityFilter = filter),
                currentUserId,
                mockMemberListState,
            )

        val matchingActivity = activityData("activity-1", type = "post")
        feedStateWithFilter.onActivityAdded(matchingActivity)

        val activities = feedStateWithFilter.activities.value
        assertEquals(listOf(matchingActivity), activities)
    }

    @Test
    fun `on activityAdded with non-matching filter, then do not add activity`() = runTest {
        val filter = ActivitiesFilterField.type.equal("comment")
        val feedStateWithFilter =
            FeedStateImpl(
                feedQuery.copy(activityFilter = filter),
                currentUserId,
                mockMemberListState,
            )

        val nonMatchingActivity = activityData("activity-1", type = "post")
        feedStateWithFilter.onActivityAdded(nonMatchingActivity)

        val activities = feedStateWithFilter.activities.value
        assertEquals(emptyList<ActivityData>(), activities)
    }

    @Test
    fun `on activityUpdated with matching filter, then update activity`() = runTest {
        val filter = ActivitiesFilterField.type.equal("post")
        val feedStateWithFilter =
            FeedStateImpl(
                feedQuery.copy(activityFilter = filter),
                currentUserId,
                mockMemberListState,
            )

        val initialActivity = activityData("activity-1", type = "post")
        feedStateWithFilter.onActivityAdded(initialActivity)

        val updatedActivity = activityData("activity-1", type = "post", text = "Updated text")
        feedStateWithFilter.onActivityUpdated(updatedActivity)

        val activities = feedStateWithFilter.activities.value
        assertEquals(listOf(updatedActivity), activities)
    }

    @Test
    fun `on activityUpdated with non-matching filter, then do not update activity`() = runTest {
        val filter = ActivitiesFilterField.type.equal("comment")
        val feedStateWithFilter =
            FeedStateImpl(
                feedQuery.copy(activityFilter = filter),
                currentUserId,
                mockMemberListState,
            )

        setupInitialState(listOf(activityData("activity-1", type = "post")))

        val updatedActivity = activityData("activity-1", type = "post", text = "Updated text")
        feedStateWithFilter.onActivityUpdated(updatedActivity)

        val activities = feedStateWithFilter.activities.value
        assertEquals(emptyList<ActivityData>(), activities)
    }

    @Test
    fun `on onPollClosed, then mark poll as closed in activities`() = runTest {
        val poll = pollData("poll-1", "Test Poll", isClosed = false)
        val activity = setupActivityWithPoll(poll)

        feedState.onPollClosed("poll-1")

        expectActivityWithPoll(activity, poll.copy(isClosed = true))
    }

    @Test
    fun `on onPollDeleted, then remove poll from activities`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity = setupActivityWithPoll(poll)

        feedState.onPollDeleted("poll-1")

        expectActivityWithPoll(activity, null)
    }

    @Test
    fun `on onPollUpdated, then preserve own votes when updating poll in activities`() = runTest {
        val ownVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val poll = pollData("poll-1", "Test Poll", ownVotes = listOf(ownVote))
        val activity = setupActivityWithPoll(poll)

        val updatedPoll = pollData("poll-1", "Updated Poll", ownVotes = emptyList())
        feedState.onPollUpdated(updatedPoll)

        expectActivityWithPoll(activity, updatedPoll.copy(ownVotes = listOf(ownVote)))
    }

    @Test
    fun `on onPollVoteCasted, then update poll with new vote in activities`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity = setupActivityWithPoll(poll)
        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)

        feedState.onPollVoteCasted(vote, "poll-1")

        val expectedPoll =
            poll.copy(
                voteCount = 1,
                ownVotes = listOf(vote),
                latestVotesByOption = mapOf("option-1" to listOf(vote)),
                voteCountsByOption = mapOf("option-1" to 1),
            )
        expectActivityWithPoll(activity, expectedPoll)
    }

    @Test
    fun `on onPollVoteChanged, then update poll with changed vote in activities`() = runTest {
        val originalVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val poll = pollWithVote("poll-1", originalVote)
        val activity = setupActivityWithPoll(poll)
        val changedVote = pollVoteData("vote-1", "poll-1", "option-2", currentUserId)

        feedState.onPollVoteChanged(changedVote, "poll-1")

        val expectedPoll =
            poll.copy(
                voteCount = 1,
                ownVotes = listOf(changedVote),
                latestVotesByOption =
                    mapOf("option-1" to emptyList(), "option-2" to listOf(changedVote)),
                voteCountsByOption = mapOf("option-1" to 0, "option-2" to 1),
            )
        expectActivityWithPoll(activity, expectedPoll)
    }

    @Test
    fun `on onPollVoteRemoved, then remove vote from poll in activities`() = runTest {
        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val poll = pollWithVote("poll-1", vote)
        val activity = setupActivityWithPoll(poll)

        feedState.onPollVoteRemoved(vote, "poll-1")

        val expectedPoll =
            poll.copy(
                voteCount = 0,
                ownVotes = emptyList(),
                latestVotesByOption = mapOf("option-1" to emptyList()),
                voteCountsByOption = mapOf("option-1" to 0),
            )
        expectActivityWithPoll(activity, expectedPoll)
    }

    @Test
    fun `on poll events with different poll id, then keep existing polls unchanged`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity = setupActivityWithPoll(poll)
        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)

        feedState.onPollVoteCasted(vote, "poll-2")

        expectActivityWithPoll(activity, poll)
    }

    // Helper functions
    private fun setupInitialState(
        activities: List<ActivityData> = listOf(activityData("activity-1")),
        feed: FeedData = feedData(),
        followers: List<FollowData> = emptyList(),
        following: List<FollowData> = emptyList(),
        followRequests: List<FollowData> = emptyList(),
    ) {
        val result =
            createGetOrCreateInfo(
                activities = activities,
                feed = feed,
                followers = followers,
                following = following,
                followRequests = followRequests,
            )
        feedState.onQueryFeed(result)
    }

    private fun createGetOrCreateInfo(
        activities: List<ActivityData>,
        feed: FeedData = feedData(),
        followers: List<FollowData> = emptyList(),
        following: List<FollowData> = emptyList(),
        followRequests: List<FollowData> = emptyList(),
    ): GetOrCreateInfo {
        val paginationResult =
            PaginationResult(
                models = activities,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = createQueryConfig()

        return GetOrCreateInfo(
            activities = paginationResult,
            activitiesQueryConfig = queryConfig,
            feed = feed,
            followers = followers,
            following = following,
            followRequests = followRequests,
            pinnedActivities = emptyList(),
            aggregatedActivities = emptyList(),
            notificationStatus = null,
            members =
                PaginationResult(
                    models = emptyList(),
                    pagination = PaginationData(next = null, previous = null),
                ),
        )
    }

    private fun createQueryConfig() =
        ActivitiesQueryConfig(filter = null, sort = ActivitiesSort.Default)

    private fun setupActivityWithPoll(poll: PollData): ActivityData {
        val activity = activityData("activity-1", poll = poll)
        setupInitialState(listOf(activity))
        return activity
    }

    private fun expectActivityWithPoll(activity: ActivityData, expectedPoll: PollData?) {
        val expectedActivity = activity.copy(poll = expectedPoll)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
    }

    private fun pollWithVote(pollId: String, vote: PollVoteData): PollData =
        pollData(
            pollId,
            "Test Poll",
            voteCount = 1,
            ownVotes = listOf(vote),
            latestVotesByOption = mapOf(vote.optionId to listOf(vote)),
            voteCountsByOption = mapOf(vote.optionId to 1),
        )
}
