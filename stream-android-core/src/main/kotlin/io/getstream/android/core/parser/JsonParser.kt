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
package io.getstream.android.core.parser

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi

/** A general Json parser. */
@StreamInternalApi
public interface JsonParser {

    /**
     * Converts an object to its JSON representation.
     *
     * @param any The object to convert.
     * @return The JSON string representation of the object.
     */
    public fun toJson(any: Any): String

    /**
     * Converts a JSON string to an object of the specified class.
     *
     * @param raw The JSON string to convert.
     * @param clazz The class of the object to convert to.
     * @return The object of type [T] parsed from the JSON string.
     */
    public fun <T : Any> fromJson(raw: String, clazz: Class<T>): T

    /**
     * Converts a JSON string to an object of the specified class, returning a [Result].
     *
     * @param raw The JSON string to convert.
     * @param clazz The class of the object to convert to.
     * @return A [Result] containing the parsed object or an error if parsing fails.
     */
    public fun <T : Any> fromJsonOrError(raw: String, clazz: Class<T>): Result<T> {
        return try {
            val result = fromJson(raw, clazz)
            Result.success(result)
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }
}
