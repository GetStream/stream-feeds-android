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

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.CreateDeviceRequest
import io.getstream.feeds.android.network.models.ListDevicesResponse
import io.getstream.feeds.android.network.models.UpsertPushPreferencesRequest
import io.getstream.feeds.android.network.models.UpsertPushPreferencesResponse

/**
 * Default implementation of the [DevicesRepository] interface. Uses the provided [FeedsApi] to
 * perform network requests related to devices.
 *
 * @property api The API service used to perform network requests.
 */
internal class DevicesRepositoryImpl(private val api: FeedsApi) : DevicesRepository {

    override suspend fun queryDevices(): Result<ListDevicesResponse> = runSafely {
        api.listDevices()
    }

    override suspend fun createDevice(
        id: String,
        pushProvider: PushNotificationsProvider,
        pushProviderName: String,
    ): Result<Unit> = runSafely {
        check(id.isNotBlank()) { "Device id must not be empty when trying to set device." }
        val requestPushProvider = CreateDeviceRequest.PushProvider.fromString(pushProvider.value)
        check(requestPushProvider !is CreateDeviceRequest.PushProvider.Unknown) {
            "Invalid push provider value: $pushProvider"
        }
        val request =
            CreateDeviceRequest(
                id = id,
                pushProvider = requestPushProvider,
                pushProviderName = pushProviderName,
            )
        api.createDevice(request)
    }

    override suspend fun deleteDevice(id: String): Result<Unit> = runSafely { api.deleteDevice(id) }

    override suspend fun updatePushNotificationPreferences(
        request: UpsertPushPreferencesRequest
    ): Result<UpsertPushPreferencesResponse> = runSafely {
        api.updatePushNotificationPreferences(request)
    }
}
