package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.CastPollVoteRequest
import io.getstream.feeds.android.core.generated.models.CreatePollOptionRequest
import io.getstream.feeds.android.core.generated.models.CreatePollRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollOptionRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollPartialRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollRequest

/**
 * Default implementation of [PollsRepository].
 *
 * Uses [ApiService] to perform network operations.
 *
 * @param api The API service to use for network operations.
 */
internal class PollsRepositoryImpl(private val api: ApiService) : PollsRepository {

    override suspend fun closePoll(pollId: String): Result<PollData> = runCatching {
        val request = UpdatePollPartialRequest(set = mapOf("is_closed" to true))
        api.updatePollPartial(pollId, request).poll.toModel()
    }

    override suspend fun createPoll(request: CreatePollRequest): Result<PollData> = runCatching {
        api.createPoll(request).poll.toModel()
    }

    override suspend fun deletePoll(
        pollId: String,
        userId: String?
    ): Result<Unit> = runCatching {
        api.deletePoll(pollId = pollId, userId = userId)
    }

    override suspend fun getPoll(
        pollId: String,
        userId: String?
    ): Result<PollData> = runCatching {
        api.getPoll(pollId = pollId, userId = userId).poll.toModel()
    }

    override suspend fun updatePollPartial(
        pollId: String,
        request: UpdatePollPartialRequest
    ): Result<PollData> = runCatching {
        api.updatePollPartial(pollId, request).poll.toModel()
    }

    override suspend fun updatePoll(request: UpdatePollRequest): Result<PollData> = runCatching {
        api.updatePoll(request).poll.toModel()
    }

    override suspend fun createPollOption(
        pollId: String,
        request: CreatePollOptionRequest
    ): Result<PollOptionData> = runCatching {
        api.createPollOption(pollId, request).pollOption.toModel()
    }

    override suspend fun deletePollOption(
        pollId: String,
        optionId: String,
        userId: String?
    ): Result<Unit> = runCatching {
        api.deletePollOption(pollId = pollId, optionId = optionId, userId = userId)
    }

    override suspend fun getPollOption(
        pollId: String,
        optionId: String,
        userId: String?
    ): Result<PollOptionData> = runCatching {
        api.getPollOption(
            pollId = pollId,
            optionId = optionId,
            userId = userId
        ).pollOption.toModel()
    }

    override suspend fun updatePollOption(
        pollId: String,
        request: UpdatePollOptionRequest
    ): Result<PollOptionData> = runCatching {
        api.updatePollOption(pollId, request).pollOption.toModel()
    }

    override suspend fun queryPolls(
        query: PollsQuery,
    ): Result<PaginationResult<PollData>> = runCatching {
        val response = api.queryPolls(
            userId = null,
            queryPollsRequest = query.toRequest(),
        )
        PaginationResult(
            models = response.polls.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun castPollVote(
        activityId: String,
        pollId: String,
        request: CastPollVoteRequest
    ): Result<PollVoteData?> = runCatching {
        api.castPollVote(
            activityId = activityId,
            pollId = pollId,
            castPollVoteRequest = request,
        ).vote?.toModel()
    }

    override suspend fun queryPollVotes(
        query: PollVotesQuery,
    ): Result<PaginationResult<PollVoteData>> = runCatching {
        val response = api.queryPollVotes(
            pollId = query.pollId,
            userId = query.userId,
            queryPollVotesRequest = query.toRequest(),
        )
        PaginationResult(
            models = response.votes.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun deletePollVote(
        activityId: String,
        pollId: String,
        voteId: String,
        userId: String?
    ): Result<PollVoteData?> = runCatching {
        api.deletePollVote(
            activityId = activityId,
            pollId = pollId,
            voteId = voteId,
            userId = userId
        ).vote?.toModel()
    }
}