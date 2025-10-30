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
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.internal.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
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
    fun `on onActivityUpserted, then update specific activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val updatedActivity = activityData("activity-1", text = "Updated activity")
        activityListState.onActivityUpserted(updatedActivity)

        assertEquals(listOf(updatedActivity, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onActivityUpserted with new activity, then insert it in sorted position`() = runTest {
        val activity1 = activityData("activity-1", createdAt = 1000)
        val activity2 = activityData("activity-2", createdAt = 2000)
        setupInitialActivities(activity2, activity1)

        val newActivity = activityData("activity-new", createdAt = 1500)
        activityListState.onActivityUpserted(newActivity)

        // Default sort is by createdAt descending
        val expectedActivities = listOf(activity2, newActivity, activity1)
        assertEquals(expectedActivities, activityListState.activities.value)
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
        activityListState.onCommentUpserted(comment)

        val activityWithComment = activityListState.activities.value.first()
        assertEquals(1, activityWithComment.commentCount)
    }

    @Test
    fun `on onCommentRemoved, then remove comment from activity`() = runTest {
        val activity1 = activityData("activity-1")
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val comment = commentData("comment-1", objectId = "activity-1")
        activityListState.onCommentUpserted(comment)
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
        activityListState.onReactionUpserted(reaction, updatedActivity, enforceUnique = false)

        val expected = updatedActivity.copy(ownReactions = listOf(reaction))
        assertEquals(listOf(expected, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onReactionUpserted with enforceUnique true, then replace all existing user reactions with single new one`() =
        runTest {
            val existingReactions =
                listOf(
                    feedsReactionData(
                        activityId = "activity-1",
                        type = "like",
                        userId = currentUserId,
                    ),
                    feedsReactionData(
                        activityId = "activity-1",
                        type = "heart",
                        userId = currentUserId,
                    ),
                )

            val activity1 = activityData("activity-1", ownReactions = existingReactions)
            val activity2 = activityData("activity-2")
            setupInitialActivities(activity1, activity2)

            val newReaction =
                feedsReactionData(activityId = "activity-1", type = "smile", userId = currentUserId)
            val updatedActivity = activityData("activity-1", ownReactions = existingReactions)

            activityListState.onReactionUpserted(newReaction, updatedActivity, enforceUnique = true)

            val expectedActivity = updatedActivity.copy(ownReactions = listOf(newReaction))
            assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
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
        activityListState.onCommentReactionUpserted(updatedComment, reaction, enforceUnique = false)
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
        activityListState.onCommentReactionUpserted(updatedComment, reaction, enforceUnique = false)

        val expectedComment = updatedComment.copy(ownReactions = listOf(reaction))
        val expectedActivity = activity1.copy(comments = listOf(expectedComment))
        assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
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
            val activity1 = activityData("activity-1", comments = listOf(comment))
            val activity2 = activityData("activity-2")
            setupInitialActivities(activity1, activity2)

            val newReaction =
                feedsReactionData(commentId = "comment-1", type = "smile", userId = currentUserId)
            val updatedComment =
                commentData("comment-1", objectId = "activity-1", ownReactions = existingReactions)

            activityListState.onCommentReactionUpserted(
                updatedComment,
                newReaction,
                enforceUnique = true,
            )

            val expectedComment = updatedComment.copy(ownReactions = listOf(newReaction))
            val expectedActivity = activity1.copy(comments = listOf(expectedComment))
            assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
        }

    @Test
    fun `on onPollDeleted, remove poll from activity`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity1 = activityData("activity-1", poll = poll)
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        activityListState.onPollDeleted("poll-1")

        val expectedActivity = activity1.copy(poll = null)
        assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onPollUpdated, update poll in activity`() = runTest {
        val originalPoll = pollData("poll-1", "Original Poll")
        val activity1 = activityData("activity-1", poll = originalPoll)
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val updatedPoll = pollData("poll-1", "Updated Poll", description = "Updated description")
        activityListState.onPollUpdated(updatedPoll)

        val expectedActivity = activity1.copy(poll = updatedPoll)
        assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onPollUpdated with non-matching poll, keep activities unchanged`() = runTest {
        val poll = pollData("poll-1", "Test Poll")
        val activity1 = activityData("activity-1", poll = poll)
        val activity2 = activityData("activity-2")
        val initialActivities = listOf(activity1, activity2)
        setupInitialActivities(activity1, activity2)

        val differentPoll = pollData("different-poll", "Different Poll")
        activityListState.onPollUpdated(differentPoll)

        assertEquals(initialActivities, activityListState.activities.value)
    }

    @Test
    fun `on onPollVoteUpserted, update poll with new vote`() = runTest {
        val originalPoll = pollData("poll-1", "Test Poll")
        val activity1 = activityData("activity-1", poll = originalPoll)
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        activityListState.onPollVoteUpserted("poll-1", vote)

        val expectedPoll =
            originalPoll.copy(
                ownVotes = listOf(vote),
                latestVotesByOption = mapOf("option-1" to listOf(vote)),
                voteCountsByOption = mapOf("option-1" to 1),
                voteCount = 1,
            )
        val expectedActivity = activity1.copy(poll = expectedPoll)
        assertEquals(listOf(expectedActivity, activity2), activityListState.activities.value)
    }

    @Test
    fun `on onPollVoteRemoved, update poll with vote removed`() = runTest {
        val existingVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val originalPoll =
            pollData(
                "poll-1",
                "Test Poll",
                ownVotes = listOf(existingVote),
                latestVotesByOption = mapOf("option-1" to listOf(existingVote)),
                voteCountsByOption = mapOf("option-1" to 1),
                voteCount = 1,
            )
        val activity1 = activityData("activity-1", poll = originalPoll)
        val activity2 = activityData("activity-2")
        setupInitialActivities(activity1, activity2)

        activityListState.onPollVoteRemoved("poll-1", existingVote)

        val expectedPoll =
            originalPoll.copy(
                ownVotes = emptyList(),
                latestVotesByOption = mapOf("option-1" to emptyList()),
                voteCountsByOption = mapOf("option-1" to 0),
                voteCount = 0,
            )
        val expectedActivity = activity1.copy(poll = expectedPoll)
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
