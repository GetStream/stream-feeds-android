package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.core.generated.models.ListDevicesResponse

/**
 * Repository interface for managing push devices associated with the current user.
 */
internal interface DevicesRepository {

    /**
     * Queries the push devices associated with the current user.
     *
     * @return A [Result] containing a [ListDevicesResponse] with the list of devices, or an error
     * if the query fails.
     */
    suspend fun queryDevices(): Result<ListDevicesResponse>

    /**
     * Creates a new push device for the current user with the specified ID and configuration.
     *
     * @param id The unique identifier for the push device.
     * @param pushProvider The push notifications provider to be used for the device.
     * @param pushProviderName The name of the push provider.
     */
    suspend fun createDevice(
        id: String,
        pushProvider: PushNotificationsProvider,
        pushProviderName: String,
    ): Result<Unit>

    /**
     * Deletes a push device associated with the current user.
     *
     * @param id The unique identifier of the push device to be deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    suspend fun deleteDevice(id: String): Result<Unit>
}
