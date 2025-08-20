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

/**
 * A sealed interface representing the semantic category of a file.
 *
 * This is used to differentiate between file types that may require different handling or
 * validation, such as images versus other generic files.
 */
public sealed interface FileType {
    public data class Image(val format: String) : FileType

    public data class Other(val format: String) : FileType
}

/**
 * A convenience extension property to check if a [FileType] is an image.
 *
 * @return `true` if this instance is an image, `false` otherwise.
 */
public val FileType.isImage
    get() = this is FileType.Image
