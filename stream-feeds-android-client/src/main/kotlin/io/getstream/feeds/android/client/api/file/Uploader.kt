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
package io.getstream.feeds.android.client.api.file

/** The Uploader is responsible for uploading files. */
public interface Uploader<C : UploadContext> {
    /**
     * Uploads a file and returns the information of the uploaded remote file.
     *
     * @param payload The payload containing the information about the file to upload.
     * @param progress A lambda that notifies about upload progress (from 0.0 to 1.0).
     * @return A [Result] containing the uploaded file's information or an error.
     */
    public suspend fun upload(
        payload: UploadPayload<C>,
        progress: ((Double) -> Unit)? = null,
    ): Result<UploadedFile>
}
