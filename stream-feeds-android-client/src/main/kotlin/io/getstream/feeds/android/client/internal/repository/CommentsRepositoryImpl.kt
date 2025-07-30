package io.getstream.feeds.android.client.internal.repository

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
    ): Result<PaginationResult<CommentData>> = runCatching {
        val response = api.queryComments(query.toRequest())
        PaginationResult(
            models = response.comments.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun getComments(
        query: ActivityCommentsQuery,
    ): Result<PaginationResult<ThreadedCommentData>> = runCatching {
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

    override suspend fun addComment(request: AddCommentRequest): Result<CommentData> = runCatching {
        api.addComment(request).comment.toModel()
    }

    override suspend fun addCommentsBatch(
        request: AddCommentsBatchRequest,
    ): Result<List<CommentData>> = runCatching {
        api.addCommentsBatch(request).comments.map { it.toModel() }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> = runCatching {
        api.deleteComment(commentId)
    }

    override suspend fun getComment(commentId: String): Result<CommentData> = runCatching {
        api.getComment(commentId).comment.toModel()
    }

    override suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest,
    ): Result<CommentData> = runCatching {
        api.updateComment(commentId, request).comment.toModel()
    }

    override suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<Pair<FeedsReactionData, String>> = runCatching {
        val response = api.addCommentReaction(commentId, request)
        Pair(response.reaction.toModel(), response.comment.id)
    }

    override suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
    ): Result<Pair<FeedsReactionData, String>> = runCatching {
        val response = api.deleteCommentReaction(commentId = commentId, type = type)
        Pair(response.reaction.toModel(), response.comment.id)
    }

    override suspend fun queryCommentReactions(
        commentId: String,
        query: CommentReactionsQuery,
    ): Result<PaginationResult<FeedsReactionData>> = runCatching {
        val response = api.queryCommentReactions(commentId, query.toRequest())
        PaginationResult(
            models = response.reactions.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun getCommentReplies(
        query: CommentRepliesQuery,
    ): Result<PaginationResult<ThreadedCommentData>> = runCatching {
        val response = api.getCommentReplies(
            commentId = query.commentId,
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