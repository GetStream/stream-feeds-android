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

import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.Response
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class FilesRepositoryImplTest {

    private val apiService: ApiService = mockk()

    private val repository = FilesRepositoryImpl(api = apiService)

    @Test
    fun `deleteFile on success returns success result`() = runTest {
        val fileUrl = "https://example.com/file.jpg"

        coEvery { apiService.deleteFile(fileUrl) } returns Response("")

        val result = repository.deleteFile(fileUrl)

        assertTrue(result.isSuccess)
        coVerify { apiService.deleteFile(fileUrl) }
    }

    @Test
    fun `deleteFile on error returns failure result`() = runTest {
        val fileUrl = "https://example.com/file.jpg"
        val exception = Exception("File deletion failed")

        coEvery { apiService.deleteFile(fileUrl) } throws exception

        val result = repository.deleteFile(fileUrl)

        assertTrue(result.isFailure)
        assertEquals("File deletion failed", result.exceptionOrNull()?.message)
        coVerify { apiService.deleteFile(fileUrl) }
    }

    @Test
    fun `deleteImage on success returns success result`() = runTest {
        val imageUrl = "https://example.com/image.png"

        coEvery { apiService.deleteImage(imageUrl) } returns Response("")

        val result = repository.deleteImage(imageUrl)

        assertTrue(result.isSuccess)
        coVerify { apiService.deleteImage(imageUrl) }
    }

    @Test
    fun `deleteImage on error returns failure result`() = runTest {
        val imageUrl = "https://example.com/image.png"
        val exception = Exception("Image deletion failed")

        coEvery { apiService.deleteImage(imageUrl) } throws exception

        val result = repository.deleteImage(imageUrl)

        assertTrue(result.isFailure)
        assertEquals("Image deletion failed", result.exceptionOrNull()?.message)
        coVerify { apiService.deleteImage(imageUrl) }
    }
}
