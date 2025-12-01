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
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class ActivityStateImplTest {
    private val currentUserId = "user-1"
    private val mockActivityCommentListState: ActivityCommentListState = mockk()
    private val activityState = ActivityStateImpl(currentUserId, mockActivityCommentListState)

    @Test
    fun `on initial state, then return null activity and poll`() = runTest {
        assertNull(activityState.activity.value)
        assertNull(activityState.poll.value)
    }

    @Test
    fun `on onActivityUpdated, then update activity and poll`() = runTest {
        val poll = pollData()
        val activityWithPoll = activityData(poll = poll)

        activityState.onActivityUpdated(activityWithPoll)

        assertEquals(activityWithPoll, activityState.activity.value)
        assertEquals(poll, activityState.poll.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve ownBookmarks when updating activity`() = runTest {
        val initialBookmark = bookmarkData("activity-1", currentUserId)
        val initialActivity =
            activityData("activity-1", text = "Original", ownBookmarks = listOf(initialBookmark))
        val updatedActivity =
            activityData("activity-1", text = "Updated", ownBookmarks = emptyList())
        setupAndUpdateActivity(initialActivity, updatedActivity)

        val expectedActivity = updatedActivity.copy(ownBookmarks = listOf(initialBookmark))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve ownReactions when updating activity`() = runTest {
        val initialReaction = feedsReactionData("activity-1", "like", currentUserId)
        val initialActivity =
            activityData("activity-1", text = "Original", ownReactions = listOf(initialReaction))
        val updatedActivity =
            activityData("activity-1", text = "Updated", ownReactions = emptyList())
        setupAndUpdateActivity(initialActivity, updatedActivity)

        val expectedActivity = updatedActivity.copy(ownReactions = listOf(initialReaction))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onActivityUpdated, then preserve poll ownVotes when updating activity`() = runTest {
        val ownVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(ownVote))
        val initialActivity = activityData("activity-1", text = "Original", poll = initialPoll)
        val updatedPoll = pollData("poll-1", "Updated Poll", ownVotes = emptyList())
        val updatedActivity = activityData("activity-1", text = "Updated", poll = updatedPoll)
        setupAndUpdateActivity(initialActivity, updatedActivity)

        val expectedPoll = updatedPoll.copy(ownVotes = listOf(ownVote))
        val expectedActivity = updatedActivity.copy(poll = expectedPoll)
        expectActivityAndPoll(expectedActivity, expectedPoll)
    }

    @Test
    fun `on onActivityUpdated with poll removed, then clear poll state`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val initialActivity = activityData("activity-1", text = "Original", poll = initialPoll)
        val updatedActivity = activityData("activity-1", text = "Updated", poll = null)
        setupAndUpdateActivity(initialActivity, updatedActivity)

        assertEquals(updatedActivity, activityState.activity.value)
        assertNull(activityState.poll.value)
    }

    @Test
    fun `on onActivityRemoved, then clear activity and poll state`() = runTest {
        setupInitialPoll(pollData())

        activityState.onActivityRemoved()

        assertNull(activityState.activity.value)
        assertNull(activityState.poll.value)
    }

    @Test
    fun `on onActivityUpdated with new poll, then set new poll`() = runTest {
        val initialActivity = activityData("activity-1", text = "Original", poll = null)
        val newPoll = pollData("poll-1", "New Poll")
        val updatedActivity = activityData("activity-1", text = "Updated", poll = newPoll)
        setupAndUpdateActivity(initialActivity, updatedActivity)

        expectActivityAndPoll(updatedActivity, newPoll)
    }

    @Test
    fun `on onActivityUpdated, then preserve all own properties together`() = runTest {
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
        setupAndUpdateActivity(initialActivity, updatedActivity)

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
        expectActivityAndPoll(expectedActivity, expectedPoll)
    }

    @Test
    fun `on onActivityUpdated with single feed and no currentFeed, then preserve currentFeed`() =
        runTest {
            val initialFeed = feedData(id = "1", groupId = "user")
            val initialActivity =
                activityData(
                    "activity-1",
                    text = "Original",
                    currentFeed = initialFeed,
                    feeds = listOf("user:1"),
                )
            val updatedActivity =
                activityData(
                    "activity-1",
                    text = "Updated",
                    currentFeed = null,
                    feeds = listOf("user:1"),
                )
            setupAndUpdateActivity(initialActivity, updatedActivity)

            val expectedActivity = updatedActivity.copy(currentFeed = initialFeed)
            assertEquals(expectedActivity, activityState.activity.value)
        }

    @Test
    fun `on onReactionUpserted from current user, then update activity and own reactions`() =
        runTest {
            val initialActivity = activityData("activity-1")
            setupInitialActivity(initialActivity)

            val reaction = feedsReactionData("activity-1", "like", currentUserId)
            val updatedActivity = activityData("activity-1", text = "Updated activity")
            activityState.onReactionUpserted(reaction, updatedActivity, enforceUnique = false)

            val expected = updatedActivity.copy(ownReactions = listOf(reaction))
            assertEquals(expected, activityState.activity.value)
        }

    @Test
    fun `on onReactionUpserted from other user, then update activity and keep ownReactions`() =
        runTest {
            val initialActivity = activityData("activity-1")
            activityState.onActivityUpdated(initialActivity)

            val reaction = feedsReactionData("activity-1", "like", "other-user")
            val updatedActivity = activityData("activity-1", text = "Updated activity")
            activityState.onReactionUpserted(reaction, updatedActivity, enforceUnique = false)

            val expected = updatedActivity.copy(ownReactions = emptyList())
            assertEquals(expected, activityState.activity.value)
        }

    @Test
    fun `on onReactionRemoved, then remove reaction from activity`() = runTest {
        val reaction = feedsReactionData("activity-1", "like", currentUserId)
        val initialActivity =
            activityData("activity-1", text = "With reaction", ownReactions = listOf(reaction))
        setupInitialActivity(initialActivity)

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        activityState.onReactionRemoved(reaction, updatedActivity)

        val expected = updatedActivity.copy(ownReactions = emptyList())
        assertEquals(expected, activityState.activity.value)
    }

    @Test
    fun `on onBookmarkUpserted, then add bookmark to activity`() = runTest {
        val initialActivity = activityData("activity-1")
        setupInitialActivity(initialActivity)

        val bookmark = bookmarkData("activity-1", currentUserId)
        activityState.onBookmarkUpserted(bookmark)

        val expectedActivity = bookmark.activity.copy(ownBookmarks = listOf(bookmark))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onBookmarkRemoved, then remove bookmark from activity`() = runTest {
        val initialActivity = activityData("activity-1")
        setupInitialActivity(initialActivity)

        val bookmark = bookmarkData("activity-1", currentUserId)
        activityState.onBookmarkUpserted(bookmark)
        activityState.onBookmarkRemoved(bookmark)

        val expectedActivity = initialActivity.copy(bookmarkCount = 0, ownBookmarks = emptyList())
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onBookmarkUpserted from other user, then update count but not ownBookmarks`() =
        runTest {
            val initialActivity = activityData("activity-1")
            setupInitialActivity(initialActivity)

            val bookmark = bookmarkData("activity-1", "other-user")
            activityState.onBookmarkUpserted(bookmark)

            val expectedActivity = bookmark.activity.copy(ownBookmarks = emptyList())
            assertEquals(expectedActivity, activityState.activity.value)
        }

    @Test
    fun `on onPollDeleted, then remove poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        activityState.onPollDeleted("poll-1")

        assertNull(activityState.poll.value)
    }

    @Test
    fun `on onPollDeleted with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        activityState.onPollDeleted("poll-2")

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollUpdated, then update poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val updatedPoll = pollData("poll-1", name = "Updated Poll")
        activityState.onPollUpdated(updatedPoll)

        assertEquals(updatedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollUpdated with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val differentPoll = pollData("poll-2", name = "Different Poll")
        activityState.onPollUpdated(differentPoll)

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteUpserted with matching id, then update poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        activityState.onPollVoteUpserted(vote, "poll-1")

        val expectedPoll =
            initialPoll.copy(
                voteCount = 1,
                ownVotes = listOf(vote),
                latestVotesByOption = mapOf("option-1" to listOf(vote)),
                voteCountsByOption = mapOf("option-1" to 1),
            )
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteUpserted with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        activityState.onPollVoteUpserted(vote, "poll-2")

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteUpserted with answer vote, then update answer data`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll", allowAnswers = true)
        setupInitialPoll(initialPoll)

        val answerVote =
            pollVoteData(
                "vote-1",
                "poll-1",
                optionId = "",
                userId = currentUserId,
                answerText = "My answer",
            )
        activityState.onPollVoteUpserted(answerVote, "poll-1")

        val expectedPoll =
            initialPoll.copy(
                answersCount = 1,
                ownVotes = listOf(answerVote),
                latestAnswers = listOf(answerVote),
            )
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteUpserted with multiple votes, then update vote counts properly`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote1 = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val vote2 = pollVoteData("vote-2", "poll-1", "option-2", "user-2")

        activityState.onPollVoteUpserted(vote1, "poll-1")
        activityState.onPollVoteUpserted(vote2, "poll-1")

        val expectedPoll =
            initialPoll.copy(
                voteCount = 2,
                ownVotes = listOf(vote1),
                latestVotesByOption =
                    mapOf("option-1" to listOf(vote1), "option-2" to listOf(vote2)),
                voteCountsByOption = mapOf("option-1" to 1, "option-2" to 1),
            )
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteRemoved, then remove vote locally`() = runTest {
        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val initialPoll =
            pollData(
                "poll-1",
                "Test Poll",
                voteCount = 1,
                latestVotesByOption = mapOf("option-1" to listOf(vote)),
                voteCountsByOption = mapOf("option-1" to 1),
                ownVotes = listOf(vote),
            )
        setupInitialPoll(initialPoll)

        activityState.onPollVoteRemoved(vote, "poll-1")

        val expectedPoll =
            initialPoll.copy(
                voteCount = 0,
                ownVotes = emptyList(),
                latestVotesByOption = mapOf("option-1" to emptyList()),
                voteCountsByOption = mapOf("option-1" to 0),
            )
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteRemoved with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        activityState.onPollVoteRemoved(vote, "poll-2")

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteRemoved with answer vote, then update answer data`() = runTest {
        val answerVote =
            pollVoteData(
                "vote-1",
                "poll-1",
                optionId = "",
                userId = currentUserId,
                answerText = "My answer",
            )
        val initialPoll =
            pollData(
                "poll-1",
                "Test Poll",
                allowAnswers = true,
                answersCount = 1,
                latestAnswers = listOf(answerVote),
                ownVotes = listOf(answerVote),
            )
        setupInitialPoll(initialPoll)

        activityState.onPollVoteRemoved(answerVote, "poll-1")

        val expectedPoll =
            initialPoll.copy(answersCount = 0, ownVotes = emptyList(), latestAnswers = emptyList())
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollUpdated, then preserve own votes when updating`() = runTest {
        val ownVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(ownVote))
        setupInitialPoll(initialPoll)

        val updatedPollFromBackend = pollData("poll-1", "Updated Poll", ownVotes = emptyList())
        activityState.onPollUpdated(updatedPollFromBackend)

        val expectedPoll = updatedPollFromBackend.copy(ownVotes = listOf(ownVote))
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onCommentUpserted, add comment to activity`() = runTest {
        val initialActivity = activityData("activity-1", comments = emptyList())
        setupInitialActivity(initialActivity)

        val comment = commentData("comment-1", objectId = "activity-1")
        activityState.onCommentUpserted(comment)

        val expectedActivity = initialActivity.copy(commentCount = 1, comments = listOf(comment))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onCommentUpserted, update existing comment in activity`() = runTest {
        val originalComment = commentData("comment-1", text = "Original", objectId = "activity-1")
        val initialActivity = activityData("activity-1", comments = listOf(originalComment))
        setupInitialActivity(initialActivity)

        val updatedComment = commentData("comment-1", text = "Updated", objectId = "activity-1")
        activityState.onCommentUpserted(updatedComment)

        val expectedActivity = initialActivity.copy(comments = listOf(updatedComment))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onCommentRemoved, remove comment from activity`() = runTest {
        val comment = commentData("comment-1", objectId = "activity-1")
        val initialActivity = activityData("activity-1", comments = listOf(comment))
        setupInitialActivity(initialActivity)

        activityState.onCommentRemoved("comment-1")

        val expectedActivity = initialActivity.copy(commentCount = 0, comments = emptyList())
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onCommentReactionUpserted, add reaction to comment in activity`() = runTest {
        val comment = commentData("comment-1", objectId = "activity-1", ownReactions = emptyList())
        val initialActivity = activityData("activity-1", comments = listOf(comment))
        setupInitialActivity(initialActivity)

        val reaction = feedsReactionData("comment-1", "like", currentUserId)
        activityState.onCommentReactionUpserted(comment, reaction, enforceUnique = false)

        val expectedComment = comment.copy(ownReactions = listOf(reaction))
        val expectedActivity = initialActivity.copy(comments = listOf(expectedComment))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onCommentReactionRemoved, remove reaction from comment in activity`() = runTest {
        val reaction = feedsReactionData("comment-1", "like", currentUserId)
        val comment =
            commentData("comment-1", objectId = "activity-1", ownReactions = listOf(reaction))
        val initialActivity = activityData("activity-1", comments = listOf(comment))
        setupInitialActivity(initialActivity)

        activityState.onCommentReactionRemoved(comment, reaction)

        val expectedComment = comment.copy(ownReactions = emptyList())
        val expectedActivity = initialActivity.copy(comments = listOf(expectedComment))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onCommentReactionUpserted with enforceUnique true, replace existing user reactions with new one`() =
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
            val initialActivity = activityData("activity-1", comments = listOf(comment))
            setupInitialActivity(initialActivity)

            val newReaction =
                feedsReactionData(commentId = "comment-1", type = "smile", userId = currentUserId)

            activityState.onCommentReactionUpserted(comment, newReaction, enforceUnique = true)

            val expectedComment = comment.copy(ownReactions = listOf(newReaction))
            val expectedActivity = initialActivity.copy(comments = listOf(expectedComment))
            assertEquals(expectedActivity, activityState.activity.value)
        }

    @Test
    fun `on onFeedCapabilitiesUpdated, update matching activity`() = runTest {
        val feedId1 = FeedId("user:1")
        val feed1 = feedData(id = "1", groupId = "user", ownCapabilities = emptySet())
        val activity1 = activityData("activity-1", currentFeed = feed1)
        setupInitialActivity(activity1)
        val newCapabilities = setOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)

        activityState.onFeedCapabilitiesUpdated(mapOf(feedId1 to newCapabilities))

        val expectedActivity1 =
            activity1.copy(currentFeed = feed1.copy(ownCapabilities = newCapabilities))
        assertEquals(expectedActivity1, activityState.activity.value)
    }

    @Test
    fun `on onFeedCapabilitiesUpdated with non-matching feed, keep activity unchanged`() = runTest {
        val feedId1 = FeedId("user:1")
        val feedId2 = FeedId("user:2")
        val feed1 = feedData(id = "1", groupId = "user", ownCapabilities = emptySet())
        val activity1 = activityData("activity-1", currentFeed = feed1)
        setupInitialActivity(activity1)
        val newCapabilities = setOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)

        activityState.onFeedCapabilitiesUpdated(mapOf(feedId2 to newCapabilities))

        assertEquals(activity1, activityState.activity.value)
    }

    @Test
    fun `on onFeedCapabilitiesUpdated with activity without feed, keep activity unchanged`() =
        runTest {
            val feedId1 = FeedId("user:1")
            val activity1 = activityData("activity-1", currentFeed = null)
            setupInitialActivity(activity1)
            val newCapabilities = setOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)

            activityState.onFeedCapabilitiesUpdated(mapOf(feedId1 to newCapabilities))

            assertEquals(activity1, activityState.activity.value)
        }

    private fun setupInitialActivity(activity: ActivityData) {
        activityState.onActivityUpdated(activity)
    }

    private fun setupInitialPoll(poll: PollData) {
        val activity = activityData(poll = poll)
        activityState.onActivityUpdated(activity)
    }

    private fun setupAndUpdateActivity(initial: ActivityData, updated: ActivityData) {
        activityState.onActivityUpdated(initial)
        activityState.onActivityUpdated(updated)
    }

    private fun expectActivityAndPoll(expectedActivity: ActivityData, expectedPoll: PollData) {
        assertEquals(expectedActivity, activityState.activity.value)
        assertEquals(expectedPoll, activityState.poll.value)
    }
}
