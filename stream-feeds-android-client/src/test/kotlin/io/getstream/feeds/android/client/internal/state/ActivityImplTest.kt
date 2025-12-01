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

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.FeedOwnDataRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.state.event.FidScope
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollOptionData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.client.internal.test.TestSubscriptionManager
import io.getstream.feeds.android.network.models.ActivityFeedbackRequest
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
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

internal class ActivityImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk()
    private val commentsRepository: CommentsRepository = mockk()
    private val pollsRepository: PollsRepository = mockk()
    private val feedOwnDataRepository: FeedOwnDataRepository = mockk(relaxed = true)
    private val commentListState = mockk<ActivityCommentListMutableState>(relaxed = true)
    private val activityCommentListImpl: ActivityCommentListImpl = mockk {
        every { state } returns commentListState
    }
    private val stateEventListener: StateUpdateEventListener = mockk(relaxed = true)
    private val fid = FeedId("group:feed")

    private val activity =
        ActivityImpl(
            activityId = "activityId",
            fid = fid,
            currentUserId = "currentUserId",
            activitiesRepository = activitiesRepository,
            commentsRepository = commentsRepository,
            pollsRepository = pollsRepository,
            feedOwnDataRepository = feedOwnDataRepository,
            commentList = activityCommentListImpl,
            subscriptionManager = TestSubscriptionManager(stateEventListener),
        )

    @Test
    fun `on addComment, delegate to repository and notify state`() = runTest {
        val request = ActivityAddCommentRequest(activityId = "activityId", comment = "Comment")
        val progress = { _: FeedUploadPayload, _: Double -> }
        val commentData = commentData("id")
        coEvery { commentsRepository.addComment(any(), any()) } returns Result.success(commentData)

        activity.addComment(request, progress)

        verify { stateEventListener.onEvent(CommentAdded(FidScope.unknown, commentData)) }
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

        commentData.forEach { data ->
            verify { stateEventListener.onEvent(CommentAdded(FidScope.unknown, data)) }
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
        verify { stateEventListener.onEvent(ActivityUpdated(FidScope.unknown, activityData)) }
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
        verify { stateEventListener.onEvent(CommentUpdated(FidScope.unknown, comment)) }
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
        assertEquals(expectedActivity, activity.state.activity.value)
        verify {
            stateEventListener.onEvent(CommentDeleted(FidScope.unknown, deleteData.first))
            stateEventListener.onEvent(ActivityUpdated(FidScope.unknown, expectedActivity))
        }
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
        verify { stateEventListener.onEvent(CommentUpdated(FidScope.unknown, updatedComment)) }
    }

    @Test
    fun `on addCommentReaction, delegate to repository and update state`() = runTest {
        val commentId = "comment1"
        val request = AddCommentReactionRequest(type = "like")
        val reactionData = feedsReactionData(type = "like")
        val commentData = commentData(commentId)
        coEvery { commentsRepository.addCommentReaction(commentId, request) } returns
            Result.success(Pair(reactionData, commentData))

        val result = activity.addCommentReaction(commentId, request)

        assertEquals(reactionData, result.getOrNull())
        verify {
            stateEventListener.onEvent(
                StateUpdateEvent.CommentReactionUpserted(
                    FidScope.unknown,
                    commentData,
                    reactionData,
                    false,
                )
            )
        }
    }

    @Test
    fun `on deleteCommentReaction, delegate to repository and update state`() = runTest {
        val commentId = "comment1"
        val type = "like"
        val reactionData = feedsReactionData(type = type)
        val commentData = commentData(commentId)
        coEvery { commentsRepository.deleteCommentReaction(commentId, type) } returns
            Result.success(Pair(reactionData, commentData))

        val result = activity.deleteCommentReaction(commentId, type)

        assertEquals(reactionData, result.getOrNull())
        verify {
            stateEventListener.onEvent(
                CommentReactionDeleted(FidScope.unknown, commentData, reactionData)
            )
        }
    }

    @Test
    fun `on pin, delegate to repository and update state`() = runTest {
        val activityData = activityData("activityId")
        coEvery { activitiesRepository.pin("activityId", fid) } returns Result.success(activityData)

        val result = activity.pin()

        assertEquals(Unit, result.getOrNull())
        assertEquals(activityData, activity.state.activity.value)
        verify { stateEventListener.onEvent(ActivityUpdated(FidScope.unknown, activityData)) }
    }

    @Test
    fun `on unpin, delegate to repository and update state`() = runTest {
        val activityData = activityData("activityId")
        coEvery { activitiesRepository.unpin("activityId", fid) } returns
            Result.success(activityData)

        val result = activity.unpin()

        assertEquals(Unit, result.getOrNull())
        assertEquals(activityData, activity.state.activity.value)
        verify { stateEventListener.onEvent(ActivityUpdated(FidScope.unknown, activityData)) }
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
        verify { stateEventListener.onEvent(PollUpdated(updatedPoll)) }
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
        verify { stateEventListener.onEvent(PollUpdated(closedPoll)) }
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
            verify { stateEventListener.onEvent(StateUpdateEvent.PollDeleted("poll-1")) }
        }

    @Test
    fun `on createPollOption when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1", options = listOf(pollOptionData("option-1", "Option 1")))
            val request = CreatePollOptionRequest(text = "New Option")
            val option = pollOptionData("option-2", "New Option")
            val expectedPoll =
                poll.copy(
                    options =
                        listOf(
                            pollOptionData("option-1", "Option 1"),
                            pollOptionData("option-2", "New Option"),
                        )
                )

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.createPollOption("poll-1", request) } returns
                Result.success(option)

            val result = activity.createPollOption(request)

            assertEquals(option, result.getOrNull())
            assertEquals(expectedPoll, activity.state.poll.value)
            verify { stateEventListener.onEvent(PollUpdated(expectedPoll)) }
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
            verify { stateEventListener.onEvent(StateUpdateEvent.PollVoteCasted("poll-1", vote)) }
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
            verify { stateEventListener.onEvent(StateUpdateEvent.PollVoteRemoved("poll-1", vote)) }
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
            verify { stateEventListener.onEvent(PollUpdated(updatedPoll)) }
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
            verify { stateEventListener.onEvent(PollUpdated(updatedPoll)) }
        }

    @Test
    fun `on deletePollOption when activity has poll, delegate to repository`() = runTest {
        val poll =
            pollData(
                "poll-1",
                options = listOf(pollOptionData("option-1"), pollOptionData("option-2")),
            )
        val optionId = "option-1"
        val userId = "user-1"
        val expectedPoll = poll.copy(options = listOf(pollOptionData("option-2")))

        setupActivityWithPoll(poll)
        coEvery { pollsRepository.deletePollOption("poll-1", optionId, userId) } returns
            Result.success(Unit)

        val result = activity.deletePollOption(optionId, userId)

        assertEquals(Unit, result.getOrNull())
        assertEquals(expectedPoll, activity.state.poll.value)
        verify { stateEventListener.onEvent(PollUpdated(expectedPoll)) }
    }

    @Test
    fun `on getPollOption when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1")
            val optionId = "option-1"
            val userId = "user-1"
            val option = pollOptionData(optionId, text = "Updated text")
            val expectedPoll = poll.copy(options = listOf(option))

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.getPollOption("poll-1", optionId, userId) } returns
                Result.success(option)

            val result = activity.getPollOption(optionId, userId)

            assertEquals(option, result.getOrNull())
            verify { stateEventListener.onEvent(PollUpdated(expectedPoll)) }
        }

    @Test
    fun `on updatePollOption when activity has poll, delegate to repository and update state`() =
        runTest {
            val poll = pollData("poll-1", options = listOf(pollOptionData("option-1")))
            val updatedOption = pollOptionData("option-1", text = "Updated Option")
            val request = UpdatePollOptionRequest(id = "option-1", text = "Updated Option")
            val expectedPoll =
                poll.copy(options = listOf(pollOptionData("option-1", "Updated Option")))

            setupActivityWithPoll(poll)
            coEvery { pollsRepository.updatePollOption("poll-1", request) } returns
                Result.success(updatedOption)

            val result = activity.updatePollOption(request)

            assertEquals(updatedOption, result.getOrNull())
            assertEquals(expectedPoll, activity.state.poll.value)
            verify { stateEventListener.onEvent(PollUpdated(expectedPoll)) }
        }

    @Test
    fun `on activityFeedback, delegate to repository`() = runTest {
        val request = ActivityFeedbackRequest(hide = true)

        coEvery { activitiesRepository.activityFeedback("activityId", request) } returns
            Result.success(Unit)

        val result = activity.activityFeedback(request)

        assertEquals(Unit, result.getOrNull())
        coVerify { activitiesRepository.activityFeedback("activityId", request) }
    }

    private suspend fun setupActivityWithPoll(poll: PollData = pollData("poll-1")) {
        val activityWithPoll = activityData("activityId", poll = poll)
        coEvery { activitiesRepository.getActivity("activityId") } returns
            Result.success(activityWithPoll)
        coEvery { activityCommentListImpl.get() } returns Result.success(emptyList())
        activity.get()
    }
}
