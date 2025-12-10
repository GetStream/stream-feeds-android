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

package io.getstream.feeds.android.client.internal.file

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.client.internal.http.ProgressRequestBody
import java.io.File
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

internal class StreamFeedUploader(
    private val cdnApi: CdnApi,
    private val getMediaType: suspend File.() -> MediaType = File::getMediaType,
) : FeedUploader {
    override suspend fun upload(
        payload: FeedUploadPayload,
        progress: ((Double) -> Unit)?,
    ): Result<UploadedFile> = runSafely {
        val requestBody =
            payload.file.asRequestBody(payload.file.getMediaType()).withProgressListener(progress)
        val part =
            MultipartBody.Part.createFormData(
                name = "file",
                filename = payload.file.name,
                body = requestBody,
            )

        val response =
            if (payload.type == FileType.Image) {
                cdnApi.sendImage(part)
            } else {
                cdnApi.sendFile(part)
            }

        UploadedFile(
            fileUrl = checkNotNull(response.file) { "Uploaded file URL is null" },
            thumbnailUrl = response.thumbUrl,
        )
    }

    private fun RequestBody.withProgressListener(progress: ((Double) -> Unit)?): RequestBody =
        if (progress == null) {
            this
        } else {
            ProgressRequestBody(this) { uploaded, total -> progress(uploaded.toDouble() / total) }
        }
}
