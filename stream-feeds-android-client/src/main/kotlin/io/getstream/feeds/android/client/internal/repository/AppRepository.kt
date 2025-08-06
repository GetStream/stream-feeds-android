package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.AppData

/**
 * An interface for a repository that provides application configuration data.
 *
 * This repository is responsible for fetching the application configuration data,
 * which includes settings like URL enrichment, translation, and file upload configurations.
 */
internal interface AppRepository {

    /**
     * Fetches the application configuration data.
     *
     * @return A [Result] containing the [AppData] if successful, or an error if the request fails.
     */
    suspend fun getApp(): Result<AppData>
}
