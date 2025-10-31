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
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.activityPin
import io.getstream.feeds.android.client.internal.test.TestData.aggregatedActivityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.NotificationStatusResponse
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
    fun `on queryMoreActivities, merge with new activities and aggregated activities`() = runTest {
        val initialActivities = listOf(activityData("activity-1"))
        val initialAggregated =
            listOf(
                aggregatedActivityData(
                    group = "group-1",
                    activities = listOf(activityData("activity-1")),
                    activityCount = 1,
                )
            )
        setupInitialState(activities = initialActivities, aggregatedActivities = initialAggregated)

        val newActivities = listOf(activityData("activity-2"), activityData("activity-3"))
        val newAggregated =
            listOf(
                aggregatedActivityData(
                    group = "group-2",
                    activities = listOf(activityData("activity-2")),
                ),
                aggregatedActivityData(
                    group = "group-3",
                    activities = listOf(activityData("activity-3")),
                ),
            )
        val newPagination = PaginationData(next = "next-cursor-2", previous = null)

        feedState.onQueryMoreActivities(
            activities = newActivities,
            aggregatedActivities = newAggregated,
            pagination = newPagination,
            queryConfig = createQueryConfig(),
        )

        assertEquals(newPagination, feedState.activitiesPagination)
        assertEquals(initialActivities + newActivities, feedState.activities.value)
        assertEquals((initialAggregated + newAggregated), feedState.aggregatedActivities.value)
    }

    @Test
    fun `on onActivityUpserted, then add activity`() = runTest {
        val initialActivity = activityData()
        setupInitialState(listOf(initialActivity))

        val newActivity = activityData("activity-2")
        feedState.onActivityUpserted(newActivity)

        val activities = feedState.activities.value
        assertEquals(listOf(initialActivity, newActivity), activities)
    }

    @Test
    fun `on onActivityUpserted, then update activity`() = runTest {
        val initialActivity = activityData("activity-1")
        val activityPin = activityPin(initialActivity)
        setupInitialState(listOf(initialActivity), listOf(activityPin))

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onActivityUpserted(updatedActivity)

        assertEquals(listOf(updatedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = updatedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onActivityUpserted, then preserve own properties in activity`() = runTest {
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
        feedState.onActivityUpserted(updatedActivity)

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
    fun `on onActivityUpserted with new poll in feed, then set new poll`() = runTest {
        val initialActivity = activityData("activity-1", text = "Original", poll = null)
        setupInitialState(listOf(initialActivity))

        val newPoll = pollData("poll-1", "New Poll")
        val updatedActivity = activityData("activity-1", text = "Updated", poll = newPoll)
        feedState.onActivityUpserted(updatedActivity)

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
        val initialActivity = activityData("activity-1")
        val activityPin = activityPin(initialActivity)
        setupInitialState(listOf(initialActivity), listOf(activityPin))

        val bookmark = bookmarkData("activity-1", currentUserId)
        feedState.onBookmarkUpserted(bookmark)

        val expected = bookmark.activity.copy(ownBookmarks = listOf(bookmark))
        assertEquals(listOf(expected), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expected)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onBookmarkRemoved, then remove bookmark from activity`() = runTest {
        val initialActivity = activityData("activity-1")
        val activityPin = activityPin(initialActivity)
        setupInitialState(listOf(initialActivity), listOf(activityPin))

        val bookmark = bookmarkData("activity-1", currentUserId)
        feedState.onBookmarkUpserted(bookmark)
        feedState.onBookmarkRemoved(bookmark)

        val activityWithoutBookmark = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(0, activityWithoutBookmark?.bookmarkCount)
        val pinnedActivityWithoutBookmark =
            feedState.pinnedActivities.value.find { it.activity.id == "activity-1" }
        assertEquals(0, pinnedActivityWithoutBookmark?.activity?.bookmarkCount)
    }

    @Test
    fun `on onCommentAdded, then add comment to activity`() = runTest {
        val initialActivity = activityData("activity-1")
        val activityPin = activityPin(initialActivity)
        setupInitialState(listOf(initialActivity), listOf(activityPin))

        val comment = commentData("comment-1", objectId = "activity-1")
        feedState.onCommentUpserted(comment)

        val activityWithComment = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(1, activityWithComment?.commentCount)
        val pinnedActivityWithComment =
            feedState.pinnedActivities.value.find { it.activity.id == "activity-1" }
        assertEquals(1, pinnedActivityWithComment?.activity?.commentCount)
    }

    @Test
    fun `on onCommentRemoved, then remove comment from activity`() = runTest {
        val initialActivity = activityData("activity-1")
        val activityPin = activityPin(initialActivity)
        setupInitialState(listOf(initialActivity), listOf(activityPin))

        val comment = commentData("comment-1", objectId = "activity-1")
        feedState.onCommentUpserted(comment)
        feedState.onCommentRemoved(comment)

        val activityWithoutComment = feedState.activities.value.find { it.id == "activity-1" }
        assertEquals(0, activityWithoutComment?.commentCount)
        val pinnedActivityWithoutComment =
            feedState.pinnedActivities.value.find { it.activity.id == "activity-1" }
        assertEquals(0, pinnedActivityWithoutComment?.activity?.commentCount)
    }

    @Test
    fun `on onCommentReactionRemoved, then remove comment reaction from activity`() = runTest {
        val reaction = feedsReactionData(commentId = "comment-1")
        val comment =
            commentData("comment-1", objectId = "activity-1", ownReactions = listOf(reaction))
        val activity = activityData("activity-1").copy(comments = listOf(comment))
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))

        val updatedComment = commentData("comment-1", objectId = "activity-1", text = "Updated")
        feedState.onCommentReactionUpserted(updatedComment, reaction, enforceUnique = false)
        feedState.onCommentReactionRemoved(updatedComment, reaction)

        val expectedActivity =
            activity.copy(comments = listOf(updatedComment.copy(ownReactions = emptyList())))
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onCommentReactionUpserted, then upsert comment reaction in activity`() = runTest {
        val comment = commentData("comment-1", objectId = "activity-1")
        val activity = activityData("activity-1").copy(comments = listOf(comment))
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))

        val reaction = feedsReactionData(commentId = "comment-1")
        val updatedComment = commentData("comment-1", objectId = "activity-1", text = "Updated")
        feedState.onCommentReactionUpserted(updatedComment, reaction, enforceUnique = false)

        val expectedActivity =
            activity.copy(comments = listOf(updatedComment.copy(ownReactions = listOf(reaction))))
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onCommentReactionUpserted with enforceUnique true, then replace all existing user reactions with single new one`() =
        runTest {
            val existingReactions =
                listOf(
                    feedsReactionData(
                        commentId = "comment-1",
                        type = "like",
                        userId = currentUserId,
                    ),
                    feedsReactionData(
                        commentId = "comment-1",
                        type = "heart",
                        userId = currentUserId,
                    ),
                )

            val comment =
                commentData("comment-1", objectId = "activity-1", ownReactions = existingReactions)
            val activity = activityData("activity-1").copy(comments = listOf(comment))
            val activityPin = activityPin(activity)
            setupInitialState(listOf(activity), listOf(activityPin))

            val newReaction =
                feedsReactionData(commentId = "comment-1", type = "smile", userId = currentUserId)
            val updatedComment =
                commentData("comment-1", objectId = "activity-1", ownReactions = existingReactions)

            feedState.onCommentReactionUpserted(updatedComment, newReaction, enforceUnique = true)

            val expectedActivity =
                activity.copy(
                    comments = listOf(updatedComment.copy(ownReactions = listOf(newReaction)))
                )
            assertEquals(listOf(expectedActivity), feedState.activities.value)
            val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
            assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
        }

    @Test
    fun `on onReactionUpserted, then add reaction to activity`() = runTest {
        val activity = activityData("activity-1")
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))

        val reaction = feedsReactionData("activity-1", currentUserId)
        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onReactionUpserted(reaction, updatedActivity, enforceUnique = false)

        val expected = updatedActivity.copy(ownReactions = listOf(reaction))
        assertEquals(listOf(expected), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expected)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onReactionRemoved, then remove reaction from activity`() = runTest {
        val reaction = feedsReactionData("activity-1", currentUserId)
        val activity = activityData("activity-1", ownReactions = listOf(reaction))
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onReactionRemoved(reaction, updatedActivity)

        val expected = updatedActivity.copy(ownReactions = emptyList())
        assertEquals(listOf(expected), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expected)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
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
    fun `on onPollDeleted, then remove poll from activities`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity = activityData("activity-1", poll = poll)
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))

        feedState.onPollDeleted("poll-1")

        val expectedActivity = activity.copy(poll = null)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onPollUpdated, then preserve own votes when updating poll in activities`() = runTest {
        val ownVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val poll = pollData("poll-1", "Test Poll", ownVotes = listOf(ownVote))
        val activity = activityData("activity-1", poll = poll)
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))

        val updatedPoll = pollData("poll-1", "Updated Poll", ownVotes = emptyList())
        feedState.onPollUpdated(updatedPoll)

        val expectedPoll = updatedPoll.copy(ownVotes = listOf(ownVote))
        val expectedActivity = activity.copy(poll = expectedPoll)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onPollVoteUpserted, then update poll with changed vote in activities`() = runTest {
        val originalVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val poll = pollWithVote("poll-1", originalVote)
        val activity = activityData("activity-1", poll = poll)
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))
        val changedVote = pollVoteData("vote-1", "poll-1", "option-2", currentUserId)

        feedState.onPollVoteUpserted(changedVote, "poll-1")

        val expectedPoll =
            poll.copy(
                voteCount = 1,
                ownVotes = listOf(changedVote),
                latestVotesByOption =
                    mapOf("option-1" to emptyList(), "option-2" to listOf(changedVote)),
                voteCountsByOption = mapOf("option-1" to 0, "option-2" to 1),
            )
        val expectedActivity = activity.copy(poll = expectedPoll)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onPollVoteRemoved, then remove vote from poll in activities`() = runTest {
        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val poll = pollWithVote("poll-1", vote)
        val activity = activityData("activity-1", poll = poll)
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))

        feedState.onPollVoteRemoved(vote, "poll-1")

        val expectedPoll =
            poll.copy(
                voteCount = 0,
                ownVotes = emptyList(),
                latestVotesByOption = mapOf("option-1" to emptyList()),
                voteCountsByOption = mapOf("option-1" to 0),
            )
        val expectedActivity = activity.copy(poll = expectedPoll)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on poll events with different poll id, then keep existing polls unchanged`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity = setupActivityWithPoll(poll)
        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)

        feedState.onPollVoteUpserted(vote, "poll-2")

        expectActivityWithPoll(activity, poll)
    }

    @Test
    fun `on onNotificationFeedUpdated, update matching groups and notification status`() = runTest {
        val initial =
            List(3) {
                aggregatedActivityData(
                    activities = listOf(activityData("activity-$it")),
                    activityCount = it,
                    group = "group-$it",
                    userCount = it,
                )
            }

        setupInitialState(aggregatedActivities = initial)

        val updated =
            aggregatedActivityData(
                activities = listOf(activityData("activity-1-updated")),
                activityCount = 5,
                group = "group-1",
                userCount = 5,
            )
        val notificationStatus = NotificationStatusResponse(unread = 5, unseen = 3)

        feedState.onNotificationFeedUpdated(listOf(updated), notificationStatus)

        assertEquals(listOf(initial[0], updated, initial[2]), feedState.aggregatedActivities.value)
        assertEquals(notificationStatus, feedState.notificationStatus.value)
    }

    @Test
    fun `on onStoriesFeedUpdated, update matching groups`() = runTest {
        val initial =
            List(3) {
                aggregatedActivityData(
                    activities = listOf(activityData("story-$it")),
                    activityCount = it,
                    group = "story-group-$it",
                    userCount = it,
                )
            }

        setupInitialState(aggregatedActivities = initial)

        val updated0 =
            aggregatedActivityData(
                activities = listOf(activityData("story-0-updated")),
                activityCount = 10,
                group = "story-group-0",
                userCount = 10,
            )
        val updated2 =
            aggregatedActivityData(
                activities = listOf(activityData("story-2-updated")),
                activityCount = 30,
                group = "story-group-2",
                userCount = 30,
            )

        feedState.onStoriesFeedUpdated(listOf(updated0, updated2))

        assertEquals(listOf(updated0, initial[1], updated2), feedState.aggregatedActivities.value)
    }

    // Helper functions
    private fun setupInitialState(
        activities: List<ActivityData> = listOf(activityData("activity-1")),
        pinnedActivities: List<ActivityPinData> = emptyList(),
        feed: FeedData = feedData(),
        followers: List<FollowData> = emptyList(),
        following: List<FollowData> = emptyList(),
        followRequests: List<FollowData> = emptyList(),
        aggregatedActivities: List<AggregatedActivityData> = emptyList(),
    ) {
        val result =
            createGetOrCreateInfo(
                activities = activities,
                pinnedActivities = pinnedActivities,
                feed = feed,
                followers = followers,
                following = following,
                followRequests = followRequests,
                aggregatedActivities = aggregatedActivities,
            )
        feedState.onQueryFeed(result)
    }

    private fun createGetOrCreateInfo(
        activities: List<ActivityData>,
        feed: FeedData = feedData(),
        followers: List<FollowData> = emptyList(),
        following: List<FollowData> = emptyList(),
        followRequests: List<FollowData> = emptyList(),
        pinnedActivities: List<ActivityPinData> = emptyList(),
        aggregatedActivities: List<AggregatedActivityData> = emptyList(),
    ): GetOrCreateInfo {
        val pagination = PaginationData(next = "next-cursor", previous = null)
        val queryConfig = createQueryConfig()

        return GetOrCreateInfo(
            pagination = pagination,
            activities = activities,
            activitiesQueryConfig = queryConfig,
            aggregatedActivities = aggregatedActivities,
            feed = feed,
            followers = followers,
            following = following,
            followRequests = followRequests,
            pinnedActivities = pinnedActivities,
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
