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
package io.getstream.feeds.android.client.internal.http

import io.getstream.android.core.api.model.StreamTypedKey
import io.getstream.android.core.api.processing.StreamSingleFlightProcessor
import io.getstream.feeds.android.client.internal.test.TestData
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.AddActivityResponse
import io.getstream.feeds.android.network.models.AddBookmarkRequest
import io.getstream.feeds.android.network.models.AddBookmarkResponse
import io.getstream.feeds.android.network.models.AddCommentRequest
import io.getstream.feeds.android.network.models.AddCommentResponse
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.AddReactionResponse
import io.getstream.feeds.android.network.models.AppResponseFields
import io.getstream.feeds.android.network.models.CastPollVoteRequest
import io.getstream.feeds.android.network.models.CreateDeviceRequest
import io.getstream.feeds.android.network.models.CreatePollOptionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesResponse
import io.getstream.feeds.android.network.models.FileUploadConfig
import io.getstream.feeds.android.network.models.GetApplicationResponse
import io.getstream.feeds.android.network.models.GetOGResponse
import io.getstream.feeds.android.network.models.ListDevicesResponse
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchRequest
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchResponse
import io.getstream.feeds.android.network.models.PollOptionResponse
import io.getstream.feeds.android.network.models.PollResponse
import io.getstream.feeds.android.network.models.PollVotesResponse
import io.getstream.feeds.android.network.models.QueryActivitiesRequest
import io.getstream.feeds.android.network.models.QueryActivitiesResponse
import io.getstream.feeds.android.network.models.QueryPollVotesRequest
import io.getstream.feeds.android.network.models.QueryPollsResponse
import io.getstream.feeds.android.network.models.Response
import io.getstream.feeds.android.network.models.UpdatePollOptionRequest
import io.getstream.feeds.android.network.models.UpdatePollPartialRequest
import io.getstream.feeds.android.network.models.UpdatePollRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class FeedsSingleFlightApiTest(private val testCase: SingleFlightTestCase<Any>) {

    @Test
    fun `test single flight wrapper methods`() {
        testSingleFlightWrapper(testCase)
    }

    internal data class SingleFlightTestCase<T>(
        val testName: String,
        val expectedKeyPrefix: String,
        val call: suspend (FeedsApi) -> T,
        val apiResult: T,
    )

    companion object {
        private val testAppResponse =
            GetApplicationResponse(
                duration = "100ms",
                app =
                    AppResponseFields(
                        asyncUrlEnrichEnabled = false,
                        autoTranslationEnabled = false,
                        name = "Test App",
                        fileUploadConfig = FileUploadConfig(sizeLimit = 0),
                        imageUploadConfig = FileUploadConfig(sizeLimit = 0),
                        region = "region",
                        shard = "shard",
                    ),
            )

        private val testListDevicesResponse = ListDevicesResponse(duration = "100ms")
        private val testDeleteDeviceResponse = Response(duration = "100ms")
        private val testGetOGResponse = GetOGResponse(duration = "100ms")
        private val testAddActivityResponse =
            AddActivityResponse(duration = "100ms", activity = TestData.activityResponse())
        private val testQueryActivitiesResponse = QueryActivitiesResponse(duration = "100ms")
        private val testDeleteActivitiesResponse = DeleteActivitiesResponse(duration = "100ms")
        private val testCreateDeviceResponse = Response(duration = "100ms")
        private val testAddBookmarkResponse =
            AddBookmarkResponse(duration = "100ms", bookmark = TestData.bookmarkResponse())
        private val testPollVoteResponse =
            PollVotesResponse(
                duration = "100ms",
                votes = listOf(TestData.pollVoteResponseData()),
                next = "next",
                prev = "prev",
            )
        private val testPollResponse =
            PollResponse(duration = "100ms", poll = TestData.pollResponseData())
        private val testQueryPollsResponse =
            QueryPollsResponse(
                duration = "100ms",
                polls = listOf(TestData.pollResponseData()),
                next = "next",
                prev = "prev",
            )
        private val testResponse = Response("100ms")
        private val testPollOptionResponse =
            PollOptionResponse(duration = "100ms", pollOption = TestData.pollOptionResponseData())

        private val testAddCommentResponse =
            AddCommentResponse(duration = "100ms", comment = TestData.commentResponse())
        private val testAddReactionResponse =
            AddReactionResponse(
                duration = "100ms",
                activity = TestData.activityResponse(),
                reaction = TestData.feedsReactionResponse(),
            )
        private val testOwnCapabilitiesBatchResponse = OwnCapabilitiesBatchResponse("100ms")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testCases(): List<Array<Any>> =
            listOf(
                arrayOf(
                    SingleFlightTestCase(
                        testName = "getApp",
                        expectedKeyPrefix = "getApp",
                        call = { api -> api.getApp() },
                        apiResult = testAppResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "listDevices",
                        expectedKeyPrefix = "listDevices",
                        call = { api -> api.listDevices() },
                        apiResult = testListDevicesResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "deleteDevice",
                        expectedKeyPrefix = "deleteDevice-device123",
                        call = { api -> api.deleteDevice("device123") },
                        apiResult = testDeleteDeviceResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "getOG",
                        expectedKeyPrefix = "getOG-https://example.com",
                        call = { api -> api.getOG("https://example.com") },
                        apiResult = testGetOGResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "addActivity",
                        expectedKeyPrefix = "addActivity-",
                        call = { it.addActivity(AddActivityRequest(type = "text")) },
                        apiResult = testAddActivityResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "queryActivities",
                        expectedKeyPrefix = "queryActivities-",
                        call = { it.queryActivities(QueryActivitiesRequest()) },
                        apiResult = testQueryActivitiesResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "deleteActivities",
                        expectedKeyPrefix = "deleteActivities-",
                        call = {
                            it.deleteActivities(DeleteActivitiesRequest(ids = listOf("activity-1")))
                        },
                        apiResult = testDeleteActivitiesResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "createDevice",
                        expectedKeyPrefix = "createDevice-",
                        call = {
                            it.createDevice(
                                CreateDeviceRequest(
                                    id = "device-1",
                                    pushProvider = CreateDeviceRequest.PushProvider.Firebase,
                                )
                            )
                        },
                        apiResult = testCreateDeviceResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "addBookmark",
                        expectedKeyPrefix = "addBookmark-activity123-",
                        call = { it.addBookmark("activity123", AddBookmarkRequest()) },
                        apiResult = testAddBookmarkResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "castPollVote",
                        expectedKeyPrefix = "castPollVote-activity123-",
                        call = { it.castPollVote("activity123", "poll123", CastPollVoteRequest()) },
                        apiResult = testPollVoteResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "deletePollVote",
                        expectedKeyPrefix = "deletePollVote-activity123-",
                        call = { it.deletePollVote("activity123", "poll123", "vote123") },
                        apiResult = testPollVoteResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "createPoll",
                        expectedKeyPrefix = "createPoll-",
                        call = { it.createPoll(CreatePollRequest(name = "Test Poll")) },
                        apiResult = testPollResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "updatePoll",
                        expectedKeyPrefix = "updatePoll-",
                        call = { it.updatePoll(UpdatePollRequest("poll-1", "UpdatedName")) },
                        apiResult = testPollResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "queryPolls",
                        expectedKeyPrefix = "queryPolls-",
                        call = { it.queryPolls() },
                        apiResult = testQueryPollsResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "deletePoll",
                        expectedKeyPrefix = "deletePoll-poll123",
                        call = { it.deletePoll("poll123") },
                        apiResult = testResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "getPoll",
                        expectedKeyPrefix = "getPoll-poll123",
                        call = { it.getPoll("poll123") },
                        apiResult = testPollResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "updatePollPartial",
                        expectedKeyPrefix = "updatePollPartial-poll123",
                        call = {
                            it.updatePollPartial(
                                "poll123",
                                UpdatePollPartialRequest(
                                    unset = listOf("description"),
                                    set = mapOf("name" to "Partially Updated Poll"),
                                ),
                            )
                        },
                        apiResult = testPollResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "updatePollPartialWithNoBodyRequestNeeded",
                        expectedKeyPrefix = "updatePollPartial-poll123",
                        call = { it.updatePollPartial("poll123") },
                        apiResult = testPollResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "createPollOption",
                        expectedKeyPrefix = "createPollOption-poll123",
                        call = {
                            it.createPollOption("poll123", CreatePollOptionRequest("Create Option"))
                        },
                        apiResult = testPollOptionResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "updatePollOption",
                        expectedKeyPrefix = "updatePollOption-poll123",
                        call = {
                            it.updatePollOption(
                                "poll123",
                                UpdatePollOptionRequest(
                                    id = "option456",
                                    text = "Updated option text",
                                    custom = mapOf("updatedBy" to "Ezra"),
                                ),
                            )
                        },
                        apiResult = testPollOptionResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "deletePollOption",
                        expectedKeyPrefix = "deletePollOption-poll123-option456",
                        call = { it.deletePollOption(pollId = "poll123", optionId = "option456") },
                        apiResult = testResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "getPollOption",
                        expectedKeyPrefix = "getPollOption-poll123-option456",
                        call = { it.getPollOption(pollId = "poll123", optionId = "option456") },
                        apiResult = testPollOptionResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "queryPollVotes",
                        expectedKeyPrefix = "queryPollVotes-poll123",
                        call = {
                            it.queryPollVotes(
                                pollId = "poll123",
                                queryPollVotesRequest = QueryPollVotesRequest(),
                            )
                        },
                        apiResult = testPollVoteResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "addActivityReaction",
                        expectedKeyPrefix = "addActivityReaction-activity123-",
                        call = {
                            it.addActivityReaction("activity123", AddReactionRequest(type = "like"))
                        },
                        apiResult = testAddReactionResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "addComment",
                        expectedKeyPrefix = "addComment-",
                        call = {
                            it.addComment(
                                AddCommentRequest(
                                    comment = "Nice!",
                                    objectId = "activity-1",
                                    objectType = "activity",
                                )
                            )
                        },
                        apiResult = testAddCommentResponse,
                    )
                ),
                arrayOf(
                    SingleFlightTestCase(
                        testName = "ownCapabilitiesBatch",
                        expectedKeyPrefix = "ownCapabilitiesBatch-connection123-",
                        call = {
                            it.ownCapabilitiesBatch("connection123", OwnCapabilitiesBatchRequest())
                        },
                        apiResult = testOwnCapabilitiesBatchResponse,
                    )
                ),
            )
    }

    fun <T> testSingleFlightWrapper(testCase: SingleFlightTestCase<T>) = runTest {
        val mockDelegate = mockk<FeedsApi>()
        val mockProcessor = mockk<StreamSingleFlightProcessor>()
        val singleFlightApi = FeedsSingleFlightApi(mockDelegate, mockProcessor)

        // Setup the mock API call
        coEvery { testCase.call(mockDelegate) } returns testCase.apiResult

        // Mock the single flight processor to return the API result wrapped in success
        coEvery { mockProcessor.run(any<StreamTypedKey<Any>>(), any()) } coAnswers
            {
                val block = secondArg<suspend () -> Any>()
                Result.success(block())
            }

        // Execute
        val result = testCase.call(singleFlightApi)

        // Verify the API was called
        coVerify { testCase.call(mockDelegate) }

        // Verify the single flight processor was called
        coVerify {
            mockProcessor.run(
                match { it.id.toString().startsWith(testCase.expectedKeyPrefix) },
                any<suspend () -> Any>(),
            )
        }

        // Verify the result
        assertEquals(testCase.apiResult, result)
    }
}
