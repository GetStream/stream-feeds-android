package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import io.getstream.feeds.android.core.generated.models.AddCommentRequest
import io.getstream.feeds.android.core.generated.models.AddCommentsBatchRequest
import io.getstream.feeds.android.core.generated.models.UpdateCommentRequest

/**
 * Default implementation of the [CommentsRepository] interface.
 *
 * Uses the provided [ApiService] to perform network requests related to comments.
 *
 * @property api The API service used to perform network requests.
 */
internal class CommentsRepositoryImpl(private val api: ApiService) : CommentsRepository {

    override suspend fun queryComments(
        query: CommentsQuery,
    ): Result<PaginationResult<CommentData>> = runSafely {
        val response = api.queryComments(query.toRequest())
        PaginationResult(
            models = response.comments.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun getComments(
        query: ActivityCommentsQuery,
    ): Result<PaginationResult<ThreadedCommentData>> = runSafely {
        val response = api.getComments(
            objectId = query.objectId,
            objectType = query.objectType,
            depth = query.depth,
            sort = query.sort?.toRequest()?.value,
            limit = query.limit,
            next = query.next,
            prev = query.previous,
        )
        PaginationResult(
            models = response.comments.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun addComment(request: AddCommentRequest): Result<CommentData> = runSafely {
        api.addComment(request).comment.toModel()
    }

    override suspend fun addCommentsBatch(
        request: AddCommentsBatchRequest,
    ): Result<List<CommentData>> = runSafely {
        api.addCommentsBatch(request).comments.map { it.toModel() }
    }

    override suspend fun deleteComment(
        commentId: String,
        hardDelete: Boolean?,
    ): Result<Unit> = runSafely {
        api.deleteComment(commentId, hardDelete)
    }

    override suspend fun getComment(commentId: String): Result<CommentData> = runSafely {
        api.getComment(commentId).comment.toModel()
    }

    override suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest,
    ): Result<CommentData> = runSafely {
        api.updateComment(commentId, request).comment.toModel()
    }

    override suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<Pair<FeedsReactionData, String>> = runSafely {
        val response = api.addCommentReaction(commentId, request)
        Pair(response.reaction.toModel(), response.comment.id)
    }

    override suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
    ): Result<Pair<FeedsReactionData, String>> = runSafely {
        val response = api.deleteCommentReaction(id = commentId, type = type)
        Pair(response.reaction.toModel(), response.comment.id)
    }

    override suspend fun queryCommentReactions(
        commentId: String,
        query: CommentReactionsQuery,
    ): Result<PaginationResult<FeedsReactionData>> = runSafely {
        val response = api.queryCommentReactions(commentId, query.toRequest())
        PaginationResult(
            models = response.reactions.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun getCommentReplies(
        query: CommentRepliesQuery,
    ): Result<PaginationResult<ThreadedCommentData>> = runSafely {
        val response = api.getCommentReplies(
            id = query.commentId,
            depth = query.depth,
            limit = query.limit,
            next = query.next,
            prev = query.previous,
            repliesLimit = query.repliesLimit,
            sort = query.sort?.toRequest()?.value,
        )
        PaginationResult(
            models = response.comments.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }
}
