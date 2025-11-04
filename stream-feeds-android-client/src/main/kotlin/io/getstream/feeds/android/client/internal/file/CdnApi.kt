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

import io.getstream.feeds.android.network.models.FileUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

internal interface CdnApi {
    @Multipart
    @POST("/api/v2/uploads/file")
    suspend fun sendFile(@Part file: MultipartBody.Part): FileUploadResponse

    @Multipart
    @POST("/api/v2/uploads/image")
    suspend fun sendImage(@Part file: MultipartBody.Part): FileUploadResponse
}
