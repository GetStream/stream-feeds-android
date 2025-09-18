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
    fun `on pollClosed, then update poll`() = runTest {
        val initialPoll = pollData()
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val closedPoll = pollData("poll-1", isClosed = true)
        activityState.onPollClosed(closedPoll)

        assertEquals(closedPoll, activityState.poll.value)
    }

    @Test
    fun `on pollClosed with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val differentPoll = pollData("poll-2", isClosed = true)
        activityState.onPollClosed(differentPoll)

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on pollDeleted, then remove poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        activityState.onPollDeleted("poll-1")

        assertNull(activityState.poll.value)
    }

    @Test
    fun `on pollDeleted with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        activityState.onPollDeleted("poll-2")

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on pollUpdated, then update poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val updatedPoll = pollData("poll-1", name = "Updated Poll")
        activityState.onPollUpdated(updatedPoll)

        assertEquals(updatedPoll, activityState.poll.value)
    }

    @Test
    fun `on pollUpdated with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val differentPoll = pollData("poll-2", name = "Different Poll")
        activityState.onPollUpdated(differentPoll)

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on optionCreated, then add option to poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val newOption = pollOptionData("option-3", "New Option")
        activityState.onOptionCreated(newOption)

        val updatedPoll = activityState.poll.value
        assertEquals(3, updatedPoll?.options?.size)
        assertEquals(newOption, updatedPoll?.options?.last())
    }

    @Test
    fun `on optionDeleted, then remove option from poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        activityState.onOptionDeleted("option-1")

        val updatedPoll = activityState.poll.value
        assertEquals(1, updatedPoll?.options?.size)
        assertEquals("option-2", updatedPoll?.options?.first()?.id)
    }

    @Test
    fun `on optionUpdated, then update option in poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val updatedOption = pollOptionData("option-1", "Updated Option Text")
        activityState.onOptionUpdated(updatedOption)

        val updatedPoll = activityState.poll.value
        assertEquals(updatedOption, updatedPoll?.options?.first())
    }

    @Test
    fun `on pollVoteCasted with poll data, then update poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val updatedPoll = pollData("poll-1", "Test Poll")
        activityState.onPollVoteCasted(vote, updatedPoll)

        assertEquals(updatedPoll, activityState.poll.value)
    }

    @Test
    fun `on pollVoteCasted with poll data different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        val differentPoll = pollData("poll-2")
        activityState.onPollVoteCasted(vote, differentPoll)

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on pollVoteCasted with vote data, then cast vote on poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        activityState.onPollVoteCasted(vote)

        val updatedPoll = activityState.poll.value
        assertEquals(1, updatedPoll?.voteCount)
        assertEquals(1, updatedPoll?.ownVotes?.size)
    }

    @Test
    fun `on pollVoteCasted with null vote, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        activityState.onPollVoteCasted(null)

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on pollVoteChanged, then update poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val vote = pollVoteData("vote-1", "poll-1", "option-2", currentUserId)
        val updatedPoll = pollData("poll-1", "Test Poll")
        activityState.onPollVoteChanged(vote, updatedPoll)

        assertEquals(updatedPoll, activityState.poll.value)
    }

    @Test
    fun `on pollVoteChanged with different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        val differentPoll = pollData("poll-2")
        activityState.onPollVoteChanged(vote, differentPoll)

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on pollVoteRemoved with poll data, then update poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val updatedPoll = pollData("poll-1", "Test Poll")
        activityState.onPollVoteRemoved(vote, updatedPoll)

        assertEquals(updatedPoll, activityState.poll.value)
    }

    @Test
    fun `on pollVoteRemoved with poll data different poll id, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        val vote = pollVoteData("vote-1", "poll-2", "option-1", currentUserId)
        val differentPoll = pollData("poll-2")
        activityState.onPollVoteRemoved(vote, differentPoll)

        assertEquals(initialPoll, activityState.poll.value)
    }

    @Test
    fun `on pollVoteRemoved with vote data, then remove vote from poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        // First add a vote
        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        activityState.onPollVoteCasted(vote)

        // Then remove it
        activityState.onPollVoteRemoved(vote)

        val updatedPoll = activityState.poll.value
        assertEquals(0, updatedPoll?.voteCount)
        assertEquals(0, updatedPoll?.ownVotes?.size)
    }

    @Test
    fun `on pollVoteRemoved with null vote, then keep existing poll`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        val activity = activityData(poll = initialPoll)
        activityState.onActivityUpdated(activity)

        activityState.onPollVoteRemoved(null)

        assertEquals(initialPoll, activityState.poll.value)
    }
}
