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

import io.getstream.android.core.api.serialization.StreamJsonSerialization
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FeedsEventParserTest {
    private val jsonParser: StreamJsonSerialization = mockk()

    private val parser = FeedsEventParser(jsonParser)

    @Test
    fun `serialize when called, delegate to json parser`() {
        val expectedJson = """{"type":"activity.new","data":{}}"""
        val expectedResult = Result.success(expectedJson)

        every { jsonParser.toJson(testEvent) } returns expectedResult

        val result = parser.serialize(testEvent)

        assertEquals(expectedResult, result)
        verify { jsonParser.toJson(testEvent) }
    }

    @Test
    fun `serialize when json parser fails, return failure`() {
        val exception = RuntimeException("Serialization failed")
        val expectedResult = Result.failure<String>(exception)

        every { jsonParser.toJson(testEvent) } returns expectedResult

        val result = parser.serialize(testEvent)

        assertEquals(expectedResult, result)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `deserialize when called, delegate to json parser`() {
        val json = """{"type":"activity.new","data":{}}"""
        val expectedResult = Result.success(testEvent)

        every { jsonParser.fromJson(json, WSEvent::class.java) } returns expectedResult

        val result = parser.deserialize(json)

        assertEquals(expectedResult, result)
        verify { jsonParser.fromJson(json, WSEvent::class.java) }
    }

    @Test
    fun `deserialize when json parser fails, return failure`() {
        val json = """{"invalid":"json"}"""
        val exception = RuntimeException("Parsing failed")
        val expectedResult = Result.failure<WSEvent>(exception)

        every { jsonParser.fromJson(json, WSEvent::class.java) } returns expectedResult

        val result = parser.deserialize(json)

        assertEquals(expectedResult, result)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    companion object {
        private val testEvent =
            object : WSEvent {
                override fun getWSEventType(): String = "test.event"
            }
    }
}
