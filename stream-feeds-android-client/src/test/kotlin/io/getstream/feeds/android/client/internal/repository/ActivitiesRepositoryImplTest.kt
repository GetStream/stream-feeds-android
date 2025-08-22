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
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.Attachment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.File
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ActivitiesRepositoryImplTest {
    private val feedsApi: FeedsApi = mockk()
    private val uploader: FeedUploader = mockk()

    private val repository = ActivitiesRepositoryImpl(api = feedsApi, uploader = uploader)

    @Test
    fun `on addActivity, upload attachments and send api request`() = runTest {
        val attachmentUploads =
            listOf(
                FeedUploadPayload(File("1"), FileType.Image("jpg")),
                FeedUploadPayload(File("2"), FileType.Image("png")),
            )
        val request =
            FeedAddActivityRequest(
                request =
                    AddActivityRequest(
                        type = "post",
                        text = "Nice post",
                        attachments =
                            listOf(Attachment(imageUrl = "alreadyUploaded", type = "image")),
                    ),
                attachmentUploads = attachmentUploads,
            )
        val expectedAddActivityRequest =
            AddActivityRequest(
                type = "post",
                text = "Nice post",
                attachments =
                    listOf(
                        Attachment(imageUrl = "alreadyUploaded", type = "image"),
                        Attachment(assetUrl = "file/1", thumbUrl = "thumb/1"),
                        Attachment(assetUrl = "file/2", thumbUrl = "thumb/2"),
                    ),
            )

        coEvery { uploader.upload(any()) } answers
            {
                val name = firstArg<FeedUploadPayload>().file.name
                Result.success(UploadedFile(fileUrl = "file/$name", thumbnailUrl = "thumb/$name"))
            }

        repository.addActivity(request)

        attachmentUploads.forEach { localFile -> coVerify { uploader.upload(localFile) } }
        coVerify { feedsApi.addActivity(expectedAddActivityRequest) }
    }

    @Test
    fun `addActivity on error return failure`() = runTest {
        val attachmentUploads = listOf(FeedUploadPayload(File("some file"), FileType.Image("jpg")))
        val request =
            FeedAddActivityRequest(
                request = AddActivityRequest(type = "post", text = "Nice post"),
                attachmentUploads = attachmentUploads,
            )

        coEvery { uploader.upload(any()) } returns Result.failure(Exception("Upload failed"))

        val result = repository.addActivity(request)

        assertEquals("Upload failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { feedsApi.addActivity(any()) }
    }
}
