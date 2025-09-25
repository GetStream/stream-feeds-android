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

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest

/**
 * A repository for managing comments.
 *
 * Action methods make API requests and transform API responses to domain models.
 */
internal interface CommentsRepository {

    /**
     * Queries comments based on the provided request.
     *
     * @param query The request containing the filter, limit, pagination, and sorting options.
     * @return A [Result] containing a [PaginationResult] of [CommentData] if successful, or an
     *   error if the operation fails.
     */
    suspend fun queryComments(query: CommentsQuery): Result<PaginationResult<CommentData>>

    /**
     * Retrieves comments for a specific activity using the provided query.
     *
     * @param query The query containing the activity ID and other parameters.
     * @return A [Result] containing a [PaginationResult] of [ThreadedCommentData] if successful, or
     *   an error if the operation fails.
     */
    suspend fun getComments(
        query: ActivityCommentsQuery
    ): Result<PaginationResult<ThreadedCommentData>>

    /**
     * Adds a new comment based on the provided request.
     *
     * @param request The request containing the comment details such as activity ID, text, and any
     *   additional metadata.
     * @return A [Result] containing the newly created [CommentData] if successful, or an error if
     *   the operation fails.
     */
    suspend fun addComment(
        request: ActivityAddCommentRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)? = null,
    ): Result<CommentData>

    /**
     * Adds multiple comments in a single batch operation.
     *
     * @param requests A list of [ActivityAddCommentRequest] objects, each representing a comment to
     *   be added.
     * @return A [Result] containing a list of [CommentData] for each successfully added comment, or
     *   an error if the operation fails.
     */
    suspend fun addCommentsBatch(
        requests: List<ActivityAddCommentRequest>,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)? = null,
    ): Result<List<CommentData>>

    /**
     * Deletes a comment by its identifier.
     *
     * @param commentId The unique identifier of the comment to be deleted.
     * @param hardDelete If true, the comment will be permanently deleted. Otherwise, it will be
     *   soft-deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    suspend fun deleteComment(
        commentId: String,
        hardDelete: Boolean?,
    ): Result<Pair<CommentData, ActivityData>>

    /**
     * Retrieves a specific comment by its identifier.
     *
     * @param commentId The unique identifier of the comment to retrieve.
     * @return A [Result] containing the [CommentData] if successful, or an error if the operation
     *   fails.
     */
    suspend fun getComment(commentId: String): Result<CommentData>

    /**
     * Updates an existing comment.
     *
     * @param commentId The unique identifier of the comment to be updated.
     * @param request The request containing the updated details of the comment.
     * @return A [Result] containing the updated [CommentData] if successful, or an error if the
     *   operation fails.
     */
    suspend fun updateComment(commentId: String, request: UpdateCommentRequest): Result<CommentData>

    /**
     * Adds a reaction to a comment.
     *
     * @param commentId The unique identifier of the comment to which the reaction is added.
     * @param request The request containing the details of the reaction to be added.
     * @return A [Result] containing the [FeedsReactionData] if successful, or an error if the
     *   operation fails.
     */
    suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<Pair<FeedsReactionData, CommentData>>

    /**
     * Deletes a reaction from a comment.
     *
     * @param commentId The unique identifier of the comment from which the reaction is deleted.
     * @param type The type of the reaction to be deleted (e.g., "like", "love", etc.).
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
    ): Result<Pair<FeedsReactionData, CommentData>>

    /**
     * Queries reactions for a specific comment.
     *
     * @param commentId The unique identifier of the comment for which reactions are queried.
     * @param query The request containing pagination and filtering options for the reactions.
     * @return A [Result] containing a [PaginationResult] of [FeedsReactionData] if successful, or
     *   an error if the operation fails.
     */
    suspend fun queryCommentReactions(
        commentId: String,
        query: CommentReactionsQuery,
    ): Result<PaginationResult<FeedsReactionData>>

    /**
     * Retrieves replies to a specific comment.
     *
     * @param query The query containing the comment ID and other parameters for pagination and
     *   sorting.
     * @return A [Result] containing a [PaginationResult] of [ThreadedCommentData] if successful, or
     *   an error if the operation fails.
     */
    suspend fun getCommentReplies(
        query: CommentRepliesQuery
    ): Result<PaginationResult<ThreadedCommentData>>
}
