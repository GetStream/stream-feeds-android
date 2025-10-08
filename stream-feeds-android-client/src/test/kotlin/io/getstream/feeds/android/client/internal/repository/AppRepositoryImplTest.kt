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

import io.getstream.feeds.android.client.internal.test.TestData.appData
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AppResponseFields
import io.getstream.feeds.android.network.models.FileUploadConfig
import io.getstream.feeds.android.network.models.GetApplicationResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AppRepositoryImplTest {
    private val api: FeedsApi = mockk(relaxed = true)
    private val repository: AppRepositoryImpl = AppRepositoryImpl(api)

    @Test
    fun `getApp when called multiple times, fetch from api once and cache result`() = runTest {
        val fileUploadConfig = FileUploadConfig(sizeLimit = 0)
        val imageUploadConfig = FileUploadConfig(sizeLimit = 0)
        val appResponseFields =
            AppResponseFields(
                asyncUrlEnrichEnabled = false,
                autoTranslationEnabled = false,
                name = "Test App",
                fileUploadConfig = fileUploadConfig,
                imageUploadConfig = imageUploadConfig,
                region = "region",
                shard = "shard",
            )
        val apiResponse = GetApplicationResponse(duration = "100ms", app = appResponseFields)

        coEvery { api.getApp() } returns apiResponse

        val expectedAppData = appData(name = "Test App")

        // First call - should hit API
        val firstResult = repository.getApp()
        assertEquals(expectedAppData, firstResult.getOrNull())

        // Second call - should return cached result
        val secondResult = repository.getApp()
        assertEquals(expectedAppData, secondResult.getOrNull())

        // Third call - should return cached result
        val thirdResult = repository.getApp()
        assertEquals(expectedAppData, thirdResult.getOrNull())

        // Verify API was called exactly once (not three times)
        coVerify(exactly = 1) { api.getApp() }
    }
}
