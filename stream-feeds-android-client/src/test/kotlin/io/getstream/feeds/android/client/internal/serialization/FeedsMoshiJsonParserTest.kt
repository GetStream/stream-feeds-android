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
package io.getstream.feeds.android.client.internal.serialization

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FeedsMoshiJsonParserTest {
    private val adapter: JsonAdapter<Any> = mockk()
    private val moshi: Moshi = mockk { every { adapter<Any>(any()) } returns adapter }

    private val parser = FeedsMoshiJsonParser(moshi)

    @Test
    fun `toJson when successful, return json string`() {
        val testObject = TestData("test", 123)
        val expectedJson = """{"name":"test","value":123}"""

        every { adapter.toJson(testObject) } returns expectedJson

        val result = parser.toJson(testObject)

        assertEquals(expectedJson, result.getOrNull())
    }

    @Test
    fun `toJson when adapter throws exception, return failure`() {
        val testObject = TestData("test", 123)
        val exception = RuntimeException("Serialization failed")

        every { adapter.toJson(testObject) } throws exception

        val result = parser.toJson(testObject)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `fromJson when successful, return parsed object`() {
        val json = """{"name":"test","value":123}"""
        val expectedObject = TestData("test", 123)
        every { adapter.fromJson(json) } returns expectedObject

        val result = parser.fromJson(json, TestData::class.java)

        assertEquals(expectedObject, result.getOrNull())
    }

    @Test
    fun `fromJson when adapter returns null, return failure with IllegalArgumentException`() {
        val json = """{"invalid":"json"}"""
        every { adapter.fromJson(json) } returns null

        val result = parser.fromJson(json, TestData::class.java)

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `fromJson when adapter throws exception, return failure`() {
        val json = """{"malformed":json}"""
        val exception = RuntimeException("Parsing failed")
        every { adapter.fromJson(json) } throws exception

        val result = parser.fromJson(json, TestData::class.java)

        assertEquals(exception, result.exceptionOrNull())
    }

    private data class TestData(val name: String, val value: Int)
}
