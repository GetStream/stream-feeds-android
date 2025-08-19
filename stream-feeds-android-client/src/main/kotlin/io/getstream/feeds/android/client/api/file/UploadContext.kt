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
 * A marker interface for providing metadata for a file upload.
 *
 * Since different features (e.g., chat, feed posts) may require different metadata to be sent along
 * with a file, this interface allows for the creation of feature-specific context objects.
 *
 * @see UploadPayload.context
 */
public interface UploadContext
