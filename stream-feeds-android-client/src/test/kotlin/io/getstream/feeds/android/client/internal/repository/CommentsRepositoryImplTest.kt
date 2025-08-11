package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.file.DefaultFeedUploadContext
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.AddCommentRequest
import io.getstream.feeds.android.core.generated.models.AddCommentsBatchRequest
import io.getstream.feeds.android.core.generated.models.Attachment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

internal class CommentsRepositoryImplTest {
    private val apiService: ApiService = mockk()
    private val uploader: FeedUploader = mockk()

    private val repository = CommentsRepositoryImpl(
        api = apiService,
        uploader = uploader
    )

    @Test
    fun `on addComment, upload attachments and send api request`() = runTest {
        val attachmentUploads = listOf(
            FeedUploadPayload(File("1"), FileType.Image("jpg"), DefaultFeedUploadContext(FeedId("id:1"))),
            FeedUploadPayload(File("2"), FileType.Image("png"), DefaultFeedUploadContext(FeedId("id:2"))),
        )
        val request = ActivityAddCommentRequest(
            activityId = "activityId",
            comment = "Nice comment",
            attachments = listOf(Attachment(imageUrl = "alreadyUploaded", type = "image")),
            attachmentUploads = attachmentUploads
        )
        val expectedAddCommentRequest = AddCommentRequest(
            objectId = "activityId",
            objectType = "activity",
            comment = "Nice comment",
            attachments = listOf(
                Attachment(imageUrl = "alreadyUploaded", type = "image"),
                Attachment(assetUrl = "file/id:1", thumbUrl = "thumb/id:1"),
                Attachment(assetUrl = "file/id:2", thumbUrl = "thumb/id:2"),
            )
        )
        mockUploader()

        repository.addComment(request)

        coVerify {
            attachmentUploads.forEach { localFile -> uploader.upload(localFile) }
            apiService.addComment(expectedAddCommentRequest)
        }
    }

    @Test
    fun `addComment on error return failure`() = runTest {
        val attachmentUploads = listOf(
            FeedUploadPayload(File("some file"), FileType.Image("jpg"), DefaultFeedUploadContext(FeedId("id1")))
        )
        val request = ActivityAddCommentRequest(
            request = AddCommentRequest(comment = "Nice comment", objectId = "activityId", objectType = "activity"),
            attachmentUploads = attachmentUploads
        )
        coEvery { uploader.upload(any()) } returns Result.failure(Exception("Upload failed"))

        val result = repository.addComment(request)

        assertEquals("Upload failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { apiService.addActivity(any()) }
    }


    @Test
    fun `on addCommentsBatch, upload attachments and send api request`() = runTest {
        val payload1 = FeedUploadPayload(File("1"), FileType.Image("jpg"), DefaultFeedUploadContext(FeedId("id:1")))
        val payload2 = FeedUploadPayload(File("2"), FileType.Image("png"), DefaultFeedUploadContext(FeedId("id:2")))
        val requests = listOf(
            ActivityAddCommentRequest(
                activityId = "activityId1",
                comment = "Nice comment 1",
                attachments = listOf(Attachment(imageUrl = "alreadyUploaded1", type = "image")),
                attachmentUploads = listOf(payload1)
            ),
            ActivityAddCommentRequest(
                activityId = "activityId1",
                comment = "Nice comment 2",
                attachments = listOf(Attachment(imageUrl = "alreadyUploaded2", type = "image")),
                attachmentUploads = listOf(payload2)
            )
        )
        val expectedRequests = listOf(
            AddCommentRequest(
                objectId = "activityId1",
                objectType = "activity",
                comment = "Nice comment 1",
                attachments = listOf(
                    Attachment(imageUrl = "alreadyUploaded1", type = "image"),
                    Attachment(assetUrl = "file/id:1", thumbUrl = "thumb/id:1"),
                )
            ),
            AddCommentRequest(
                objectId = "activityId1",
                objectType = "activity",
                comment = "Nice comment 2",
                attachments = listOf(
                    Attachment(imageUrl = "alreadyUploaded2", type = "image"),
                    Attachment(assetUrl = "file/id:2", thumbUrl = "thumb/id:2"),
                )
            )
        )
        mockUploader()

        repository.addCommentsBatch(requests)

        coVerify {
            uploader.upload(payload1)
            uploader.upload(payload2)
            apiService.addCommentsBatch(AddCommentsBatchRequest(expectedRequests))
        }
    }

    private fun mockUploader() {
        coEvery { uploader.upload(any()) } answers {
            val id = firstArg<FeedUploadPayload>().context.feedId.rawValue
            Result.success(UploadedFile(fileUrl = "file/$id", thumbnailUrl = "thumb/$id"))
        }
    }
}
