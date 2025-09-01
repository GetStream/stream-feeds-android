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
package io.getstream.feeds.android.client.internal.client

import io.getstream.android.core.api.StreamClient
import io.getstream.android.core.api.log.StreamLogger
import io.getstream.android.core.api.model.connection.StreamConnectedUser
import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.android.core.api.model.value.StreamApiKey
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.Moderation
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.client.api.model.User
import io.getstream.feeds.android.client.api.model.UserAuthType
import io.getstream.feeds.android.client.internal.client.reconnect.ConnectionRecoveryHandler
import io.getstream.feeds.android.client.internal.client.reconnect.FeedWatchHandler
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.AppRepository
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.DevicesRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.FilesRepository
import io.getstream.feeds.android.client.internal.repository.ModerationRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.appData
import io.getstream.feeds.android.network.models.ActivityRequest
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesResponse
import io.getstream.feeds.android.network.models.ListDevicesResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FeedsClientImplTest {
    private val coreClient: StreamClient = mockk(relaxed = true)
    private val feedsEventsSubscriptionManager: StreamSubscriptionManager<FeedsEventListener> =
        mockk(relaxed = true)
    private val apiKey: StreamApiKey = StreamApiKey.fromString("test-api-key")
    private val user: User = User(id = "test-user", type = UserAuthType.REGULAR)
    private val connectionRecoveryHandler: ConnectionRecoveryHandler = mockk(relaxed = true)
    private val activitiesRepository: ActivitiesRepository = mockk(relaxed = true)
    private val appRepository: AppRepository = mockk(relaxed = true)
    private val bookmarksRepository: BookmarksRepository = mockk(relaxed = true)
    private val commentsRepository: CommentsRepository = mockk(relaxed = true)
    private val devicesRepository: DevicesRepository = mockk(relaxed = true)
    private val feedsRepository: FeedsRepository = mockk(relaxed = true)
    private val filesRepository: FilesRepository = mockk(relaxed = true)
    private val moderationRepository: ModerationRepository = mockk(relaxed = true)
    private val pollsRepository: PollsRepository = mockk(relaxed = true)
    private val uploader: FeedUploader = mockk(relaxed = true)
    private val moderation: Moderation = mockk(relaxed = true)
    private val feedWatchHandler: FeedWatchHandler = mockk(relaxed = true)
    private val logger: StreamLogger = mockk(relaxed = true)

    private val feedsClient: FeedsClientImpl =
        FeedsClientImpl(
            coreClient = coreClient,
            feedsEventsSubscriptionManager = feedsEventsSubscriptionManager,
            apiKey = apiKey,
            user = user,
            connectionRecoveryHandler = connectionRecoveryHandler,
            activitiesRepository = activitiesRepository,
            appRepository = appRepository,
            bookmarksRepository = bookmarksRepository,
            commentsRepository = commentsRepository,
            devicesRepository = devicesRepository,
            feedsRepository = feedsRepository,
            filesRepository = filesRepository,
            moderationRepository = moderationRepository,
            pollsRepository = pollsRepository,
            uploader = uploader,
            moderation = moderation,
            feedWatchHandler = feedWatchHandler,
            logger = logger,
        )

    @Test
    fun `connect when user is regular, then subscribes to client and connects successfully`() =
        runTest {
            val connectedUser =
                StreamConnectedUser(
                    createdAt = Date(1000),
                    id = "user-1",
                    language = "en",
                    role = "user",
                    updatedAt = Date(1000),
                    teams = emptyList(),
                )
            coEvery { coreClient.connect() } returns Result.success(connectedUser)
            every { coreClient.subscribe(any()) } returns Result.success(mockk())

            val result = feedsClient.connect()

            assertEquals(connectedUser, result.getOrNull())
        }

    @Test
    fun `connect when user is anonymous, then returns failure`() = runTest {
        val anonymousUser = User(id = "!anon", type = UserAuthType.ANONYMOUS)
        val anonymousClient =
            FeedsClientImpl(
                coreClient = coreClient,
                feedsEventsSubscriptionManager = feedsEventsSubscriptionManager,
                apiKey = apiKey,
                user = anonymousUser,
                connectionRecoveryHandler = connectionRecoveryHandler,
                activitiesRepository = activitiesRepository,
                appRepository = appRepository,
                bookmarksRepository = bookmarksRepository,
                commentsRepository = commentsRepository,
                devicesRepository = devicesRepository,
                feedsRepository = feedsRepository,
                filesRepository = filesRepository,
                moderationRepository = moderationRepository,
                pollsRepository = pollsRepository,
                uploader = uploader,
                moderation = moderation,
                feedWatchHandler = feedWatchHandler,
                logger = logger,
            )

        val result = anonymousClient.connect()

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Anonymous users cannot connect.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `disconnect when called, then stops recovery handler and disconnects core client`() =
        runTest {
            coEvery { coreClient.disconnect() } returns Result.success(Unit)

            assertTrue(feedsClient.disconnect().isSuccess)
        }

    @Test
    fun `addActivity when given request, then delegates to activities repository`() = runTest {
        val request = AddActivityRequest(type = "post", text = "Hello world")
        val activityData = activityData(id = "activity-1", text = "Hello world")
        coEvery { activitiesRepository.addActivity(request) } returns Result.success(activityData)

        val result = feedsClient.addActivity(request)

        assertEquals(activityData, result.getOrNull())
    }

    @Test
    fun `addActivity when repository fails, then returns failure`() = runTest {
        val request = AddActivityRequest(type = "post", text = "Hello world")
        val exception = RuntimeException("Network error")
        coEvery { activitiesRepository.addActivity(request) } returns Result.failure(exception)

        val result = feedsClient.addActivity(request)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `upsertActivities when given activities, then delegates to activities repository`() =
        runTest {
            val activities = listOf(ActivityRequest(type = "post", text = "Hello world"))
            val activityDataList = listOf(activityData(id = "activity-1", text = "Hello world"))
            coEvery { activitiesRepository.upsertActivities(activities) } returns
                Result.success(activityDataList)

            val result = feedsClient.upsertActivities(activities)

            assertEquals(activityDataList, result.getOrNull())
        }

    @Test
    fun `upsertActivities when repository fails, then returns failure`() = runTest {
        val activities = listOf(ActivityRequest(type = "post", text = "Hello world"))
        val exception = RuntimeException("Network error")
        coEvery { activitiesRepository.upsertActivities(activities) } returns
            Result.failure(exception)

        val result = feedsClient.upsertActivities(activities)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `deleteActivities when given request, then delegates to activities repository`() = runTest {
        val request = DeleteActivitiesRequest(ids = listOf("activity-1", "activity-2"))
        val deleteResponse =
            DeleteActivitiesResponse(
                duration = "50ms",
                deletedIds = listOf("activity-1", "activity-2"),
            )
        coEvery { activitiesRepository.deleteActivities(request) } returns
            Result.success(deleteResponse)

        val result = feedsClient.deleteActivities(request)

        assertEquals(deleteResponse, result.getOrNull())
    }

    @Test
    fun `getApp when called, then delegates to app repository`() = runTest {
        val testAppData = appData(name = "Test App")
        coEvery { appRepository.getApp() } returns Result.success(testAppData)

        val result = feedsClient.getApp()

        assertEquals(testAppData, result.getOrNull())
    }

    @Test
    fun `queryDevices when called, then delegates to devices repository`() = runTest {
        val devicesResponse = ListDevicesResponse(duration = "100ms", devices = emptyList())
        coEvery { devicesRepository.queryDevices() } returns Result.success(devicesResponse)

        val result = feedsClient.queryDevices()

        assertEquals(devicesResponse, result.getOrNull())
    }

    @Test
    fun `createDevice when given parameters, then delegates to devices repository`() = runTest {
        val deviceId = "device-123"
        val pushProvider = PushNotificationsProvider.FIREBASE
        val pushProviderName = "firebase"
        coEvery { devicesRepository.createDevice(deviceId, pushProvider, pushProviderName) } returns
            Result.success(Unit)

        val result = feedsClient.createDevice(deviceId, pushProvider, pushProviderName)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteDevice when given device id, then delegates to devices repository`() = runTest {
        val deviceId = "device-123"
        coEvery { devicesRepository.deleteDevice(deviceId) } returns Result.success(Unit)

        val result = feedsClient.deleteDevice(deviceId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteFile when given url, then delegates to files repository`() = runTest {
        val fileUrl = "https://example.com/file.jpg"
        coEvery { filesRepository.deleteFile(fileUrl) } returns Result.success(Unit)

        val result = feedsClient.deleteFile(fileUrl)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteImage when given url, then delegates to files repository`() = runTest {
        val imageUrl = "https://example.com/image.jpg"
        coEvery { filesRepository.deleteImage(imageUrl) } returns Result.success(Unit)

        val result = feedsClient.deleteImage(imageUrl)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `state when accessed, then returns core client connection state`() {
        val connectionState = mockk<StreamConnectionState>()
        val stateFlow = MutableStateFlow(connectionState)
        every { coreClient.connectionState } returns stateFlow

        val result = feedsClient.state

        assertEquals(stateFlow, result)
    }

    @Test
    fun `apiKey when accessed, then returns correct api key`() {
        val result = feedsClient.apiKey

        assertEquals(apiKey, result)
    }

    @Test
    fun `user when accessed, then returns correct user`() {
        val result = feedsClient.user

        assertEquals(user, result)
    }

    @Test
    fun `uploader when accessed, then returns correct uploader`() {
        val result = feedsClient.uploader

        assertEquals(uploader, result)
    }

    @Test
    fun `moderation when accessed, then returns correct moderation`() {
        val result = feedsClient.moderation

        assertEquals(moderation, result)
    }
}
