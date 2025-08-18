package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.Attachment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

internal class ActivitiesRepositoryImplTest {
    private val apiService: ApiService = mockk()
    private val uploader: FeedUploader = mockk()

    private val repository = ActivitiesRepositoryImpl(
        api = apiService,
        uploader = uploader
    )

    @Test
    fun `on addActivity, upload attachments and send api request`() = runTest {
        val attachmentUploads = listOf(
            FeedUploadPayload(File("1"), FileType.Image("jpg")),
            FeedUploadPayload(File("2"), FileType.Image("png")),
        )
        val request = FeedAddActivityRequest(
            request = AddActivityRequest(
                type = "post",
                text = "Nice post",
                attachments = listOf(Attachment(imageUrl = "alreadyUploaded", type = "image")),
            ),
            attachmentUploads = attachmentUploads
        )
        val expectedAddActivityRequest = AddActivityRequest(
            type = "post",
            text = "Nice post",
            attachments = listOf(
                Attachment(imageUrl = "alreadyUploaded", type = "image"),
                Attachment(assetUrl = "file/1", thumbUrl = "thumb/1"),
                Attachment(assetUrl = "file/2", thumbUrl = "thumb/2"),
            )
        )

        coEvery { uploader.upload(any()) } answers {
            val name = firstArg<FeedUploadPayload>().file.name
            Result.success(UploadedFile(fileUrl = "file/$name", thumbnailUrl = "thumb/$name"))
        }

        repository.addActivity(request)

        attachmentUploads.forEach { localFile ->
            coVerify { uploader.upload(localFile) }
        }
        coVerify { apiService.addActivity(expectedAddActivityRequest) }
    }

    @Test
    fun `addActivity on error return failure`() = runTest {
        val attachmentUploads = listOf(FeedUploadPayload(File("some file"), FileType.Image("jpg")))
        val request = FeedAddActivityRequest(
            request = AddActivityRequest(type = "post", text = "Nice post"),
            attachmentUploads = attachmentUploads
        )

        coEvery { uploader.upload(any()) } returns Result.failure(Exception("Upload failed"))

        val result = repository.addActivity(request)

        assertEquals("Upload failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { apiService.addActivity(any()) }
    }
}
