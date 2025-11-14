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

package io.getstream.feeds.android.client.internal.file

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.network.models.FileUploadResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okio.blackholeSink
import okio.buffer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

internal class StreamFeedUploaderTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    private val cdnApi: CdnApi = mockk()

    private val uploader =
        StreamFeedUploader(cdnApi = cdnApi, getMediaType = { "type/subtype".toMediaType() })

    @Test
    fun `upload when it's a file, then send it through the correct api`() = runTest {
        val uploadResponse =
            FileUploadResponse(duration = "", file = "file", thumbUrl = "thumbnail")
        coEvery { cdnApi.sendFile(any()) } returns uploadResponse
        val expectedResult =
            Result.success(UploadedFile(fileUrl = "file", thumbnailUrl = "thumbnail"))
        val payload = FeedUploadPayload(file = File("test.txt"), type = FileType.Other)

        val result = uploader.upload(payload)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `upload when it's an image, then send it through the correct api`() = runTest {
        val uploadResponse = FileUploadResponse(duration = "", file = "img", thumbUrl = "thumbnail")
        coEvery { cdnApi.sendImage(any()) } returns uploadResponse
        val expectedResult =
            Result.success(UploadedFile(fileUrl = "img", thumbnailUrl = "thumbnail"))
        val payload = FeedUploadPayload(file = File("test.png"), type = FileType.Image)

        val result = uploader.upload(payload)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `upload when a progress listener is provided, then notify progress`() = runTest {
        val file = temporaryFolder.newFile("test.png").apply { writeText("dummy file content") }
        val payload = FeedUploadPayload(file = file, type = FileType.Image)
        val progressListener: (Double) -> Unit = mockk(relaxed = true)
        val uploadResponse = FileUploadResponse(duration = "", file = "img", thumbUrl = "thumbnail")
        coEvery { cdnApi.sendImage(any()) } answers
            {
                firstArg<MultipartBody.Part>().body.writeTo(blackholeSink().buffer())
                uploadResponse
            }

        uploader.upload(payload, progressListener)

        verify { progressListener(any()) }
    }
}
