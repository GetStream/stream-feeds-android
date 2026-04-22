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

package io.getstream.feeds.android.network.infrastructure

import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * A Retrofit [Converter.Factory] that JSON-serializes complex objects used as `@Query` parameters.
 *
 * Retrofit's default behavior for `@Query` is to call `toString()` on the value, which produces a
 * Kotlin data class string representation instead of JSON. This factory intercepts non-primitive
 * types and serializes them to JSON via Moshi.
 */
public class MoshiQueryConverterFactory(private val moshi: Moshi) : Converter.Factory() {

    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<*, String>? {
        if (
            type == String::class.java ||
                type == Int::class.java ||
                type == Long::class.java ||
                type == Float::class.java ||
                type == Double::class.java ||
                type == Boolean::class.java ||
                type == java.lang.Integer::class.java ||
                type == java.lang.Long::class.java ||
                type == java.lang.Float::class.java ||
                type == java.lang.Double::class.java ||
                type == java.lang.Boolean::class.java ||
                (type is Class<*> && type.isEnum)
        ) {
            return null
        }
        return MoshiQueryConverter(moshi, type)
    }

    private class MoshiQueryConverter(private val moshi: Moshi, private val type: Type) :
        Converter<Any, String> {
        override fun convert(value: Any): String {
            return moshi.adapter<Any>(type).toJson(value)
        }
    }
}
