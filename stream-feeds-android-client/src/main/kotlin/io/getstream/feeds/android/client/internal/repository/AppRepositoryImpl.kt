package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.core.generated.apis.ApiService

/**
 * Default implementation of the [AppRepository].
 * Uses the [ApiService] to fetch application configuration data. Caches the result to avoid
 * multiple network calls for the same data.
 *
 * @property api The API service used to fetch application data.
 */
internal class AppRepositoryImpl(private val api: ApiService) : AppRepository {

    private var cachedAppData: AppData? = null

    override suspend fun getApp(): Result<AppData> = runCatching {
        if (cachedAppData != null) {
            return Result.success(cachedAppData!!)
        }
        api.getApp().app.toModel()
            .also { cachedAppData = it }
    }
}
