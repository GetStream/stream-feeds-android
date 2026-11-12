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

import com.squareup.moshi.Types
import io.getstream.feeds.android.network.infrastructure.JsonQuery
import io.getstream.feeds.android.network.infrastructure.QueryConverterFactory
import io.getstream.feeds.android.network.infrastructure.Serializer
import io.getstream.feeds.android.network.models.QueryUsersPayload
import java.lang.reflect.Type
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Converter
import retrofit2.Retrofit

internal class QueryConverterFactoryTest {

    private val factory = QueryConverterFactory(Serializer.moshi)
    private val retrofit = Retrofit.Builder().baseUrl("https://example.com/").build()

    @Test
    fun `on plain query types, return no converter`() {
        assertNull(factory.stringConverter(String::class.java, emptyArray(), retrofit))
        assertNull(factory.stringConverter(Int::class.javaObjectType, emptyArray(), retrofit))
        assertNull(factory.stringConverter(Boolean::class.javaObjectType, emptyArray(), retrofit))
        assertNull(factory.stringConverter(List::class.java, emptyArray(), retrofit))
    }

    @Test
    fun `on JsonQuery of a model, encode the value as one JSON string`() {
        val type = Types.newParameterizedType(JsonQuery::class.java, QueryUsersPayload::class.java)
        val payload = QueryUsersPayload(filterConditions = mapOf("id" to "user-1"), limit = 10)

        val result = convert(type, JsonQuery(payload))

        assertEquals("""{"filter_conditions":{"id":"user-1"},"limit":10}""", result)
    }

    @Test
    fun `on JsonQuery of a list, encode the list as one JSON string`() {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val type = Types.newParameterizedType(JsonQuery::class.java, listType)

        val result = convert(type, JsonQuery(listOf("a", "b")))

        assertEquals("""["a","b"]""", result)
    }

    @Test
    fun `on Date, format it as RFC 3339 in UTC`() {
        val result = convert(Date::class.java, Date(1_700_000_000_123))

        assertEquals("2023-11-14T22:13:20.123Z", result)
    }

    @Suppress("UNCHECKED_CAST")
    private fun convert(type: Type, value: Any): String? {
        val converter =
            factory.stringConverter(type, emptyArray(), retrofit) as Converter<Any, String>
        return converter.convert(value)
    }
}
