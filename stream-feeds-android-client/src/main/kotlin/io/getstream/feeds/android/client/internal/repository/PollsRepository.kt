package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.core.generated.models.CastPollVoteRequest
import io.getstream.feeds.android.core.generated.models.CreatePollOptionRequest
import io.getstream.feeds.android.core.generated.models.CreatePollRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollOptionRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollPartialRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollRequest

/**
 * A repository for managing polls and poll votes.
 */
internal interface PollsRepository {

    /**
     * Closes a poll by its ID.
     *
     * @param pollId The ID of the poll to close.
     */
    suspend fun closePoll(pollId: String): Result<PollData>

    /**
     * Creates a new poll.
     *
     * @param request The request containing the details of the poll to create.
     */
    suspend fun createPoll(request: CreatePollRequest): Result<PollData>

    /**
     * Deletes a poll by its ID.
     *
     * @param pollId The ID of the poll to delete.
     * @param userId Optional user ID for authorization.
     */
    suspend fun deletePoll(pollId: String, userId: String?): Result<Unit>

    /**
     * Retrieves a poll by its ID.
     *
     * @param pollId The ID of the poll to retrieve.
     * @param userId Optional user ID for authorization.
     */
    suspend fun getPoll(pollId: String, userId: String?): Result<PollData>

    /**
     * Updates a poll partially by its ID.
     *
     * @param pollId The ID of the poll to update.
     * @param request The request containing the partial updates for the poll.
     */
    suspend fun updatePollPartial(
        pollId: String,
        request: UpdatePollPartialRequest
    ): Result<PollData>

    /**
     * Updates a poll by its ID.
     *
     * @param request The request containing the updated details of the poll.
     */
    suspend fun updatePoll(request: UpdatePollRequest): Result<PollData>

    /**
     * Creates a new poll option for a given poll.
     *
     * @param pollId The ID of the poll to which the option will be added.
     * @param request The request containing the details of the poll option to create.
     */
    suspend fun createPollOption(pollId: String, request: CreatePollOptionRequest): Result<PollOptionData>

    /**
     * Deletes a poll option by its ID.
     *
     * @param pollId The ID of the poll from which the option will be deleted.
     * @param optionId The ID of the option to delete.
     * @param userId Optional user ID for authorization.
     */
    suspend fun deletePollOption(pollId: String, optionId: String, userId: String?): Result<Unit>

    /**
     * Retrieves a poll option by its ID.
     *
     * @param pollId The ID of the poll containing the option.
     * @param optionId The ID of the option to retrieve.
     * @param userId Optional user ID for authorization.
     */
    suspend fun getPollOption(
        pollId: String,
        optionId: String,
        userId: String?
    ): Result<PollOptionData>

    /**
     * Updates a poll option.
     *
     * @param pollId The ID of the poll containing the option to update.
     * @param request The request containing the updated details of the poll option.
     */
    suspend fun updatePollOption(
        pollId: String,
        request: UpdatePollOptionRequest,
    ): Result<PollOptionData>

    /**
     * Queries polls based on the provided query parameters.
     *
     * @param query The query parameters to filter and paginate the polls.
     */
    suspend fun queryPolls(query: PollsQuery): Result<PaginationResult<PollData>>

    /**
     * Casts a vote in a poll.
     *
     * @param activityId The ID of the activity associated with the poll.
     * @param pollId The ID of the poll to vote in.
     * @param request The request containing the vote details.
     */
    suspend fun castPollVote(
        activityId: String,
        pollId: String,
        request: CastPollVoteRequest,
    ): Result<PollVoteData?>

    /**
     * Queries poll votes based on the provided query parameters.
     *
     * @param query The query parameters to filter and paginate the poll votes.
     */
    suspend fun queryPollVotes(query: PollVotesQuery): Result<PaginationResult<PollVoteData>>

    /**
     * Deletes a poll vote.
     *
     * @param activityId The ID of the activity associated with the poll vote.
     * @param pollId The ID of the poll from which the vote will be deleted.
     * @param voteId The ID of the vote to delete.
     * @param userId Optional user ID for authorization.
     */
    suspend fun deletePollVote(
        activityId: String,
        pollId: String,
        voteId: String,
        userId: String? = null
    ): Result<PollVoteData?>
}