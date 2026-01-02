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

package io.getstream.feeds.android.client.api.file

import java.io.File

/**
 * Represents a complete, self-contained request for a file upload operation.
 *
 * This class encapsulates all the necessary information for the uploader: the file itself, its
 * type, and the contextual metadata required to process it.
 *
 * @property file The local [File] object on disk that is to be uploaded.
 * @property type The semantic type of the file, represented by the [FileType] interface.
 * @property context The metadata required to process the upload.
 */
public data class UploadPayload<C : UploadContext>(
    val file: File,
    val type: FileType,
    val context: C,
)
