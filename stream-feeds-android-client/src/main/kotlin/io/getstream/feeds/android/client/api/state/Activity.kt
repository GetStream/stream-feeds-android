package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import io.getstream.feeds.android.core.generated.models.CastPollVoteRequest
import io.getstream.feeds.android.core.generated.models.CreatePollOptionRequest
import io.getstream.feeds.android.core.generated.models.UpdateCommentRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollOptionRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollPartialRequest
import io.getstream.feeds.android.core.generated.models.UpdatePollRequest

/**
 * A class representing a single activity in a feed.
 *
 * This class provides methods to interact with an activity including fetching its data,
 * managing comments, handling reactions, and working with polls. It maintains an observable state
 * that automatically updates when WebSocket events are received.
 */
public interface Activity {

    /**
     * The unique identifier of this activity.
     */
    public val activityId: String

    /**
     * The feed id for the activity.
     */
    public val fid: FeedId

    /**
     * An observable object representing the current state of the activity.
     */
    public val state: ActivityState

    /**
     * Fetches the current state of the activity.
     * This method retrieves the latest activity data from the server and updates the local state.
     *
     * @return A [Result] containing the current [ActivityData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun get(): Result<ActivityData>

    /**
     * Queries comments for this activity.
     *
     * @return A [Result] containing a list of [ThreadedCommentData] if successful, or an error if
     * the operation fails.
     */
    public suspend fun queryComments(): Result<List<ThreadedCommentData>>

    /**
     * Loads the next page of comments if more are available.
     *
     * This method fetches additional comments using the pagination information
     * from the previous request. If no more comments are available, an empty
     * array is returned.
     *
     * @param limit Optional limit for the number of comments to fetch. If not specified, uses the
     * limit from the original query.
     * @return A [Result] containing a list of [ThreadedCommentData] if successful, or an error if
     * the operation fails. If no more comments are available, an empty list is returned.
     */
    public suspend fun queryMoreComments(limit: Int? = null): Result<List<ThreadedCommentData>>

    /**
     * Retrieves a specific comment by its identifier.
     *
     * @param commentId The unique identifier of the comment to retrieve.
     * @return A [Result] containing the [CommentData] if successful, or an error if the operation
     * fails.
     */
    public suspend fun getComment(commentId: String): Result<CommentData>

    /**
     * Adds a new comment to the activity.
     *
     * @param request The request object containing the details of the comment to be added.
     * @return A [Result] containing the newly created [CommentData] if successful, or an error if
     * the operation fails.
     */
    public suspend fun addComment(request: ActivityAddCommentRequest): Result<CommentData>

    /**
     * Adds multiple comments to the activity in a single batch operation.
     *
     * @param requests A list of [ActivityAddCommentRequest] objects, each representing a comment to
     * be added.
     * @return A [Result] containing a list of [CommentData] for each successfully added comment, or
     * an error if the operation fails.
     */
    public suspend fun addCommentsBatch(
        requests: List<ActivityAddCommentRequest>,
    ): Result<List<CommentData>>

    /**
     * Deletes a comment from the activity.
     *
     * @param commentId The unique identifier of the comment to be deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteComment(commentId: String): Result<Unit>

    /**
     * Updates an existing comment in the activity.
     *
     * @param commentId The unique identifier of the comment to be updated.
     * @param request The request object containing the updated details of the comment.
     * @return A [Result] containing the updated [CommentData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest,
    ): Result<CommentData>

    /**
     * Adds a reaction to a comment.
     *
     * @param commentId The unique identifier of the comment to which the reaction is added.
     * @param request The request object containing the details of the reaction to be added.
     * @return A [Result] containing the [FeedsReactionData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<FeedsReactionData>

    /**
     * Removes a reaction from a comment.
     *
     * @param commentId The unique identifier of the comment from which the reaction is removed.
     * @param type The type of the reaction to be removed.
     * @return A [Result] containing the updated [FeedsReactionData] if successful, or an error if
     * the operation fails.
     */
    public suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
    ): Result<FeedsReactionData>

    /**
     * Pins an activity in the feed.
     *
     * @return A [Result] indicating success or failure of the pinning operation.
     */
    public suspend fun pin(): Result<Unit>

    /**
     * Unpins an activity from the feed.
     *
     * @return A [Result] indicating success or failure of the unpinning operation.
     */
    public suspend fun unpin(): Result<Unit>

    /**
     * Closes a poll, preventing further votes.
     *
     * @return A [Result] containing the updated [PollData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun closePoll(): Result<PollData>

    /**
     * Deletes the activity from the feed.
     *
     * @param userId Optional user identifier for authorization.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deletePoll(userId: String? = null): Result<Unit>

    /**
     * Gets a specific poll by its identifier.
     *
     * @param userId Optional user identifier for user-specific poll data.
     * @return A [Result] containing the [PollData] if successful, or an error if the operation
     * fails.
     */
    public suspend fun getPoll(userId: String? = null): Result<PollData>

    /**
     * Updates a poll with partial data.
     *
     * @param request The request containing the partial update data.
     * @return A [Result] containing the updated [PollData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun updatePollPartial(request: UpdatePollPartialRequest): Result<PollData>

    /**
     * Updates a poll.
     *
     * @param request The request containing the update data.
     * @return A [Result] containing the updated [PollData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun updatePoll(request: UpdatePollRequest): Result<PollData>

    /**
     * Creates a new option for a poll.
     *
     * @param request The request containing the details of the poll option to be created.
     * @return A [Result] containing the newly created [PollOptionData] if successful, or an error
     * if the operation fails.
     */
    public suspend fun createPollOption(request: CreatePollOptionRequest): Result<PollOptionData>

    /**
     * Removes a poll option.
     *
     * @param optionId The unique identifier of the poll option to be removed.
     * @param userId Optional user identifier for authorization.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deletePollOption(optionId: String, userId: String? = null): Result<Unit>

    /**
     * Retrieves a specific poll option by its identifier.
     *
     * @param optionId The unique identifier of the poll option to retrieve.
     * @param userId Optional user identifier for user-specific poll option data.
     * @return A [Result] containing the [PollOptionData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun getPollOption(
        optionId: String,
        userId: String? = null,
    ): Result<PollOptionData>

    /**
     * Updates a poll option.
     *
     * @param request The request containing the updated details of the poll option.
     * @return A [Result] containing the updated [PollOptionData] if successful, or an error if the
     * operation fails.
     */
    public suspend fun updatePollOption(request: UpdatePollOptionRequest): Result<PollOptionData>

    /**
     * Casts a vote in a poll.
     *
     * @param request The request containing the vote data.
     * @return A [Result] containing the updated [PollVoteData] if successful, 'null' if the vote
     * was not created, or an error if the operation fails.
     */
    public suspend fun castPollVote(request: CastPollVoteRequest): Result<PollVoteData?>

    /**
     * Removes a vote from a poll.
     *
     * @param voteId The unique identifier of the vote to be removed.
     * @param userId Optional user identifier for authorization.
     * @return A [Result] containing the deleted [PollVoteData] if successful, 'null' if the vote
     * was not found, or an error if the operation fails.
     */
    public suspend fun deletePollVote(voteId: String, userId: String? = null): Result<PollVoteData?>
}