package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.core.generated.apis.ApiService

/**
 * Default implementation of [FilesRepository].
 * Uses [ApiService] to perform file operations.
 *
 * @property api The API service used for file operations.
 */
internal class FilesRepositoryImpl(private val api: ApiService) : FilesRepository {

    override suspend fun deleteFile(url: String): Result<Unit> = runSafely {
        api.deleteFile(url)
    }

    override suspend fun deleteImage(url: String): Result<Unit> = runSafely {
        api.deleteImage(url)
    }
}
