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

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollOptionData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.CastPollVoteRequest
import io.getstream.feeds.android.network.models.CreatePollOptionRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.network.models.UpdatePollOptionRequest
import io.getstream.feeds.android.network.models.UpdatePollPartialRequest
import io.getstream.feeds.android.network.models.UpdatePollRequest
import io.getstream.feeds.android.network.models.VoteData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

internal class ActivityImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk()
    private val commentsRepository: CommentsRepository = mockk()
    private val pollsRepository: PollsRepository = mockk()
    private val commentListState = mockk<ActivityCommentListMutableState>(relaxed = true)
    private val activityCommentListImpl: ActivityCommentListImpl = mockk {
        every { state } returns commentListState
        every { mutableState } returns commentListState
    }
    private val subscriptionManager: StreamSubscriptionManager<FeedsEventListener> =
        mockk(relaxed = true)

    private val activity =
        ActivityImpl(
            activityId = "activityId",
            fid = FeedId("feedId"),
            currentUserId = "currentUserId",
            activitiesRepository = activitiesRepository,
            commentsRepository = commentsRepository,
            pollsRepository = pollsRepository,
            commentList = activityCommentListImpl,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on addComment, delegate to repository and notify state`() = runTest {
        val request = ActivityAddCommentRequest(activityId = "activityId", comment = "Comment")
        val progress = { _: FeedUploadPayload, _: Double -> }
        val commentData = commentData("id")
        coEvery { commentsRepository.addComment(any(), any()) } returns Result.success(commentData)

        activity.addComment(request, progress)

        coVerify { commentListState.onCommentAdded(ThreadedCommentData(commentData)) }
    }

    @Test
    fun `on addCommentsBatch, delegate to repository and notify state`() = runTest {
        val requests =
            listOf(
                ActivityAddCommentRequest(activityId = "activityId", comment = "Comment 1"),
                ActivityAddCommentRequest(activityId = "activityId", comment = "Comment 2"),
            )
        val commentData = listOf(commentData("id1"), commentData("id2"))
        coEvery { commentsRepository.addCommentsBatch(requests) } returns
            Result.success(commentData)

        activity.addCommentsBatch(requests)

        coVerify {
            commentData.forEach { data ->
                commentListState.onCommentAdded(ThreadedCommentData(data))
            }
        }
    }

    @Test
    fun `on get, delegate to repository and update state`() = runTest {
        val activityData = activityData("activityId")
        val commentsResult = defaultPaginationResult(listOf(commentData("comment1")))
        coEvery { activitiesRepository.getActivity("activityId") } returns
            Result.success(activityData)
        coEvery { activityCommentListImpl.get() } returns
            Result.success(commentsResult.models.map(::ThreadedCommentData))

        val result = activity.get()

        assertEquals(activityData, result.getOrNull())
        assertEquals(activityData, activity.state.activity.value)
    }

    @Test
    fun `on queryComments, delegate to comment list`() = runTest {
        val commentsResult =
            listOf(commentData("comment1"), commentData("comment2")).map(::ThreadedCommentData)
        coEvery { activityCommentListImpl.get() } returns Result.success(commentsResult)

        val result = activity.queryComments()

        assertEquals(commentsResult, result.getOrNull())
        coVerify { activityCommentListImpl.get() }
    }

    @Test
    fun `on queryMoreComments, delegate to comment list with limit`() = runTest {
        val commentsResult = listOf(commentData("comment3")).map(::ThreadedCommentData)
        val limit = 10
        coEvery { activityCommentListImpl.queryMoreComments(limit) } returns
            Result.success(commentsResult)

        val result = activity.queryMoreComments(limit)

        assertEquals(commentsResult, result.getOrNull())
        coVerify { activityCommentListImpl.queryMoreComments(limit) }
    }

    @Test
    fun `on getComment, delegate to repository and update state`() = runTest {
        val commentId = "comment1"
        val comment = commentData(commentId)
        coEvery { commentsRepository.getComment(commentId) } returns Result.success(comment)

        val result = activity.getComment(commentId)

        assertEquals(comment, result.getOrNull())
        coVerify { commentListState.onCommentUpdated(comment) }
    }

    @Test
    fun `on deleteComment, delegate to repository and notify state`() = runTest {
        val commentId = "comment1"
        val hardDelete = true
        val expectedActivity = activityData(id = "activityId", text = "Updated activity")
        val deleteData = commentData(commentId) to expectedActivity
        coEvery { commentsRepository.deleteComment(commentId, hardDelete) } returns
            Result.success(deleteData)

        val result = activity.deleteComment(commentId, hardDelete)

        assertEquals(Unit, result.getOrNull())
        coVerify { commentListState.onCommentRemoved(commentId) }
        assertEquals(expectedActivity, activity.state.activity.value)
    }

    @Test
    fun `on updateComment, delegate to repository and update state`() = runTest {
        val commentId = "comment1"
        val request = UpdateCommentRequest(comment = "Updated comment")
        val updatedComment = commentData(commentId, text = "Updated comment")
        coEvery { commentsRepository.updateComment(commentId, request) } returns
            Result.success(updatedComment)

        val result = activity.updateComment(commentId, request)

        assertEquals(updatedComment, result.getOrNull())
        coVerify { commentListState.onCommentUpdated(updatedComment) }
    }

    @Test
    fun `on addCommentReaction, delegate to repository and update state`() = runTest {
        val commentId = "comment1"
        val request = AddCommentReactionRequest(type = "like")
        val reactionData = feedsReactionData(type = "like")
        coEvery { commentsRepository.addCommentReaction(commentId, request) } returns
            Result.success(Pair(reactionData, commentId))

        val result = activity.addCommentReaction(commentId, request)

        assertEquals(reactionData, result.getOrNull())
        coVerify { commentListState.onCommentReactionAdded(commentId, reactionData) }
    }

    @Test
    fun `on deleteCommentReaction, delegate to repository and update state`() = runTest {
        val commentId = "comment1"
        val type = "like"
        val reactionData = feedsReactionData(type = type)
        coEvery { commentsRepository.deleteCommentReaction(commentId, type) } returns
            Result.success(Pair(reactionData, commentId))

        val result = activity.deleteCommentReaction(commentId, type)

        assertEquals(reactionData, result.getOrNull())
        coVerify { commentListState.onCommentReactionRemoved(commentId, reactionData) }
    }

    @Test
    fun `on pin, delegate to repository and update state`() = runTest {
        val activityData = activityData("activityId")
        coEvery { activitiesRepository.pin("activityId", FeedId("feedId")) } returns
            Result.success(activityData)

        val result = activity.pin()

        assertEquals(Unit, result.getOrNull())
        assertEquals(activityData, activity.state.activity.value)
    }

    @Test
    fun `on unpin, delegate to repository and update state`() = runTest {
        val activityData = activityData("activityId")
        coEvery { activitiesRepository.unpin("activityId", FeedId("feedId")) } returns
            Result.success(activityData)

        val result = activity.unpin()

        assertEquals(Unit, result.getOrNull())
        assertEquals(activityData, activity.state.activity.value)
    }

    @Test
    fun `on getPoll when activity has poll, delegate to repository and update state`() = runTest {
        val poll = pollData("poll-1")
        val updatedPoll = pollData("poll-1", name = "Updated Poll")

        setupActivityWithPoll(poll)
        coEvery { pollsRepository.getPoll("poll-1", "currentUserId") } returns
            Result.success(updatedPoll)

        val result = activity.getPoll("currentUserId")

        assertEquals(updatedPoll, result.getOrNull())
        assertEquals(updatedPoll, activity.state.poll.value)
    }

    @Test
    fun `on closePoll when activity has poll, delegate to repository and update state`() = runTest {
        val poll = pollData("poll-1")
        val closedPoll = pollData("poll-1", isClosed = true)

        setupActivityWithPoll(poll)
        coEvery { pollsRepository.closePoll("poll-1") } returns Result.success(closedPoll)

        val result = activity.closePoll()

        assertEquals(closedPoll, result.getOrNull())
        assertEquals(closedPoll, activity.state.poll.value)
    }

    @Test
    fun `on deletePoll when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.deletePoll("poll-1", "currentUserId") } returns
                Result.success(Unit)

            val result = activity.deletePoll("currentUserId")

            assertEquals(Unit, result.getOrNull())
            assertEquals(null, activity.state.poll.value)
        }

    @Test
    fun `on createPollOption when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val request = CreatePollOptionRequest(text = "New Option")
            val option = pollOptionData("option-3", "New Option")
            val expectedOptions =
                listOf(
                    pollOptionData("option-1", "Test Option"),
                    pollOptionData("option-2", "Test Option 2"),
                    pollOptionData("option-3", "New Option"),
                )

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.createPollOption("poll-1", request) } returns
                Result.success(option)

            val result = activity.createPollOption(request)

            assertEquals(option, result.getOrNull())
            assertEquals(expectedOptions, activity.state.poll.value?.options)
        }

    @Test
    fun `on castPollVote when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val request = CastPollVoteRequest(vote = VoteData(optionId = "option-1"))
            val vote = pollVoteData("vote-1", "poll-1", "option-1")

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.castPollVote("activityId", "poll-1", request) } returns
                Result.success(vote)

            val result = activity.castPollVote(request)

            assertEquals(vote, result.getOrNull())
            val actualPoll = activity.state.poll.value!!
            assertEquals(1, actualPoll.voteCount)
            assertEquals(1, actualPoll.voteCountsByOption["option-1"])
            assertEquals(listOf(vote), actualPoll.latestVotesByOption["option-1"])
        }

    @Test
    fun `on deletePollVote when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val vote = pollVoteData("vote-1", "poll-1", "option-1")

            setupActivityWithPoll(poll)
            coEvery {
                pollsRepository.deletePollVote("activityId", "poll-1", "vote-1", "currentUserId")
            } returns Result.success(vote)

            val result = activity.deletePollVote("vote-1", "currentUserId")

            assertEquals(vote, result.getOrNull())
            assertNotNull("Poll should still exist", activity.state.poll.value)
        }

    @Test
    fun `on updatePollPartial when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val updatedPoll = pollData("poll-1", name = "Updated Poll")
            val request = UpdatePollPartialRequest(set = mapOf("name" to "Updated Poll"))

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.updatePollPartial("poll-1", request) } returns
                Result.success(updatedPoll)

            val result = activity.updatePollPartial(request)

            assertEquals(updatedPoll, result.getOrNull())
            assertEquals(updatedPoll, activity.state.poll.value)
        }

    @Test
    fun `on updatePoll when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val updatedPoll = pollData("poll-1", name = "Updated Poll")
            val request = UpdatePollRequest(id = "poll-1", name = "Updated Poll")

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.updatePoll(request) } returns Result.success(updatedPoll)

            val result = activity.updatePoll(request)

            assertEquals(updatedPoll, result.getOrNull())
            assertEquals(updatedPoll, activity.state.poll.value)
        }

    @Test
    fun `on deletePollOption when activity has poll, delegate to repository`() = runTest {
        val poll = pollData("poll-1")
        val optionId = "option-1"
        val userId = "user-1"
        val expectedOptions = listOf(pollOptionData("option-2", "Test Option 2"))

        setupActivityWithPoll(poll)
        coEvery { pollsRepository.deletePollOption("poll-1", optionId, userId) } returns
            Result.success(Unit)

        val result = activity.deletePollOption(optionId, userId)

        assertEquals(Unit, result.getOrNull())
        assertEquals(expectedOptions, activity.state.poll.value?.options)
    }

    @Test
    fun `on getPollOption when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val optionId = "option-1"
            val userId = "user-1"
            val option = pollOptionData(optionId)

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.getPollOption("poll-1", optionId, userId) } returns
                Result.success(option)

            val result = activity.getPollOption(optionId, userId)

            assertEquals(option, result.getOrNull())
        }

    @Test
    fun `on updatePollOption when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val updatedOption = pollOptionData("option-1", text = "Updated Option")
            val request = UpdatePollOptionRequest(id = "option-1", text = "Updated Option")
            val expectedOptions =
                listOf(
                    pollOptionData("option-1", "Updated Option"),
                    pollOptionData("option-2", "Test Option 2"),
                )

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.updatePollOption("poll-1", request) } returns
                Result.success(updatedOption)

            val result = activity.updatePollOption(request)

            assertEquals(updatedOption, result.getOrNull())
            assertEquals(expectedOptions, activity.state.poll.value?.options)
        }

    private suspend fun setupActivityWithPoll(poll: PollData = pollData("poll-1")) {
        val activityWithPoll = activityData("activityId", poll = poll)
        coEvery { activitiesRepository.getActivity("activityId") } returns
            Result.success(activityWithPoll)
        coEvery { activityCommentListImpl.get() } returns Result.success(emptyList())
        activity.get()
    }
}
