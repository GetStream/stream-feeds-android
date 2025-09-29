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
package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.internal.file.uploadAll
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.AddCommentRequest
import io.getstream.feeds.android.network.models.AddCommentsBatchRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Default implementation of the [CommentsRepository] interface.
 *
 * Uses the provided [FeedsApi] to perform network requests related to comments.
 *
 * @property api The API service used to perform network requests.
 */
internal class CommentsRepositoryImpl(
    private val api: FeedsApi,
    private val uploader: FeedUploader,
) : CommentsRepository {

    override suspend fun queryComments(
        query: CommentsQuery
    ): Result<PaginationResult<CommentData>> = runSafely {
        val response = api.queryComments(query.toRequest())
        PaginationResult(
            models = response.comments.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun getComments(
        query: ActivityCommentsQuery
    ): Result<PaginationResult<ThreadedCommentData>> = runSafely {
        val response =
            api.getComments(
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

    override suspend fun addComment(
        request: ActivityAddCommentRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?,
    ): Result<CommentData> = runSafely {
        val newRequest = uploadAttachmentsAndUpdateRequest(request, attachmentUploadProgress)
        api.addComment(newRequest).comment.toModel()
    }

    override suspend fun addCommentsBatch(
        requests: List<ActivityAddCommentRequest>,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?,
    ): Result<List<CommentData>> = runSafely {
        val newRequests =
            coroutineScope {
                    requests.map { request ->
                        async {
                            uploadAttachmentsAndUpdateRequest(request, attachmentUploadProgress)
                        }
                    }
                }
                .awaitAll()

        val batchRequest = AddCommentsBatchRequest(newRequests)

        api.addCommentsBatch(batchRequest).comments.map { it.toModel() }
    }

    private suspend fun uploadAttachmentsAndUpdateRequest(
        request: ActivityAddCommentRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?,
    ): AddCommentRequest {
        val uploadedAttachments =
            uploader.uploadAll(
                files = request.attachmentUploads,
                attachmentUploadProgress = attachmentUploadProgress,
            )
        return request.request.copy(
            attachments = request.request.attachments.orEmpty() + uploadedAttachments
        )
    }

    override suspend fun deleteComment(
        commentId: String,
        hardDelete: Boolean?,
    ): Result<Pair<CommentData, ActivityData>> = runSafely {
        val response = api.deleteComment(commentId, hardDelete)
        response.comment.toModel() to response.activity.toModel()
    }

    override suspend fun getComment(commentId: String): Result<CommentData> = runSafely {
        api.getComment(commentId).comment.toModel()
    }

    override suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest,
    ): Result<CommentData> = runSafely { api.updateComment(commentId, request).comment.toModel() }

    override suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<Pair<FeedsReactionData, CommentData>> = runSafely {
        val response = api.addCommentReaction(commentId, request)
        Pair(response.reaction.toModel(), response.comment.toModel())
    }

    override suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
    ): Result<Pair<FeedsReactionData, CommentData>> = runSafely {
        val response = api.deleteCommentReaction(id = commentId, type = type)
        Pair(response.reaction.toModel(), response.comment.toModel())
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
        query: CommentRepliesQuery
    ): Result<PaginationResult<ThreadedCommentData>> = runSafely {
        val response =
            api.getCommentReplies(
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
