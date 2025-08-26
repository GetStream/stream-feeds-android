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
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityState
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.state.event.handler.ActivityEventHandler
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.client.internal.utils.flatMap
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.CastPollVoteRequest
import io.getstream.feeds.android.network.models.CreatePollOptionRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.network.models.UpdatePollOptionRequest
import io.getstream.feeds.android.network.models.UpdatePollPartialRequest
import io.getstream.feeds.android.network.models.UpdatePollRequest

/**
 * A class representing a single activity in a feed.
 *
 * This class provides methods to interact with an activity including fetching its data, managing
 * comments, handling reactions, and working with polls. It maintains an observable state that
 * automatically updates when WebSocket events are received.
 *
 * Internal implementation of the [Activity] interface.
 *
 * @property activityId The unique identifier of the activity.
 * @property fid The feed ID to which this activity belongs.
 * @property currentUserId The ID of the current user.
 * @property activitiesRepository The repository used to fetch and manage activities.
 * @property commentsRepository The repository used to fetch and manage comments.
 * @property pollsRepository The repository used to fetch and manage polls.
 * @property commentList The list of comments associated with this activity.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class ActivityImpl(
    override val activityId: String,
    override val fid: FeedId,
    private val currentUserId: String,
    private val activitiesRepository: ActivitiesRepository,
    private val commentsRepository: CommentsRepository,
    private val pollsRepository: PollsRepository,
    private val commentList: ActivityCommentListImpl,
    private val subscriptionManager: StreamSubscriptionManager<FeedsEventListener>,
) : Activity {

    private val _state: ActivityStateImpl = ActivityStateImpl(currentUserId, commentList.state)

    private val eventHandler = ActivityEventHandler(fid = fid, state = _state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: ActivityState
        get() = _state

    override suspend fun get(): Result<ActivityData> {
        val activity =
            activitiesRepository.getActivity(activityId).onSuccess { _state.onActivityUpdated(it) }
        // Query the comments as well (state will be updated automatically)
        queryComments()
        return activity
    }

    override suspend fun queryComments(): Result<List<ThreadedCommentData>> {
        return commentList.get()
    }

    override suspend fun queryMoreComments(limit: Int?): Result<List<ThreadedCommentData>> {
        return commentList.queryMoreComments(limit)
    }

    override suspend fun getComment(commentId: String): Result<CommentData> {
        return commentsRepository.getComment(commentId).onSuccess {
            commentList.mutableState.onCommentUpdated(it)
        }
    }

    override suspend fun addComment(
        request: ActivityAddCommentRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?,
    ): Result<CommentData> {
        return commentsRepository
            .addComment(request = request, attachmentUploadProgress = attachmentUploadProgress)
            .onSuccess { commentList.mutableState.onCommentAdded(ThreadedCommentData(it)) }
    }

    override suspend fun addCommentsBatch(
        requests: List<ActivityAddCommentRequest>,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?,
    ): Result<List<CommentData>> {
        return commentsRepository.addCommentsBatch(requests, attachmentUploadProgress).onSuccess { comments ->
            val threadedComments = comments.map(::ThreadedCommentData)
            threadedComments.forEach { threadedComment ->
                commentList.mutableState.onCommentAdded(threadedComment)
            }
        }
    }

    override suspend fun deleteComment(commentId: String, hardDelete: Boolean?): Result<Unit> {
        return commentsRepository.deleteComment(commentId, hardDelete).onSuccess {
            commentList.mutableState.onCommentRemoved(commentId)
        }
    }

    override suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest,
    ): Result<CommentData> {
        return commentsRepository.updateComment(commentId, request).onSuccess {
            commentList.mutableState.onCommentUpdated(it)
        }
    }

    override suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<FeedsReactionData> {
        return commentsRepository
            .addCommentReaction(commentId, request)
            .onSuccess { commentList.mutableState.onCommentReactionAdded(it.second, it.first) }
            .map { it.first }
    }

    override suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
    ): Result<FeedsReactionData> {
        return commentsRepository
            .deleteCommentReaction(commentId, type)
            .onSuccess { commentList.mutableState.onCommentReactionRemoved(it.second, it.first) }
            .map { it.first }
    }

    override suspend fun pin(): Result<Unit> {
        return activitiesRepository
            .pin(activityId, fid)
            .onSuccess { _state.onActivityUpdated(it) }
            .map { Unit }
    }

    override suspend fun unpin(): Result<Unit> {
        return activitiesRepository
            .unpin(activityId, fid)
            .onSuccess { _state.onActivityUpdated(it) }
            .map { Unit }
    }

    override suspend fun closePoll(): Result<PollData> {
        return pollId().flatMap { pollId ->
            pollsRepository.closePoll(pollId = pollId).onSuccess { _state.onPollUpdated(it) }
        }
    }

    override suspend fun deletePoll(userId: String?): Result<Unit> {
        return pollId().flatMap { pollId ->
            pollsRepository.deletePoll(pollId = pollId, userId = userId).onSuccess {
                _state.onPollDeleted(pollId)
            }
        }
    }

    override suspend fun getPoll(userId: String?): Result<PollData> {
        return pollId().flatMap { pollId ->
            pollsRepository.getPoll(pollId = pollId, userId = userId).onSuccess {
                _state.onPollUpdated(it)
            }
        }
    }

    override suspend fun updatePollPartial(request: UpdatePollPartialRequest): Result<PollData> {
        return pollId().flatMap { pollId ->
            pollsRepository.updatePollPartial(pollId, request).onSuccess {
                _state.onPollUpdated(it)
            }
        }
    }

    override suspend fun updatePoll(request: UpdatePollRequest): Result<PollData> {
        return pollsRepository.updatePoll(request).onSuccess { _state.onPollUpdated(it) }
    }

    override suspend fun createPollOption(
        request: CreatePollOptionRequest
    ): Result<PollOptionData> {
        return pollId().flatMap { pollId ->
            pollsRepository.createPollOption(pollId, request).onSuccess {
                _state.onOptionCreated(it)
            }
        }
    }

    override suspend fun deletePollOption(optionId: String, userId: String?): Result<Unit> {
        return pollId().flatMap { pollId ->
            pollsRepository
                .deletePollOption(pollId = pollId, optionId = optionId, userId = userId)
                .onSuccess { _state.onOptionDeleted(optionId) }
        }
    }

    override suspend fun getPollOption(optionId: String, userId: String?): Result<PollOptionData> {
        return pollId().flatMap { pollId ->
            pollsRepository
                .getPollOption(pollId = pollId, optionId = optionId, userId = userId)
                .onSuccess { _state.onOptionUpdated(it) }
        }
    }

    override suspend fun updatePollOption(
        request: UpdatePollOptionRequest
    ): Result<PollOptionData> {
        return pollId().flatMap { pollId ->
            pollsRepository.updatePollOption(pollId, request).onSuccess {
                _state.onOptionUpdated(it)
            }
        }
    }

    override suspend fun castPollVote(request: CastPollVoteRequest): Result<PollVoteData?> {
        return pollId().flatMap { pollId ->
            pollsRepository
                .castPollVote(activityId = activityId, pollId = pollId, request = request)
                .onSuccess { _state.onPollVoteCasted(it) }
        }
    }

    override suspend fun deletePollVote(voteId: String, userId: String?): Result<PollVoteData?> {
        return pollId().flatMap { pollId ->
            pollsRepository
                .deletePollVote(
                    activityId = activityId,
                    pollId = pollId,
                    voteId = voteId,
                    userId = userId,
                )
                .onSuccess { _state.onPollVoteRemoved(it) }
        }
    }

    private suspend fun ensureActivityLoaded(): Result<ActivityData> {
        val activity = _state.activity.value
        return if (activity != null) {
            Result.success(activity)
        } else {
            get()
        }
    }

    private suspend fun pollId(): Result<String> {
        return ensureActivityLoaded().flatMap {
            val poll = it.poll
            if (poll != null) {
                Result.success(poll.id)
            } else {
                Result.failure(IllegalStateException("Activity does not have a poll"))
            }
        }
    }
}
