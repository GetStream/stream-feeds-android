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
 * The context for uploading feed-related files. It's an interface so that clients can implement
 * multiple upload contexts if needed and share the same uploader.
 */
public interface FeedUploadContext : UploadContext

/**
 * Default implementation of [FeedUploadContext] to be used when you want to upload files without
 * additional context.
 */
public data object EmptyFeedUploadContext : FeedUploadContext

public typealias FeedUploadPayload = UploadPayload<FeedUploadContext>

public fun FeedUploadPayload(file: File, type: FileType): FeedUploadPayload =
    FeedUploadPayload(file, type, EmptyFeedUploadContext)

public typealias FeedUploader = Uploader<FeedUploadContext>
