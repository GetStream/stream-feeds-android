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

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.client.internal.test.TestData.pollResponseData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteResponseData
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.CastPollVoteRequest
import io.getstream.feeds.android.network.models.CreatePollOptionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.PollOptionResponse
import io.getstream.feeds.android.network.models.PollResponse
import io.getstream.feeds.android.network.models.PollVoteResponse
import io.getstream.feeds.android.network.models.PollVotesResponse
import io.getstream.feeds.android.network.models.QueryPollsResponse
import io.getstream.feeds.android.network.models.UpdatePollOptionRequest
import io.getstream.feeds.android.network.models.UpdatePollPartialRequest
import io.getstream.feeds.android.network.models.UpdatePollRequest
import io.getstream.feeds.android.network.models.VoteData
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
            repositoryResult = apiResult.poll.toModel(),
        )
    }

    @Test
    fun `on getPoll, delegate to api`() = runTest {
        val apiResult = PollResponse("duration", pollResponseData())

        testDelegation(
            apiFunction = { feedsApi.getPoll("pollId", "userId") },
            repositoryCall = { repository.getPoll("pollId", "userId") },
            apiResult = apiResult,
            repositoryResult = apiResult.poll.toModel(),
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
            repositoryResult = apiResult.poll.toModel(),
        )
    }

    @Test
    fun `on deletePoll, delegate to api`() {
        testDelegation(
            apiFunction = { feedsApi.deletePoll("pollId", "userId") },
            repositoryCall = { repository.deletePoll("pollId", "userId") },
            apiResult = Unit,
        )
    }

    @Test
    fun `on closePoll, delegate to api`() = runTest {
        val request = UpdatePollPartialRequest(set = mapOf("is_closed" to true))
        val apiResult = PollResponse("duration", pollResponseData(name = "Closed Poll"))

        testDelegation(
            apiFunction = { feedsApi.updatePollPartial("pollId", request) },
            repositoryCall = { repository.closePoll("pollId") },
            apiResult = apiResult,
            repositoryResult = apiResult.poll.toModel(),
        )
    }

    @Test
    fun `on updatePollPartial, delegate to api`() = runTest {
        val request = UpdatePollPartialRequest(set = mapOf("name" to "Updated Poll"))
        val apiResult = PollResponse("duration", pollResponseData(name = "Updated Poll"))

        testDelegation(
            apiFunction = { feedsApi.updatePollPartial("pollId", request) },
            repositoryCall = { repository.updatePollPartial("pollId", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.poll.toModel(),
        )
    }

    @Test
    fun `on createPollOption, delegate to api`() = runTest {
        val request = CreatePollOptionRequest(text = "New Option")
        val apiResult =
            PollOptionResponse(
                "duration",
                io.getstream.feeds.android.network.models.PollOptionResponseData(
                    id = "option-1",
                    text = "New Option",
                    custom = emptyMap(),
                ),
            )

        testDelegation(
            apiFunction = { feedsApi.createPollOption("pollId", request) },
            repositoryCall = { repository.createPollOption("pollId", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.pollOption.toModel(),
        )
    }

    @Test
    fun `on deletePollOption, delegate to api`() {
        testDelegation(
            apiFunction = { feedsApi.deletePollOption("pollId", "optionId", "userId") },
            repositoryCall = { repository.deletePollOption("pollId", "optionId", "userId") },
            apiResult = Unit,
        )
    }

    @Test
    fun `on getPollOption, delegate to api`() = runTest {
        val apiResult =
            PollOptionResponse(
                "duration",
                io.getstream.feeds.android.network.models.PollOptionResponseData(
                    id = "option-1",
                    text = "Test Option",
                    custom = emptyMap(),
                ),
            )

        testDelegation(
            apiFunction = { feedsApi.getPollOption("pollId", "optionId", "userId") },
            repositoryCall = { repository.getPollOption("pollId", "optionId", "userId") },
            apiResult = apiResult,
            repositoryResult = apiResult.pollOption.toModel(),
        )
    }

    @Test
    fun `on updatePollOption, delegate to api`() = runTest {
        val request = UpdatePollOptionRequest(id = "optionId", text = "Updated Option")
        val apiResult =
            PollOptionResponse(
                "duration",
                io.getstream.feeds.android.network.models.PollOptionResponseData(
                    id = "option-1",
                    text = "Updated Option",
                    custom = emptyMap(),
                ),
            )

        testDelegation(
            apiFunction = { feedsApi.updatePollOption("pollId", request) },
            repositoryCall = { repository.updatePollOption("pollId", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.pollOption.toModel(),
        )
    }

    @Test
    fun `on queryPolls, delegate to api and transform result`() = runTest {
        val query = PollsQuery(limit = 10)
        val apiResult =
            QueryPollsResponse(
                duration = "duration",
                polls = listOf(pollResponseData(), pollResponseData()),
                next = "next-cursor",
                prev = "prev-cursor",
            )

        testDelegation(
            apiFunction = {
                feedsApi.queryPolls(userId = null, queryPollsRequest = query.toRequest())
            },
            repositoryCall = { repository.queryPolls(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = apiResult.polls.map { it.toModel() },
                    pagination = PaginationData(apiResult.next, apiResult.prev),
                ),
        )
    }

    @Test
    fun `on castPollVote, delegate to api`() = runTest {
        val request = CastPollVoteRequest(vote = VoteData(optionId = "option-1"))
        val apiResult = PollVoteResponse("duration", pollResponseData(), pollVoteResponseData())

        testDelegation(
            apiFunction = { feedsApi.castPollVote("activityId", "pollId", request) },
            repositoryCall = { repository.castPollVote("activityId", "pollId", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.vote?.toModel() to apiResult.poll?.toModel(),
        )
    }

    @Test
    fun `on queryPollVotes, delegate to api and transform result`() = runTest {
        val query = PollVotesQuery(pollId = "pollId", userId = "userId", limit = 10)
        val apiResult =
            PollVotesResponse(
                duration = "duration",
                votes = listOf(pollVoteResponseData(), pollVoteResponseData()),
                next = "next-cursor",
                prev = "prev-cursor",
            )

        testDelegation(
            apiFunction = {
                feedsApi.queryPollVotes(
                    pollId = query.pollId,
                    userId = query.userId,
                    queryPollVotesRequest = query.toRequest(),
                )
            },
            repositoryCall = { repository.queryPollVotes(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = apiResult.votes.map { it.toModel() },
                    pagination = PaginationData(apiResult.next, apiResult.prev),
                ),
        )
    }

    @Test
    fun `on deletePollVote, delegate to api`() = runTest {
        val apiResult = PollVoteResponse("duration", pollResponseData(), pollVoteResponseData())

        testDelegation(
            apiFunction = { feedsApi.deletePollVote("activityId", "pollId", "voteId", "userId") },
            repositoryCall = {
                repository.deletePollVote("activityId", "pollId", "voteId", "userId")
            },
            apiResult = apiResult,
            repositoryResult = apiResult.vote?.toModel() to apiResult.poll?.toModel(),
        )
    }
}
