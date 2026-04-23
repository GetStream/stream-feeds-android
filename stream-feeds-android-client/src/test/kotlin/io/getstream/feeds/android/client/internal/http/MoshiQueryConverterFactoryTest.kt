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

package io.getstream.feeds.android.client.internal.http

import com.squareup.moshi.Moshi
import io.getstream.feeds.android.network.infrastructure.MoshiQueryConverterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Retrofit

internal class MoshiQueryConverterFactoryTest {

    private val moshi = Moshi.Builder().build()
    private val factory = MoshiQueryConverterFactory(moshi)
    private val retrofit = Retrofit.Builder().baseUrl("https://example.com/").build()

    @Test
    fun `returns null for String type`() {
        assertNull(factory.stringConverter(String::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `returns null for primitive Int type`() {
        assertNull(factory.stringConverter(Int::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `returns null for boxed Integer type`() {
        assertNull(factory.stringConverter(java.lang.Integer::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `returns null for Long type`() {
        assertNull(factory.stringConverter(Long::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `returns null for Boolean type`() {
        assertNull(factory.stringConverter(Boolean::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `returns null for enum type`() {
        assertNull(factory.stringConverter(TestEnum::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `returns converter for Map type`() {
        assertNotNull(factory.stringConverter(Map::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `returns converter for List type`() {
        assertNotNull(factory.stringConverter(List::class.java, emptyArray(), retrofit))
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `converter serializes map to JSON`() {
        val converter =
            factory.stringConverter(Map::class.java, emptyArray(), retrofit)
                as retrofit2.Converter<Any, String>

        val result = converter.convert(mapOf("name" to "hello", "value" to 42))

        assertEquals("{\"name\":\"hello\",\"value\":42}", result)
    }

    private enum class TestEnum {
        A,
        B,
    }
}
