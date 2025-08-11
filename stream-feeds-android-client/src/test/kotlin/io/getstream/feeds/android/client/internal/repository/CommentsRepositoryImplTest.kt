package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
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
            FeedUploadPayload(File("1"), FileType.Image("jpg")),
            FeedUploadPayload(File("2"), FileType.Image("png")),
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
                Attachment(assetUrl = "file/1", thumbUrl = "thumb/1"),
                Attachment(assetUrl = "file/2", thumbUrl = "thumb/2"),
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
            FeedUploadPayload(File("some file"), FileType.Image("jpg"))
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
        val payload1 = FeedUploadPayload(File("1"), FileType.Image("jpg"))
        val payload2 = FeedUploadPayload(File("2"), FileType.Image("png"))
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
                    Attachment(assetUrl = "file/1", thumbUrl = "thumb/1"),
                )
            ),
            AddCommentRequest(
                objectId = "activityId1",
                objectType = "activity",
                comment = "Nice comment 2",
                attachments = listOf(
                    Attachment(imageUrl = "alreadyUploaded2", type = "image"),
                    Attachment(assetUrl = "file/2", thumbUrl = "thumb/2"),
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
            val name = firstArg<FeedUploadPayload>().file.name
            Result.success(UploadedFile(fileUrl = "file/$name", thumbnailUrl = "thumb/$name"))
        }
    }
}
