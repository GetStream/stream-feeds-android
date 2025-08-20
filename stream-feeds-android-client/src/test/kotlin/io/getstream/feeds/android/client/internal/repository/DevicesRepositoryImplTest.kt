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

import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.CreateDeviceRequest
import io.getstream.feeds.android.core.generated.models.DeviceResponse
import io.getstream.feeds.android.core.generated.models.ListDevicesResponse
import io.getstream.feeds.android.core.generated.models.Response
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId

internal class DevicesRepositoryImplTest {

    private val apiService: ApiService = mockk()
    private val repository = DevicesRepositoryImpl(api = apiService)

    @Test
    fun `queryDevices should return success when api listDevices returns result`() = runTest {
        // Given
        val expectedResponse =
            ListDevicesResponse(
                duration = "100ms",
                devices =
                    listOf(
                        DeviceResponse(
                            createdAt = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")),
                            id = "device-1",
                            pushProvider = "firebase",
                            userId = "user-1",
                        )
                    ),
            )
        coEvery { apiService.listDevices() } returns expectedResponse

        // When
        val result = repository.queryDevices()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
        coVerify(exactly = 1) { apiService.listDevices() }
    }

    @Test
    fun `queryDevices should return failure when api listDevices throws exception`() = runTest {
        // Given
        val expectedException = RuntimeException("API Error")
        coEvery { apiService.listDevices() } throws expectedException

        // When
        val result = repository.queryDevices()

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify(exactly = 1) { apiService.listDevices() }
    }

    @Test
    fun `createDevice should return success when api createDevice completes successfully`() =
        runTest {
            // Given
            val deviceId = "test-device-id"
            val pushProvider = PushNotificationsProvider.FIREBASE
            val pushProviderName = "test-provider"

            val expectedRequest =
                CreateDeviceRequest(
                    id = deviceId,
                    pushProvider = CreateDeviceRequest.PushProvider.Firebase,
                    pushProviderName = pushProviderName,
                )

            coEvery { apiService.createDevice(expectedRequest) } returns Response("")

            // When
            val result =
                repository.createDevice(
                    id = deviceId,
                    pushProvider = pushProvider,
                    pushProviderName = pushProviderName,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(Unit, result.getOrNull())
            coVerify(exactly = 1) { apiService.createDevice(expectedRequest) }
        }

    @Test
    fun `createDevice should return failure when api createDevice throws exception`() = runTest {
        // Given
        val deviceId = "test-device-id"
        val pushProvider = PushNotificationsProvider.FIREBASE
        val pushProviderName = "test-provider"
        val expectedException = RuntimeException("Create device failed")

        val expectedRequest =
            CreateDeviceRequest(
                id = deviceId,
                pushProvider = CreateDeviceRequest.PushProvider.Firebase,
                pushProviderName = pushProviderName,
            )

        coEvery { apiService.createDevice(expectedRequest) } throws expectedException

        // When
        val result =
            repository.createDevice(
                id = deviceId,
                pushProvider = pushProvider,
                pushProviderName = pushProviderName,
            )

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify(exactly = 1) { apiService.createDevice(expectedRequest) }
    }

    @Test
    fun `createDevice should return failure when device id is empty`() = runTest {
        // Given
        val emptyDeviceId = ""
        val pushProvider = PushNotificationsProvider.FIREBASE
        val pushProviderName = "test-provider"

        // When
        val result =
            repository.createDevice(
                id = emptyDeviceId,
                pushProvider = pushProvider,
                pushProviderName = pushProviderName,
            )

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("Device id must not be empty when trying to set device.", exception?.message)
        coVerify(exactly = 0) { apiService.createDevice(any()) }
    }

    @Test
    fun `createDevice should return failure when device id is blank`() = runTest {
        // Given
        val blankDeviceId = "   "
        val pushProvider = PushNotificationsProvider.FIREBASE
        val pushProviderName = "test-provider"

        // When
        val result =
            repository.createDevice(
                id = blankDeviceId,
                pushProvider = pushProvider,
                pushProviderName = pushProviderName,
            )

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("Device id must not be empty when trying to set device.", exception?.message)
        coVerify(exactly = 0) { apiService.createDevice(any()) }
    }

    @Test
    fun `createDevice should work with different push providers`() = runTest {
        // Test HUAWEI provider
        val deviceId = "huawei-device"
        val pushProvider = PushNotificationsProvider.HUAWEI
        val pushProviderName = "huawei-provider"

        val expectedRequest =
            CreateDeviceRequest(
                id = deviceId,
                pushProvider = CreateDeviceRequest.PushProvider.Huawei,
                pushProviderName = pushProviderName,
            )

        coEvery { apiService.createDevice(expectedRequest) } returns Response("")

        val result =
            repository.createDevice(
                id = deviceId,
                pushProvider = pushProvider,
                pushProviderName = pushProviderName,
            )

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { apiService.createDevice(expectedRequest) }
    }

    @Test
    fun `createDevice should work with XIAOMI push provider`() = runTest {
        // Test XIAOMI provider
        val deviceId = "xiaomi-device"
        val pushProvider = PushNotificationsProvider.XIAOMI
        val pushProviderName = "xiaomi-provider"

        val expectedRequest =
            CreateDeviceRequest(
                id = deviceId,
                pushProvider = CreateDeviceRequest.PushProvider.Xiaomi,
                pushProviderName = pushProviderName,
            )

        coEvery { apiService.createDevice(expectedRequest) } returns Response("")

        val result =
            repository.createDevice(
                id = deviceId,
                pushProvider = pushProvider,
                pushProviderName = pushProviderName,
            )

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { apiService.createDevice(expectedRequest) }
    }

    @Test
    fun `deleteDevice should return success when api deleteDevice completes successfully`() =
        runTest {
            // Given
            val deviceId = "test-device-id"
            coEvery { apiService.deleteDevice(deviceId) } returns Response("")

            // When
            val result = repository.deleteDevice(deviceId)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(Unit, result.getOrNull())
            coVerify(exactly = 1) { apiService.deleteDevice(deviceId) }
        }

    @Test
    fun `deleteDevice should return failure when api deleteDevice throws exception`() = runTest {
        // Given
        val deviceId = "test-device-id"
        val expectedException = RuntimeException("Delete device failed")
        coEvery { apiService.deleteDevice(deviceId) } throws expectedException

        // When
        val result = repository.deleteDevice(deviceId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify(exactly = 1) { apiService.deleteDevice(deviceId) }
    }
}
