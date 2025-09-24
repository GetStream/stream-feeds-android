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

import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollOptionData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.client.internal.test.TestData.reactionGroupData
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
    fun `on activityUpdated, then update activity and poll`() = runTest {
        val poll = pollData()
        val activityWithPoll = activityData(poll = poll)

        activityState.onActivityUpdated(activityWithPoll)

        assertEquals(activityWithPoll, activityState.activity.value)
        assertEquals(poll, activityState.poll.value)
    }

    @Test
    fun `on reactionAdded, then add reaction to activity`() = runTest {
        val initialActivity = activityData("activity-1")
        activityState.onActivityUpdated(initialActivity)

        val reaction = feedsReactionData("activity-1", "like", currentUserId)
        activityState.onReactionAdded(reaction)

        val expectedActivity =
            initialActivity.copy(
                reactionCount = 1,
                ownReactions = listOf(reaction),
                latestReactions = listOf(reaction),
                reactionGroups = mapOf("like" to reactionGroupData(count = 1)),
            )
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on reactionRemoved, then remove reaction from activity`() = runTest {
        val initialActivity = activityData("activity-1")
        activityState.onActivityUpdated(initialActivity)

        val reaction = feedsReactionData("activity-1", "like", currentUserId)
        activityState.onReactionAdded(reaction)
        activityState.onReactionRemoved(reaction)

        val expectedActivity =
            initialActivity.copy(
                reactionCount = 0,
                ownReactions = emptyList(),
                latestReactions = emptyList(),
                reactionGroups = emptyMap(),
            )
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on bookmarkAdded, then add bookmark to activity`() = runTest {
        val initialActivity = activityData("activity-1")
        activityState.onActivityUpdated(initialActivity)

        val bookmark = bookmarkData("activity-1", currentUserId)
        activityState.onBookmarkAdded(bookmark)

        val expectedActivity =
            initialActivity.copy(bookmarkCount = 1, ownBookmarks = listOf(bookmark))
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on bookmarkRemoved, then remove bookmark from activity`() = runTest {
        val initialActivity = activityData("activity-1")
        activityState.onActivityUpdated(initialActivity)

        val bookmark = bookmarkData("activity-1", currentUserId)
        activityState.onBookmarkAdded(bookmark)
        activityState.onBookmarkRemoved(bookmark)

        val expectedActivity = initialActivity.copy(bookmarkCount = 0, ownBookmarks = emptyList())
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on reactionAdded from different user, then update latestReactions but not ownReactions`() =
        runTest {
            val initialActivity = activityData("activity-1")
            activityState.onActivityUpdated(initialActivity)

            val reaction = feedsReactionData("activity-1", "like", "other-user")
            activityState.onReactionAdded(reaction)

            val expectedActivity =
                initialActivity.copy(
                    reactionCount = 1,
                    ownReactions = emptyList(),
                    latestReactions = listOf(reaction),
                    reactionGroups = mapOf("like" to reactionGroupData(count = 1)),
                )
            assertEquals(expectedActivity, activityState.activity.value)
        }

    @Test
    fun `on bookmarkAdded from different user, then update count but not ownBookmarks`() = runTest {
        val initialActivity = activityData("activity-1")
        activityState.onActivityUpdated(initialActivity)

        val bookmark = bookmarkData("activity-1", "other-user")
        activityState.onBookmarkAdded(bookmark)

        val expectedActivity = initialActivity.copy(bookmarkCount = 1, ownBookmarks = emptyList())
        assertEquals(expectedActivity, activityState.activity.value)
    }

    @Test
    fun `on onPollClosed, then update poll`() = runTest {
        val initialPoll = pollData()
        setupInitialPoll(initialPoll)

        val closedPoll = pollData("poll-1", isClosed = true)
        activityState.onPollClosed(closedPoll)

        assertEquals(closedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollClosed with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val differentPoll = pollData("poll-2", isClosed = true)
        activityState.onPollClosed(differentPoll)

        assertEquals(initialPoll, activityState.poll.value)
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
    fun `on optionCreated, then add option to poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val newOption = pollOptionData("option-3", "New Option")
        activityState.onOptionCreated(newOption)

        val updatedPoll = activityState.poll.value
        assertEquals(3, updatedPoll?.options?.size)
        assertEquals(newOption, updatedPoll?.options?.last())
    }

    @Test
    fun `on optionDeleted, then remove option from poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        activityState.onOptionDeleted("option-1")

        val updatedPoll = activityState.poll.value
        assertEquals(1, updatedPoll?.options?.size)
        assertEquals("option-2", updatedPoll?.options?.first()?.id)
    }

    @Test
    fun `on optionUpdated, then update option in poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val updatedOption = pollOptionData("option-1", "Updated Option Text")
        activityState.onOptionUpdated(updatedOption)

        val updatedPoll = activityState.poll.value
        assertEquals(updatedOption, updatedPoll?.options?.first())
    }

    @Test
    fun `on onPollVoteCasted with matching id, then update poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        activityState.onPollVoteCasted(vote, "poll-1")

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
    fun `on onPollVoteCasted with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        activityState.onPollVoteCasted(vote, "poll-2")

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteCasted with answer vote, then update answer data`() = runTest {
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
        activityState.onPollVoteCasted(answerVote, "poll-1")

        val expectedPoll =
            initialPoll.copy(
                answersCount = 1,
                ownVotes = listOf(answerVote),
                latestAnswers = listOf(answerVote),
            )
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteCasted with multiple votes, then update vote counts properly`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote1 = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val vote2 = pollVoteData("vote-2", "poll-1", "option-2", "user-2")

        activityState.onPollVoteCasted(vote1, "poll-1")
        activityState.onPollVoteCasted(vote2, "poll-1")

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
    fun `on onPollVoteChanged, then update vote`() = runTest {
        val originalVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val initialPoll =
            pollData(
                "poll-1",
                "Test Poll",
                isClosed = false,
                voteCount = 1,
                ownVotes = listOf(originalVote),
                voteCountsByOption = mapOf("option-1" to 1),
                latestVotesByOption = mapOf("option-1" to listOf(originalVote)),
            )
        setupInitialPoll(initialPoll)

        val changedVote = pollVoteData("vote-1", "poll-1", "option-2", currentUserId)
        activityState.onPollVoteChanged(changedVote, "poll-1")

        val expectedPoll =
            initialPoll.copy(
                voteCount = 1,
                ownVotes = listOf(changedVote),
                latestVotesByOption =
                    mapOf("option-1" to emptyList(), "option-2" to listOf(changedVote)),
                voteCountsByOption = mapOf("option-1" to 0, "option-2" to 1),
            )
        assertEquals(expectedPoll, activityState.poll.value)
    }

    @Test
    fun `on onPollVoteChanged with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialPoll(initialPoll)

        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        activityState.onPollVoteChanged(vote, "poll-2")

        assertEquals(initialPoll, activityState.poll.value)
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
    fun `on onPollClosed, then mark poll as closed`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll", isClosed = false)
        setupInitialPoll(initialPoll)

        activityState.onPollClosed(initialPoll)

        val expectedPoll = initialPoll.copy(isClosed = true)
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

    private fun setupInitialPoll(poll: PollData) {
        val activity = activityData(poll = poll)
        activityState.onActivityUpdated(activity)
    }
}
