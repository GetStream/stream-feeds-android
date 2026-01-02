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

import io.getstream.feeds.android.client.api.model.BatchFollowData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.client.internal.test.TestData.acceptFeedMemberResponse
import io.getstream.feeds.android.client.internal.test.TestData.acceptFollowResponse
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedResponse
import io.getstream.feeds.android.client.internal.test.TestData.followBatchResponse
import io.getstream.feeds.android.client.internal.test.TestData.followResponse
import io.getstream.feeds.android.client.internal.test.TestData.followSuggestionsResponse
import io.getstream.feeds.android.client.internal.test.TestData.getOrCreateFeedResponse
import io.getstream.feeds.android.client.internal.test.TestData.queryFeedMembersResponse
import io.getstream.feeds.android.client.internal.test.TestData.queryFeedsResponse
import io.getstream.feeds.android.client.internal.test.TestData.queryFollowsResponse
import io.getstream.feeds.android.client.internal.test.TestData.rejectFeedMemberResponse
import io.getstream.feeds.android.client.internal.test.TestData.rejectFollowResponse
import io.getstream.feeds.android.client.internal.test.TestData.singleFollowResponse
import io.getstream.feeds.android.client.internal.test.TestData.unfollowBatchResponse
import io.getstream.feeds.android.client.internal.test.TestData.updateFeedMembersResponse
import io.getstream.feeds.android.client.internal.test.TestData.updateFeedResponse
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AcceptFollowRequest
import io.getstream.feeds.android.network.models.ActivityResponse
import io.getstream.feeds.android.network.models.FeedSuggestionResponse
import io.getstream.feeds.android.network.models.FollowBatchRequest
import io.getstream.feeds.android.network.models.FollowPair
import io.getstream.feeds.android.network.models.FollowRequest
import io.getstream.feeds.android.network.models.QueryFeedMembersRequest
import io.getstream.feeds.android.network.models.QueryFollowsRequest
import io.getstream.feeds.android.network.models.RejectFollowRequest
import io.getstream.feeds.android.network.models.UnfollowBatchRequest
import io.getstream.feeds.android.network.models.UnfollowResponse
import io.getstream.feeds.android.network.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.network.models.UpdateFeedRequest
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class FeedsRepositoryImplTest {
    private val feedsApi: FeedsApi = mockk()
    private val repository = FeedsRepositoryImpl(api = feedsApi)

    @Test
    fun `on getOrCreateFeed, delegate to api`() = runTest {
        val query = FeedQuery(group = "user", id = "user-1")
        val request = query.toRequest()
        val apiResult =
            getOrCreateFeedResponse(
                activities =
                    listOf(
                        activityResponse("activity-2", createdAt = 2000),
                        activityResponse("activity-1", createdAt = 1000),
                        activityResponse("activity-3", createdAt = 3000),
                    )
            )

        testDelegation(
            apiFunction = {
                feedsApi.getOrCreateFeed("user", "user-1", getOrCreateFeedRequest = request)
            },
            repositoryCall = { repository.getOrCreateFeed(query) },
            apiResult = apiResult,
            repositoryResult =
                GetOrCreateInfo(
                    pagination = PaginationData(next = "next", previous = "prev"),
                    activities = apiResult.activities.map(ActivityResponse::toModel),
                    aggregatedActivities = emptyList(),
                    feed = apiResult.feed.toModel(),
                    followers = emptyList(),
                    following = emptyList(),
                    followRequests = emptyList(),
                    members =
                        PaginationResult(models = emptyList(), pagination = PaginationData.EMPTY),
                    pinnedActivities = emptyList(),
                    notificationStatus = null,
                ),
        )
    }

    @Test
    fun `on stopWatching, delegate to api`() = runTest {
        testDelegation(
            apiFunction = { feedsApi.stopWatchingFeed("user", "user-1") },
            repositoryCall = { repository.stopWatching("user", "user-1") },
            apiResult = Unit,
        )
    }

    @Test
    fun `on deleteFeed, delegate to api`() = runTest {
        testDelegation(
            apiFunction = { feedsApi.deleteFeed("user", "user-1", true) },
            repositoryCall = { repository.deleteFeed("user", "user-1", true) },
            apiResult = Unit,
        )
    }

    @Test
    fun `on updateFeed, delegate to api`() = runTest {
        val request = UpdateFeedRequest()
        val apiResult = updateFeedResponse()

        testDelegation(
            apiFunction = { feedsApi.updateFeed("user", "user-1", request) },
            repositoryCall = { repository.updateFeed("user", "user-1", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.feed.toModel(),
        )
    }

    @Test
    fun `on queryFeeds, delegate to api`() = runTest {
        val query = FeedsQuery(limit = 10)
        val request = query.toRequest()
        val apiResult = queryFeedsResponse()

        testDelegation(
            apiFunction = { feedsApi.queryFeeds(queryFeedsRequest = request) },
            repositoryCall = { repository.queryFeeds(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = listOf(feedResponse().toModel()),
                    pagination = PaginationData(next = "next", previous = "prev"),
                ),
        )
    }

    @Test
    fun `on queryFollowSuggestions, delegate to api`() = runTest {
        val apiResult = followSuggestionsResponse()

        testDelegation(
            apiFunction = { feedsApi.getFollowSuggestions("user", 10) },
            repositoryCall = { repository.queryFollowSuggestions("user", 10) },
            apiResult = apiResult,
            repositoryResult = apiResult.suggestions.map(FeedSuggestionResponse::toModel),
        )
    }

    @Test
    fun `on queryFollows, delegate to api`() = runTest {
        val request = QueryFollowsRequest()
        val apiResult = queryFollowsResponse()

        testDelegation(
            apiFunction = { feedsApi.queryFollows(request) },
            repositoryCall = { repository.queryFollows(request) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = listOf(followResponse().toModel()),
                    pagination = PaginationData(next = "next", previous = "prev"),
                ),
        )
    }

    @Test
    fun `on follow, delegate to api`() = runTest {
        val request = FollowRequest("source", "target")
        val apiResult = singleFollowResponse()

        testDelegation(
            apiFunction = { feedsApi.follow(request) },
            repositoryCall = { repository.follow(request) },
            apiResult = apiResult,
            repositoryResult = apiResult.follow.toModel(),
        )
    }

    @Test
    fun `on unfollow, delegate to api`() = runTest {
        val source = FeedId("user:user-1")
        val target = FeedId("user:user-2")
        val followResponseData = followResponse()
        val unfollowResponse = UnfollowResponse(duration = "duration", follow = followResponseData)

        testDelegation(
            apiFunction = { feedsApi.unfollow("user:user-1", "user:user-2") },
            repositoryCall = { repository.unfollow(source, target) },
            apiResult = unfollowResponse,
            repositoryResult = unfollowResponse.follow.toModel(),
        )
    }

    @Test
    fun `on acceptFollow, delegate to api`() = runTest {
        val request = AcceptFollowRequest("source", "target")
        val apiResult = acceptFollowResponse()

        testDelegation(
            apiFunction = { feedsApi.acceptFollow(request) },
            repositoryCall = { repository.acceptFollow(request) },
            apiResult = apiResult,
            repositoryResult = apiResult.follow.toModel(),
        )
    }

    @Test
    fun `on rejectFollow, delegate to api`() = runTest {
        val request = RejectFollowRequest("source", "target")
        val apiResult = rejectFollowResponse()

        testDelegation(
            apiFunction = { feedsApi.rejectFollow(request) },
            repositoryCall = { repository.rejectFollow(request) },
            apiResult = apiResult,
            repositoryResult = apiResult.follow.toModel(),
        )
    }

    @Test
    fun `on updateFeedMembers, delegate to api`() = runTest {
        val request = UpdateFeedMembersRequest(UpdateFeedMembersRequest.Operation.Remove)
        val apiResult = updateFeedMembersResponse()

        testDelegation(
            apiFunction = { feedsApi.updateFeedMembers("user", "user-1", request) },
            repositoryCall = { repository.updateFeedMembers("user", "user-1", request) },
            apiResult = apiResult,
            repositoryResult =
                ModelUpdates(added = emptyList(), removedIds = emptySet(), updated = emptyList()),
        )
    }

    @Test
    fun `on acceptFeedMember, delegate to api`() = runTest {
        val apiResult = acceptFeedMemberResponse()

        testDelegation(
            apiFunction = {
                feedsApi.acceptFeedMemberInvite(feedGroupId = "user", feedId = "user-1")
            },
            repositoryCall = { repository.acceptFeedMember("user", "user-1") },
            apiResult = apiResult,
            repositoryResult = apiResult.member.toModel(),
        )
    }

    @Test
    fun `on rejectFeedMember, delegate to api`() = runTest {
        val apiResult = rejectFeedMemberResponse()

        testDelegation(
            apiFunction = {
                feedsApi.rejectFeedMemberInvite(feedGroupId = "user", feedId = "user-1")
            },
            repositoryCall = { repository.rejectFeedMember("user", "user-1") },
            apiResult = apiResult,
            repositoryResult = apiResult.member.toModel(),
        )
    }

    @Test
    fun `on queryFeedMembers, delegate to api`() = runTest {
        val request = QueryFeedMembersRequest()
        val apiResult = queryFeedMembersResponse()

        testDelegation(
            apiFunction = { feedsApi.queryFeedMembers("user", "user-1", request) },
            repositoryCall = { repository.queryFeedMembers("user", "user-1", request) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = listOf(feedMemberResponse().toModel()),
                    pagination = PaginationData(next = "next", previous = "prev"),
                ),
        )
    }

    @Test
    fun `on getOrCreateFollows, delegate to api`() = runTest {
        val follow1 = followResponse()
        val follow2 =
            followResponse(
                source = feedResponse(id = "user-2"),
                target = feedResponse(id = "user-3"),
            )
        val apiResult =
            followBatchResponse(created = listOf(follow1), follows = listOf(follow1, follow2))
        val request = FollowBatchRequest(follows = listOf(FollowRequest("user-1", "user-2")))

        testDelegation(
            apiFunction = { feedsApi.getOrCreateFollows(request) },
            repositoryCall = { repository.getOrCreateFollows(request) },
            apiResult = apiResult,
            repositoryResult =
                BatchFollowData(
                    created = listOf(follow1.toModel()),
                    follows = listOf(follow1.toModel(), follow2.toModel()),
                ),
        )
    }

    @Test
    fun `on getOrCreateUnfollows, delegate to api`() = runTest {
        val follow1 = followResponse()
        val follow2 =
            followResponse(
                source = feedResponse(id = "user-2"),
                target = feedResponse(id = "user-3"),
            )
        val apiResult = unfollowBatchResponse(follows = listOf(follow1, follow2))
        val request =
            UnfollowBatchRequest(follows = listOf(FollowPair("user:user-1", "user:user-2")))

        testDelegation(
            apiFunction = { feedsApi.getOrCreateUnfollows(request) },
            repositoryCall = { repository.getOrCreateUnfollows(request) },
            apiResult = apiResult,
            repositoryResult = listOf(follow1.toModel(), follow2.toModel()),
        )
    }
}
