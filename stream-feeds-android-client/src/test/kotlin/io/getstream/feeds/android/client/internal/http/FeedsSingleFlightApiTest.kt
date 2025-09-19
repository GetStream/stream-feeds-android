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
import io.getstream.feeds.android.network.models.AppResponseFields
import io.getstream.feeds.android.network.models.CreateDeviceRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesResponse
import io.getstream.feeds.android.network.models.FileUploadConfig
import io.getstream.feeds.android.network.models.GetApplicationResponse
import io.getstream.feeds.android.network.models.GetOGResponse
import io.getstream.feeds.android.network.models.ListDevicesResponse
import io.getstream.feeds.android.network.models.QueryActivitiesRequest
import io.getstream.feeds.android.network.models.QueryActivitiesResponse
import io.getstream.feeds.android.network.models.Response
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
        private val testAddCommentResponse =
            AddCommentResponse(duration = "100ms", comment = TestData.commentResponse())

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
