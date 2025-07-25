package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.CastPollVoteRequest

/**
 * Default implementation of [PollsRepository].
 *
 * Uses [ApiService] to perform network operations.
 *
 * @param api The API service to use for network operations.
 */
internal class PollsRepositoryImpl(private val api: ApiService) : PollsRepository {

    override suspend fun closePoll(pollId: String): Result<PollData> = runCatching {
        TODO("Not yet implemented")
    }

    override suspend fun deletePoll(
        pollId: String,
        userId: String?
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getPoll(
        pollId: String,
        userId: String?
    ): Result<PollData> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePollOption(
        pollId: String,
        optionId: String,
        userId: String?
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getPollOption(
        pollId: String,
        optionId: String,
        userId: String?
    ): Result<PollOptionData> {
        TODO("Not yet implemented")
    }

    override suspend fun queryPolls(query: PollsQuery): Result<PaginationResult<PollData>> {
        TODO("Not yet implemented")
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

    override suspend fun queryPollVotes(query: PollVotesQuery): Result<PaginationResult<PollVoteData>> {
        TODO("Not yet implemented")
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