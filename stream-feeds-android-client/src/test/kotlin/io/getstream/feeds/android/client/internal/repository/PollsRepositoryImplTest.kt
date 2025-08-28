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

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.client.internal.test.TestData.pollResponseData
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.PollResponse
import io.getstream.feeds.android.network.models.UpdatePollRequest
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class PollsRepositoryImplTest {
    private val feedsApi: FeedsApi = mockk()
    private val repository = PollsRepositoryImpl(api = feedsApi)

    @Test
    fun `on createPoll, delegate to api`() = runTest {
        val request = CreatePollRequest(name = "Test Poll")
        val apiResult = PollResponse("duration", pollResponseData(name = "Test Poll"))

        testDelegation(
            apiFunction = { feedsApi.createPoll(request) },
            repositoryCall = { repository.createPoll(request) },
            apiResult = apiResult,
            repositoryResult = apiResult.poll.toModel()
        )
    }

    @Test
    fun `on getPoll, delegate to api`() = runTest {
        val apiResult = PollResponse("duration", pollResponseData())

        testDelegation(
            apiFunction = { feedsApi.getPoll("pollId", "userId") },
            repositoryCall = { repository.getPoll("pollId", "userId") },
            apiResult = apiResult,
            repositoryResult = apiResult.poll.toModel()
        )
    }

    @Test
    fun `on updatePoll, delegate to api`() = runTest {
        val request = UpdatePollRequest(id = "pollId", name = "Updated Poll")
        val apiResult = PollResponse("duration", pollResponseData(name = "Updated Poll"))

        testDelegation(
            apiFunction = { feedsApi.updatePoll(request) },
            repositoryCall = { repository.updatePoll(request) },
            apiResult = apiResult,
            repositoryResult = apiResult.poll.toModel()
        )
    }

    @Test
    fun `on deletePoll, delegate to api`() {
        testDelegation(
            apiFunction = { feedsApi.deletePoll("pollId", "userId") },
            repositoryCall = { repository.deletePoll("pollId", "userId") },
            apiResult = Unit
        )
    }
}
