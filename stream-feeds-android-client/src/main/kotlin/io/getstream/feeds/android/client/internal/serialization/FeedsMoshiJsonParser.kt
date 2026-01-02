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

package io.getstream.feeds.android.client.internal.serialization

import com.squareup.moshi.Moshi
import io.getstream.android.core.api.serialization.StreamJsonSerialization

/**
 * A JSON parser implementation using Moshi.
 *
 * @param moshi The Moshi instance to use for parsing.
 */
internal class FeedsMoshiJsonParser(private val moshi: Moshi) : StreamJsonSerialization {

    override fun toJson(any: Any): Result<String> = runCatching {
        moshi.adapter(any.javaClass).toJson(any)
    }

    override fun <T : Any> fromJson(raw: String, clazz: Class<T>): Result<T> = runCatching {
        moshi.adapter(clazz).fromJson(raw)
            ?: throw IllegalArgumentException("Failed to parse $clazz from raw string: $raw")
    }
}
