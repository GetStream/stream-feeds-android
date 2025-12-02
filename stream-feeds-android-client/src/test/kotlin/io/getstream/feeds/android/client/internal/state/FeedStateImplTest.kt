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

package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.FollowStatus
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.InsertionAction
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.model.FeedOwnValues
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.activityPin
import io.getstream.feeds.android.client.internal.test.TestData.aggregatedActivityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.getstream.feeds.android.network.models.NotificationStatusResponse
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
        )

        assertEquals(newPagination, feedState.activitiesPagination)
        assertEquals(initialActivities + newActivities, feedState.activities.value)
        assertEquals((initialAggregated + newAggregated), feedState.aggregatedActivities.value)
    }

    @Test
    fun `on onActivityAdded with Ignore action, then ignore activity`() = runTest {
        val initialActivity = activityData()
        setupInitialState(listOf(initialActivity))

        val newActivity = activityData("activity-2")
        feedState.onActivityAdded(newActivity, InsertionAction.Ignore)

        assertEquals(listOf(initialActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityAdded with AddToStart action, then prepend activity`() = runTest {
        val initialActivity = activityData()
        setupInitialState(listOf(initialActivity))

        val newActivity = activityData("activity-2")
        feedState.onActivityAdded(newActivity, InsertionAction.AddToStart)

        assertEquals(listOf(newActivity, initialActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityAdded with AddToEnd action, then append activity`() = runTest {
        val initialActivity = activityData()
        setupInitialState(listOf(initialActivity))

        val newActivity = activityData("activity-2")
        feedState.onActivityAdded(newActivity, InsertionAction.AddToEnd)

        assertEquals(listOf(initialActivity, newActivity), feedState.activities.value)
    }

    @Test
    fun `on onActivityAdded for existing activity, then update activity`() = runTest {
        val initialActivity = activityData("activity-1", currentFeed = feedData("feed-1"))
        setupInitialState(listOf(initialActivity))

        val updatedActivity =
            activityData(
                "activity-1",
                text = "Updated activity",
                currentFeed = null,
                feeds = listOf(FeedId("feed-1"), FeedId("feed-2")),
            )
        feedState.onActivityAdded(updatedActivity, InsertionAction.AddToStart)

        val expected = updatedActivity.copy(currentFeed = initialActivity.currentFeed)
        assertEquals(listOf(expected), feedState.activities.value)
    }

    @Test
    fun `on onActivityUpdated, then update activity`() = runTest {
        val initialActivity = activityData("activity-1")
        val activityPin = activityPin(initialActivity)
        setupInitialState(listOf(initialActivity), listOf(activityPin))

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        feedState.onActivityUpdated(updatedActivity)

        assertEquals(listOf(updatedActivity), feedState.activities.value)
        val expectedPinnedActivity = activityPin.copy(activity = updatedActivity)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve own properties in activity`() = runTest {
        val initialBookmark = bookmarkData("activity-1", currentUserId)
        val initialReaction = feedsReactionData("activity-1", "like", currentUserId)
        val ownVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(ownVote))
        val initialCapabilities = setOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)
        val initialFeed =
            feedData(id = "1", groupId = "user", ownCapabilities = initialCapabilities)
        val initialActivity =
            activityData(
                "activity-1",
                text = "Original",
                poll = initialPoll,
                ownBookmarks = listOf(initialBookmark),
                ownReactions = listOf(initialReaction),
                currentFeed = initialFeed,
            )
        setupInitialState(listOf(initialActivity))

        // Backend sends update with empty "own" properties
        val updatedPoll = pollData("poll-1", "Updated Poll", ownVotes = emptyList())
        val updatedFeed = feedData(id = "1", groupId = "user", ownCapabilities = emptySet())
        val updatedActivity =
            activityData(
                "activity-1",
                text = "Updated",
                poll = updatedPoll,
                ownBookmarks = emptyList(),
                ownReactions = emptyList(),
                currentFeed = updatedFeed,
            )
        feedState.onActivityUpdated(updatedActivity)

        // Verify all "own" properties are preserved
        val expectedPoll = updatedPoll.copy(ownVotes = listOf(ownVote))
        val expectedFeed = updatedFeed.copy(ownCapabilities = initialCapabilities)
        val expectedActivity =
            updatedActivity.copy(
                poll = expectedPoll,
                ownBookmarks = listOf(initialBookmark),
                ownReactions = listOf(initialReaction),
                currentFeed = expectedFeed,
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
        val activity = activityData()
        val notificationStatus = NotificationStatusResponse(unread = 5, unseen = 3)
        setupInitialState(
            activities = listOf(activity),
            aggregatedActivities = listOf(aggregatedActivityData()),
            pinnedActivities = listOf(activityPin(activity)),
            followers = listOf(followData()),
            following = listOf(followData()),
            followRequests = listOf(followData()),
        )
        feedState.onNotificationFeedUpdated(emptyList(), notificationStatus)

        feedState.onFeedDeleted()

        assertTrue(feedState.activities.value.isEmpty())
        assertTrue(feedState.aggregatedActivities.value.isEmpty())
        assertTrue(feedState.pinnedActivities.value.isEmpty())
        assertNull(feedState.feed.value)
        assertTrue(feedState.followers.value.isEmpty())
        assertTrue(feedState.following.value.isEmpty())
        assertTrue(feedState.followRequests.value.isEmpty())
        assertNull(feedState.notificationStatus.value)
        assertNull(feedState.activitiesPagination)
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
    fun `onPollVoteUpserted with current user vote, update poll and add to ownVotes`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity = activityData("activity-1", poll = poll)
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))
        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)

        val updatedPoll = pollData("poll-1", "Updated Poll")
        feedState.onPollVoteUpserted(updatedPoll, vote)

        val expectedActivity = activity.copy(poll = updatedPoll.copy(ownVotes = listOf(vote)))
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `onPollVoteUpserted with different user vote, update poll and keep ownVotes`() = runTest {
        val existingVote = pollVoteData("existing-vote", "poll-1", "option-1", currentUserId)
        val poll = pollData("poll-1", "Test Poll", ownVotes = listOf(existingVote))
        val activity = activityData("activity-1", poll = poll)
        val activityPin = activityPin(activity)
        setupInitialState(listOf(activity), listOf(activityPin))
        val vote = pollVoteData("vote-1", "poll-1", "option-1", "other-user")

        val updatedPoll = pollData("poll-1", "Updated Poll")
        feedState.onPollVoteUpserted(updatedPoll, vote)

        val expectedActivity = activity.copy(poll = updatedPoll.copy(ownVotes = poll.ownVotes))
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
        assertEquals(listOf(expectedActivity), feedState.activities.value)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
    }

    @Test
    fun `onPollVoteRemoved with current user vote, update poll and remove from ownVotes`() =
        runTest {
            val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
            val poll = pollData("poll-1", "Test Poll", ownVotes = listOf(vote))
            val activity = activityData("activity-1", poll = poll)
            val activityPin = activityPin(activity)
            setupInitialState(listOf(activity), listOf(activityPin))

            val updatedPoll = pollData("poll-1", "Updated Poll")
            feedState.onPollVoteRemoved(updatedPoll, vote)

            val expectedActivity = activity.copy(poll = updatedPoll.copy(ownVotes = emptyList()))
            val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
            assertEquals(listOf(expectedActivity), feedState.activities.value)
            assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
        }

    @Test
    fun `on onPollVoteRemoved with different user vote, then update poll and keep ownVotes`() =
        runTest {
            val existingVote = pollVoteData("existing-vote", "poll-1", "option-1", currentUserId)
            val poll = pollData("poll-1", "Test Poll", ownVotes = listOf(existingVote))
            val activity = activityData("activity-1", poll = poll)
            val activityPin = activityPin(activity)
            setupInitialState(listOf(activity), listOf(activityPin))
            val vote = pollVoteData("vote-1", "poll-1", "option-1", "other-user")

            val updatedPoll = pollData("poll-1", "Updated Poll")
            feedState.onPollVoteRemoved(updatedPoll, vote)

            val expectedActivity =
                activity.copy(poll = updatedPoll.copy(ownVotes = listOf(existingVote)))
            val expectedPinnedActivity = activityPin.copy(activity = expectedActivity)
            assertEquals(listOf(expectedActivity), feedState.activities.value)
            assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
        }

    @Test
    fun `on poll events with different poll id, then keep existing polls unchanged`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity = setupActivityWithPoll(poll)
        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        val differentPoll = pollData("poll-2", "Different Poll")

        feedState.onPollVoteUpserted(differentPoll, vote)

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
    fun `on onStoriesFeedUpdated, update matching activities and groups`() = runTest {
        val initialActivities = List(3) { activityData("story-$it") }
        val initialAggregated =
            List(3) {
                aggregatedActivityData(
                    activities = listOf(activityData("story-$it")),
                    activityCount = it,
                    group = "story-group-$it",
                    userCount = it,
                )
            }
        setupInitialState(activities = initialActivities, aggregatedActivities = initialAggregated)

        val updatedActivity0 = activityData("story-0", text = "Updated 0")
        val updatedActivity2 = activityData("story-2", text = "Updated 2")
        val updatedAggregated0 =
            aggregatedActivityData(
                activities = listOf(activityData("story-0-updated")),
                activityCount = 10,
                group = "story-group-0",
                userCount = 10,
            )
        val updatedAggregated2 =
            aggregatedActivityData(
                activities = listOf(activityData("story-2-updated")),
                activityCount = 30,
                group = "story-group-2",
                userCount = 30,
            )

        feedState.onStoriesFeedUpdated(
            listOf(updatedActivity0, updatedActivity2),
            listOf(updatedAggregated0, updatedAggregated2),
        )

        assertEquals(
            listOf(updatedActivity0, initialActivities[1], updatedActivity2),
            feedState.activities.value,
        )
        assertEquals(
            listOf(updatedAggregated0, initialAggregated[1], updatedAggregated2),
            feedState.aggregatedActivities.value,
        )
    }

    @Test
    fun `on onFollowAdded when targetFeed matches, update feed with it`() = runTest {
        val initialFeed =
            feedData(
                id = "test",
                groupId = "user",
                ownCapabilities = setOf(FeedOwnCapability.UpdateFeed),
            )
        setupInitialState(feed = initialFeed)

        val updatedTargetFeed = feedData(id = "test", groupId = "user").copy(followerCount = 10)
        val follow = followData().copy(targetFeed = updatedTargetFeed)

        feedState.onFollowAdded(follow)

        val expected = updatedTargetFeed.copy(ownCapabilities = initialFeed.ownCapabilities)
        assertEquals(expected, feedState.feed.value)
    }

    @Test
    fun `on onFollowAdded when sourceFeed matches, update feed with it`() = runTest {
        val initialFeed =
            feedData(
                id = "test",
                groupId = "user",
                ownCapabilities = setOf(FeedOwnCapability.UpdateFeed),
            )
        setupInitialState(feed = initialFeed)

        val updatedSourceFeed = feedData(id = "test", groupId = "user").copy(followingCount = 15)
        val follow = followData().copy(sourceFeed = updatedSourceFeed)

        feedState.onFollowAdded(follow)

        val expected = updatedSourceFeed.copy(ownCapabilities = initialFeed.ownCapabilities)
        assertEquals(expected, feedState.feed.value)
    }

    @Test
    fun `on onFollowRemoved when targetFeed matches, update feed with it`() = runTest {
        val initialFeed =
            feedData(
                id = "test",
                groupId = "user",
                ownCapabilities = setOf(FeedOwnCapability.UpdateFeed),
            )
        setupInitialState(feed = initialFeed)

        val updatedTargetFeed = feedData(id = "test", groupId = "user").copy(followerCount = 5)
        val follow = followData().copy(targetFeed = updatedTargetFeed)

        feedState.onFollowRemoved(follow)

        val expected = updatedTargetFeed.copy(ownCapabilities = initialFeed.ownCapabilities)
        assertEquals(expected, feedState.feed.value)
    }

    @Test
    fun `on onFollowRemoved when sourceFeed matches, update feed with it`() = runTest {
        val initialFeed =
            feedData(
                id = "test",
                groupId = "user",
                ownCapabilities = setOf(FeedOwnCapability.UpdateFeed),
            )
        setupInitialState(feed = initialFeed)

        val updatedSourceFeed = feedData(id = "test", groupId = "user").copy(followingCount = 8)
        val follow = followData().copy(sourceFeed = updatedSourceFeed)

        feedState.onFollowRemoved(follow)

        val expected = updatedSourceFeed.copy(ownCapabilities = initialFeed.ownCapabilities)
        assertEquals(expected, feedState.feed.value)
    }

    @Test
    fun `onFollowsUpdated, update following, followers, and follow requests`() = runTest {
        val following1 = followData(sourceFid = "user:test", targetFid = "user:target-1")
        val following2 = followData(sourceFid = "user:test", targetFid = "user:target-2")
        val follower1 = followData(sourceFid = "user:source-1", targetFid = "user:test")
        val follower2 = followData(sourceFid = "user:source-2", targetFid = "user:test")
        val pendingRequest =
            followData(
                sourceFid = "user:source-3",
                targetFid = "user:test",
                status = FollowStatus.Pending,
            )
        setupInitialState(
            following = listOf(following1, following2),
            followers = listOf(follower1, follower2),
            followRequests = listOf(pendingRequest),
        )

        val newFollowing = followData(sourceFid = "user:test", targetFid = "user:target-3")
        val newFollower = followData(sourceFid = "user:source-3", targetFid = "user:test")
        val newPendingRequest =
            followData(
                sourceFid = "user:source-4",
                targetFid = "user:test",
                status = FollowStatus.Pending,
            )
        val newSentRequest =
            followData(
                sourceFid = "user:test",
                targetFid = "user:target-4",
                status = FollowStatus.Pending,
            )
        val nonMatchingFollow = followData(sourceFid = "user:other-1", targetFid = "user:other-2")
        val updatedFollowing2 = following2.copy(pushPreference = "disabled")
        val updatedFollower2 = follower2.copy(pushPreference = "disabled")

        val updates =
            ModelUpdates(
                added =
                    listOf(
                        newFollowing,
                        newFollower,
                        newPendingRequest,
                        newSentRequest,
                        nonMatchingFollow,
                    ),
                updated = listOf(updatedFollowing2, updatedFollower2, nonMatchingFollow),
                removedIds = setOf(following1.id, follower1.id, pendingRequest.id),
            )

        feedState.onFollowsUpdated(updates)

        assertEquals(listOf(updatedFollowing2, newFollowing), feedState.following.value)
        assertEquals(listOf(updatedFollower2, newFollower), feedState.followers.value)
        assertEquals(listOf(newPendingRequest), feedState.followRequests.value)
    }

    @Test
    fun `on onFeedOwnValuesUpdated, update matching activities`() = runTest {
        val feedId1 = FeedId("user:1")
        val feed1 = feedData(id = "1", groupId = "user", ownCapabilities = emptySet())
        val feed2 = feedData(id = "2", groupId = "user", ownCapabilities = emptySet())
        val activity1 = activityData("activity-1", currentFeed = feed1)
        val activity2 = activityData("activity-2", currentFeed = feed2)
        val activityPin = activityPin(activity1)
        setupInitialState(listOf(activity1, activity2), listOf(activityPin))

        val newOwnValues =
            FeedOwnValues(
                capabilities = setOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity),
                follows = listOf(followData()),
                membership = feedMemberData(),
            )
        feedState.onFeedOwnValuesUpdated(mapOf(feedId1 to newOwnValues))

        val expectedActivity1 =
            activity1.copy(
                currentFeed =
                    feed1.copy(
                        ownCapabilities = newOwnValues.capabilities,
                        ownFollows = newOwnValues.follows,
                        ownMembership = newOwnValues.membership,
                    )
            )
        val expectedPinnedActivity = activityPin.copy(activity = expectedActivity1)
        assertEquals(listOf(expectedActivity1, activity2), feedState.activities.value)
        assertEquals(listOf(expectedPinnedActivity), feedState.pinnedActivities.value)
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

        return GetOrCreateInfo(
            pagination = pagination,
            activities = activities,
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
