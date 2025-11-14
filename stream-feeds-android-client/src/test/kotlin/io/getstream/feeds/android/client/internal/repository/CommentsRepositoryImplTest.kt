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
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.client.internal.test.TestData.commentResponse
import io.getstream.feeds.android.client.internal.test.TestData.deleteCommentResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.client.internal.test.TestData.userResponse
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.AddCommentReactionResponse
import io.getstream.feeds.android.network.models.AddCommentRequest
import io.getstream.feeds.android.network.models.AddCommentsBatchRequest
import io.getstream.feeds.android.network.models.Attachment
import io.getstream.feeds.android.network.models.DeleteCommentReactionResponse
import io.getstream.feeds.android.network.models.GetCommentRepliesResponse
import io.getstream.feeds.android.network.models.GetCommentResponse
import io.getstream.feeds.android.network.models.GetCommentsResponse
import io.getstream.feeds.android.network.models.QueryCommentReactionsResponse
import io.getstream.feeds.android.network.models.QueryCommentsResponse
import io.getstream.feeds.android.network.models.ThreadedCommentResponse
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.network.models.UpdateCommentResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentsRepositoryImplTest {
    private val feedsApi: FeedsApi = mockk()
    private val uploader: FeedUploader = mockk()

    private val repository = CommentsRepositoryImpl(api = feedsApi, uploader = uploader)

    @Test
    fun `on addComment, upload attachments and send api request`() = runTest {
        val attachmentUploads =
            listOf(
                FeedUploadPayload(File("1"), FileType.Image),
                FeedUploadPayload(File("2"), FileType.Image),
            )
        val request =
            ActivityAddCommentRequest(
                activityId = "activityId",
                comment = "Nice comment",
                attachments = listOf(Attachment(imageUrl = "alreadyUploaded", type = "image")),
                attachmentUploads = attachmentUploads,
            )
        val expectedAddCommentRequest =
            AddCommentRequest(
                objectId = "activityId",
                objectType = "activity",
                comment = "Nice comment",
                attachments =
                    listOf(
                        Attachment(imageUrl = "alreadyUploaded", type = "image"),
                        Attachment(assetUrl = "file/1", thumbUrl = "thumb/1"),
                        Attachment(assetUrl = "file/2", thumbUrl = "thumb/2"),
                    ),
            )
        mockUploader()

        repository.addComment(request)

        coVerify {
            attachmentUploads.forEach { localFile -> uploader.upload(localFile) }
            feedsApi.addComment(expectedAddCommentRequest)
        }
    }

    @Test
    fun `addComment on error return failure`() = runTest {
        val attachmentUploads = listOf(FeedUploadPayload(File("some file"), FileType.Image))
        val request =
            ActivityAddCommentRequest(
                request =
                    AddCommentRequest(
                        comment = "Nice comment",
                        objectId = "activityId",
                        objectType = "activity",
                    ),
                attachmentUploads = attachmentUploads,
            )
        coEvery { uploader.upload(any()) } returns Result.failure(Exception("Upload failed"))

        val result = repository.addComment(request)

        assertEquals("Upload failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { feedsApi.addActivity(any()) }
    }

    @Test
    fun `on addCommentsBatch, upload attachments and send api request`() = runTest {
        val payload1 = FeedUploadPayload(File("1"), FileType.Image)
        val payload2 = FeedUploadPayload(File("2"), FileType.Image)
        val requests =
            listOf(
                ActivityAddCommentRequest(
                    activityId = "activityId1",
                    comment = "Nice comment 1",
                    attachments = listOf(Attachment(imageUrl = "alreadyUploaded1", type = "image")),
                    attachmentUploads = listOf(payload1),
                ),
                ActivityAddCommentRequest(
                    activityId = "activityId1",
                    comment = "Nice comment 2",
                    attachments = listOf(Attachment(imageUrl = "alreadyUploaded2", type = "image")),
                    attachmentUploads = listOf(payload2),
                ),
            )
        val expectedRequests =
            listOf(
                AddCommentRequest(
                    objectId = "activityId1",
                    objectType = "activity",
                    comment = "Nice comment 1",
                    attachments =
                        listOf(
                            Attachment(imageUrl = "alreadyUploaded1", type = "image"),
                            Attachment(assetUrl = "file/1", thumbUrl = "thumb/1"),
                        ),
                ),
                AddCommentRequest(
                    objectId = "activityId1",
                    objectType = "activity",
                    comment = "Nice comment 2",
                    attachments =
                        listOf(
                            Attachment(imageUrl = "alreadyUploaded2", type = "image"),
                            Attachment(assetUrl = "file/2", thumbUrl = "thumb/2"),
                        ),
                ),
            )
        mockUploader()

        repository.addCommentsBatch(requests)

        coVerify {
            uploader.upload(payload1)
            uploader.upload(payload2)
            feedsApi.addCommentsBatch(AddCommentsBatchRequest(expectedRequests))
        }
    }

    private fun mockUploader() {
        coEvery { uploader.upload(any()) } answers
            {
                val name = firstArg<FeedUploadPayload>().file.name
                Result.success(UploadedFile(fileUrl = "file/$name", thumbnailUrl = "thumb/$name"))
            }
    }

    @Test
    fun `on queryComments, delegate to api and transform result`() = runTest {
        val query = CommentsQuery(limit = 10)
        val apiResult =
            QueryCommentsResponse(
                duration = "duration",
                comments = listOf(commentResponse(), commentResponse()),
                next = "next-cursor",
                prev = "prev-cursor",
            )

        testDelegation(
            apiFunction = { feedsApi.queryComments(query.toRequest()) },
            repositoryCall = { repository.queryComments(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = apiResult.comments.map { it.toModel() },
                    pagination = PaginationData(apiResult.next, apiResult.prev),
                ),
        )
    }

    @Test
    fun `on getComments, delegate to api and transform result`() = runTest {
        val query =
            ActivityCommentsQuery(
                objectId = "activityId",
                objectType = "activity",
                depth = 2,
                limit = 10,
            )
        val apiResult =
            GetCommentsResponse(
                duration = "duration",
                comments =
                    listOf(
                        ThreadedCommentResponse(
                            confidenceScore = 0.9f,
                            createdAt = java.util.Date(1000),
                            downvoteCount = 0,
                            id = "comment-1",
                            objectId = "activityId",
                            objectType = "activity",
                            reactionCount = 5,
                            replyCount = 0,
                            score = 10,
                            status = "active",
                            updatedAt = java.util.Date(1000),
                            upvoteCount = 5,
                            mentionedUsers = emptyList(),
                            ownReactions = emptyList(),
                            user = userResponse(),
                            text = "Test comment",
                            replies = emptyList(),
                        )
                    ),
                next = "next-cursor",
                prev = "prev-cursor",
            )

        testDelegation(
            apiFunction = {
                feedsApi.getComments(
                    objectId = query.objectId,
                    objectType = query.objectType,
                    depth = query.depth,
                    sort = query.sort?.toRequest()?.value,
                    limit = query.limit,
                    next = query.next,
                    prev = query.previous,
                )
            },
            repositoryCall = { repository.getComments(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = apiResult.comments.map { it.toModel() },
                    pagination = PaginationData(apiResult.next, apiResult.prev),
                ),
        )
    }

    @Test
    fun `on deleteComment, delegate to api`() {
        val apiResult = deleteCommentResponse()
        testDelegation(
            apiFunction = { feedsApi.deleteComment("commentId", true) },
            repositoryCall = { repository.deleteComment("commentId", true) },
            apiResult = apiResult,
            repositoryResult = apiResult.comment.toModel() to apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on getComment, delegate to api`() = runTest {
        val apiResult = GetCommentResponse(duration = "duration", comment = commentResponse())

        testDelegation(
            apiFunction = { feedsApi.getComment("commentId") },
            repositoryCall = { repository.getComment("commentId") },
            apiResult = apiResult,
            repositoryResult = apiResult.comment.toModel(),
        )
    }

    @Test
    fun `on updateComment, delegate to api`() = runTest {
        val request = UpdateCommentRequest(comment = "Updated comment")
        val apiResult = UpdateCommentResponse(duration = "duration", comment = commentResponse())

        testDelegation(
            apiFunction = { feedsApi.updateComment("commentId", request) },
            repositoryCall = { repository.updateComment("commentId", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.comment.toModel(),
        )
    }

    @Test
    fun `on addCommentReaction, delegate to api and transform result`() = runTest {
        val request = AddCommentReactionRequest(type = "like")
        val reactionResponse = feedsReactionResponse().copy(commentId = "commentId")
        val apiResult =
            AddCommentReactionResponse(
                duration = "duration",
                reaction = reactionResponse,
                comment = commentResponse(),
            )

        testDelegation(
            apiFunction = { feedsApi.addCommentReaction("commentId", request) },
            repositoryCall = { repository.addCommentReaction("commentId", request) },
            apiResult = apiResult,
            repositoryResult = Pair(apiResult.reaction.toModel(), apiResult.comment.toModel()),
        )
    }

    @Test
    fun `on deleteCommentReaction, delegate to api and transform result`() = runTest {
        val reactionResponse = feedsReactionResponse().copy(commentId = "commentId")
        val apiResult =
            DeleteCommentReactionResponse(
                duration = "duration",
                reaction = reactionResponse,
                comment = commentResponse(),
            )

        testDelegation(
            apiFunction = { feedsApi.deleteCommentReaction("commentId", "like") },
            repositoryCall = { repository.deleteCommentReaction("commentId", "like") },
            apiResult = apiResult,
            repositoryResult = Pair(apiResult.reaction.toModel(), apiResult.comment.toModel()),
        )
    }

    @Test
    fun `on queryCommentReactions, delegate to api and transform result`() = runTest {
        val query = CommentReactionsQuery(commentId = "commentId", limit = 10)
        val reactionResponse = feedsReactionResponse().copy(commentId = "commentId")
        val apiResult =
            QueryCommentReactionsResponse(
                duration = "duration",
                reactions = listOf(reactionResponse),
                next = "next-cursor",
                prev = "prev-cursor",
            )

        testDelegation(
            apiFunction = { feedsApi.queryCommentReactions("commentId", query.toRequest()) },
            repositoryCall = { repository.queryCommentReactions("commentId", query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = apiResult.reactions.map { it.toModel() },
                    pagination = PaginationData(apiResult.next, apiResult.prev),
                ),
        )
    }

    @Test
    fun `on getCommentReplies, delegate to api and transform result`() = runTest {
        val query = CommentRepliesQuery(commentId = "commentId", depth = 2, limit = 10)
        val apiResult =
            GetCommentRepliesResponse(
                duration = "duration",
                comments =
                    listOf(
                        ThreadedCommentResponse(
                            confidenceScore = 0.9f,
                            createdAt = java.util.Date(1000),
                            downvoteCount = 0,
                            id = "reply-1",
                            objectId = "commentId",
                            objectType = "comment",
                            reactionCount = 0,
                            replyCount = 0,
                            score = 5,
                            status = "active",
                            updatedAt = java.util.Date(1000),
                            upvoteCount = 5,
                            mentionedUsers = emptyList(),
                            ownReactions = emptyList(),
                            user = userResponse(),
                            text = "Test reply",
                            replies = emptyList(),
                        )
                    ),
                next = "next-cursor",
                prev = "prev-cursor",
            )

        testDelegation(
            apiFunction = {
                feedsApi.getCommentReplies(
                    id = query.commentId,
                    depth = query.depth,
                    limit = query.limit,
                    next = query.next,
                    prev = query.previous,
                    repliesLimit = query.repliesLimit,
                    sort = query.sort?.toRequest()?.value,
                )
            },
            repositoryCall = { repository.getCommentReplies(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = apiResult.comments.map { it.toModel() },
                    pagination = PaginationData(apiResult.next, apiResult.prev),
                ),
        )
    }
}
