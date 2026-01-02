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

import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.CollectionRequest
import io.getstream.feeds.android.network.models.CollectionResponse
import io.getstream.feeds.android.network.models.CreateCollectionsRequest
import io.getstream.feeds.android.network.models.CreateCollectionsResponse
import io.getstream.feeds.android.network.models.DeleteCollectionsResponse
import io.getstream.feeds.android.network.models.ReadCollectionsResponse
import io.getstream.feeds.android.network.models.UpdateCollectionRequest
import io.getstream.feeds.android.network.models.UpdateCollectionsRequest
import io.getstream.feeds.android.network.models.UpdateCollectionsResponse
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class CollectionsRepositoryImplTest {

    private val feedsApi: FeedsApi = mockk()
    private val repository = CollectionsRepositoryImpl(api = feedsApi)

    @Test
    fun `readCollections should delegate to api`() = runTest {
        val refs = listOf("movies:lotr", "games:sm")
        val apiResult =
            ReadCollectionsResponse(duration = "100ms", collections = listOf(collectionResponse()))

        testDelegation(
            apiFunction = { feedsApi.readCollections(refs) },
            repositoryCall = { repository.readCollections(refs) },
            apiResult = apiResult,
        )
    }

    @Test
    fun `createCollections should delegate to api`() = runTest {
        val request =
            CreateCollectionsRequest(
                collections =
                    listOf(
                        CollectionRequest(name = "movies", id = "lotr"),
                        CollectionRequest(name = "categories", id = "456"),
                    )
            )
        val apiResult =
            CreateCollectionsResponse(
                duration = "200ms",
                collections =
                    listOf(
                        collectionResponse(id = "lotr", name = "movies"),
                        collectionResponse(id = "456", name = "categories"),
                    ),
            )

        testDelegation(
            apiFunction = { feedsApi.createCollections(request) },
            repositoryCall = { repository.createCollections(request) },
            apiResult = apiResult,
        )
    }

    @Test
    fun `deleteCollections should delegate to api`() = runTest {
        val refs = listOf("movies:lotr", "games:sm")
        val apiResult = DeleteCollectionsResponse(duration = "100ms")

        testDelegation(
            apiFunction = { feedsApi.deleteCollections(refs) },
            repositoryCall = { repository.deleteCollections(refs) },
            apiResult = apiResult,
        )
    }

    @Test
    fun `updateCollections should delegate to api`() = runTest {
        val request =
            UpdateCollectionsRequest(
                collections =
                    listOf(
                        UpdateCollectionRequest(
                            id = "lotr",
                            name = "movies",
                            custom = mapOf("updated" to true),
                        )
                    )
            )
        val apiResult =
            UpdateCollectionsResponse(
                duration = "200ms",
                collections = listOf(collectionResponse(id = "lotr", name = "movies")),
            )

        testDelegation(
            apiFunction = { feedsApi.updateCollections(request) },
            repositoryCall = { repository.updateCollections(request) },
            apiResult = apiResult,
        )
    }

    private fun collectionResponse(id: String = "lotr", name: String = "movies") =
        CollectionResponse(
            id = id,
            name = name,
            createdAt = null,
            updatedAt = null,
            userId = "user-1",
            custom = emptyMap(),
        )
}
