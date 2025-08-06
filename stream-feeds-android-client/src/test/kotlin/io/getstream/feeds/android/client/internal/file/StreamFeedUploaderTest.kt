package io.getstream.feeds.android.client.internal.file

import io.getstream.feeds.android.client.api.file.FeedUploadContext
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.core.generated.models.FileUploadResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okio.blackholeSink
import okio.buffer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

internal class StreamFeedUploaderTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()


    private val cdnApi: CdnApi = mockk()

    private val uploader = StreamFeedUploader(
        cdnApi = cdnApi,
        getMediaType = { "type/subtype".toMediaType() }
    )

    @Test
    fun `upload when it's a file, then send it through the correct api`() = runTest {
        val uploadResponse = FileUploadResponse(duration = "", file = "file", thumbUrl = "thumbnail")
        coEvery { cdnApi.sendFile(any()) } returns uploadResponse
        val expectedResult = Result.success(UploadedFile(fileUrl = "file", thumbnailUrl = "thumbnail"))
        val payload = FeedUploadPayload(
            file = File("test.txt"),
            type = FileType.Other("txt"),
            context = FeedUploadContext(FeedId("user", "timeline"))
        )

        val result = uploader.upload(payload)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `upload when it's an image, then send it through the correct api`() = runTest {
        val uploadResponse = FileUploadResponse(duration = "", file = "img", thumbUrl = "thumbnail")
        coEvery { cdnApi.sendImage(any()) } returns uploadResponse
        val expectedResult = Result.success(UploadedFile(fileUrl = "img", thumbnailUrl = "thumbnail"))
        val payload = FeedUploadPayload(
            file = File("test.png"),
            type = FileType.Image("png"),
            context = FeedUploadContext(FeedId("user", "timeline"))
        )

        val result = uploader.upload(payload)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `upload when a progress listener is provided, then notify progress`() = runTest {
        val file = temporaryFolder.newFile("test.png").apply { writeText("dummy file content") }
        val payload = FeedUploadPayload(
            file = file,
            type = FileType.Image("png"),
            context = FeedUploadContext(FeedId("user", "timeline"))
        )
        val progressListener: (Double) -> Unit = mockk(relaxed = true)
        val uploadResponse = FileUploadResponse(duration = "", file = "img", thumbUrl = "thumbnail")
        coEvery { cdnApi.sendImage(any()) } answers {
            firstArg<MultipartBody.Part>().body.writeTo(blackholeSink().buffer())
            uploadResponse
        }

        uploader.upload(payload, progressListener)

        verify { progressListener(any()) }
    }
}
