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

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.core.generated.apis.ApiService

/**
 * Default implementation of [FilesRepository]. Uses [ApiService] to perform file operations.
 *
 * @property api The API service used for file operations.
 */
internal class FilesRepositoryImpl(private val api: ApiService) : FilesRepository {

    override suspend fun deleteFile(url: String): Result<Unit> = runSafely { api.deleteFile(url) }

    override suspend fun deleteImage(url: String): Result<Unit> = runSafely { api.deleteImage(url) }
}
