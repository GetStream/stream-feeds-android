/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

import io.getstream.feeds.android.client.api.state.query.UsersQuery
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.client.internal.test.TestData.appData
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AppResponseFields
import io.getstream.feeds.android.network.models.FileUploadConfig
import io.getstream.feeds.android.network.models.FullUserResponse
import io.getstream.feeds.android.network.models.GetApplicationResponse
import io.getstream.feeds.android.network.models.GetOGResponse
import io.getstream.feeds.android.network.models.QueryUsersResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommonRepositoryImplTest {
    private val api: FeedsApi = mockk(relaxed = true)
    private val repository: CommonRepositoryImpl = CommonRepositoryImpl(api)

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
                id = 0,
                placement = "placement",
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

    @Test
    fun `on getOG, delegate to api`() {
        val url = "https://example.com/article"
        val apiResult =
            GetOGResponse(duration = "50ms", title = "Example Article", ogScrapeUrl = url)

        testDelegation(
            apiFunction = { api.getOG(url) },
            repositoryCall = { repository.getOG(url) },
            apiResult = apiResult,
        )
    }

    @Test
    fun `on queryUsers, delegate to api and map response`() {
        val query = UsersQuery(limit = 10)
        val userResponse =
            FullUserResponse(
                id = "user-1",
                banned = false,
                createdAt = Date(1000),
                invisible = false,
                language = "en",
                online = false,
                role = "admin",
                shadowBanned = false,
                totalUnreadCount = 0,
                unreadChannels = 0,
                unreadCount = 0,
                unreadThreads = 0,
                updatedAt = Date(1000),
                name = "Admin User",
            )
        val apiResult = QueryUsersResponse(duration = "50ms", users = listOf(userResponse))

        testDelegation(
            apiFunction = { api.queryUsers(query.toRequest()) },
            repositoryCall = { repository.queryUsers(query) },
            apiResult = apiResult,
            repositoryResult = listOf(userResponse.toModel()),
        )
    }
}
