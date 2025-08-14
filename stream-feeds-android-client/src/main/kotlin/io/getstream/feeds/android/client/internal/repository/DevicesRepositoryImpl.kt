package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.CreateDeviceRequest
import io.getstream.feeds.android.core.generated.models.ListDevicesResponse

/**
 * Default implementation of the [DevicesRepository] interface.
 * Uses the provided [ApiService] to perform network requests related to devices.
 *
 * @property api The API service used to perform network requests.
 */
internal class DevicesRepositoryImpl(private val api: ApiService) : DevicesRepository {

    override suspend fun queryDevices(): Result<ListDevicesResponse> = runSafely {
        api.listDevices()
    }

    override suspend fun createDevice(
        id: String,
        pushProvider: PushNotificationsProvider,
        pushProviderName: String
    ): Result<Unit> = runSafely {
        check(id.isNotBlank()) {
            "Device id must not be empty when trying to set device."
        }
        val pushProvider = CreateDeviceRequest.PushProvider.fromString(pushProvider.value)
        check(pushProvider !is CreateDeviceRequest.PushProvider.Unknown) {
            "Invalid push provider value: $pushProvider"
        }
        val request = CreateDeviceRequest(
            id = id,
            pushProvider = pushProvider,
            pushProviderName = pushProviderName,
        )
        api.createDevice(request)
    }

    override suspend fun deleteDevice(id: String): Result<Unit> = runSafely {
        api.deleteDevice(id)
    }
}