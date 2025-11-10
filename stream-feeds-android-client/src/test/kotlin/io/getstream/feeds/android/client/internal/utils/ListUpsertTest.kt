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
package io.getstream.feeds.android.client.internal.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

internal class ListUpsertTest {

    @Test
    fun `upsert updates existing element in place`() {
        val originalList =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob", 30),
                TestUser("3", "Charlie", 35),
            )

        val updatedUser = TestUser("2", "Bob Updated", 31)
        val result = originalList.upsert(updatedUser, TestUser::id)

        val expected =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob Updated", 31),
                TestUser("3", "Charlie", 35),
            )
        assertEquals(expected, result)
    }

    @Test
    fun `upsert inserts new element into empty list`() {
        val originalList = emptyList<TestUser>()
        val newUser = TestUser("1", "Alice", 25)
        val result = originalList.upsert(newUser, TestUser::id)

        val expected = listOf(TestUser("1", "Alice", 25))
        assertEquals(expected, result)
    }

    @Test
    fun `upsert appends new element to non-empty list`() {
        val originalList = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))

        val newUser = TestUser("3", "Charlie", 35)
        val result = originalList.upsert(newUser, TestUser::id)

        val expected =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob", 30),
                TestUser("3", "Charlie", 35),
            )
        assertEquals(expected, result)
    }

    @Test
    fun `upsert leaves original list unchanged on update`() {
        val originalList = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))

        val updatedUser = TestUser("1", "Alice Updated", 26)
        val result = originalList.upsert(updatedUser, TestUser::id)

        assertNotSame(originalList, result)
        val expectedOriginal = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))
        assertEquals(expectedOriginal, originalList)

        val expectedResult = listOf(TestUser("1", "Alice Updated", 26), TestUser("2", "Bob", 30))
        assertEquals(expectedResult, result)
    }

    @Test
    fun `upsert leaves original list unchanged on insert`() {
        val originalList = listOf(TestUser("1", "Alice", 25))
        val newUser = TestUser("2", "Bob", 30)
        val result = originalList.upsert(newUser, TestUser::id)

        assertNotSame(originalList, result)
        val expectedOriginal = listOf(TestUser("1", "Alice", 25))
        assertEquals(expectedOriginal, originalList)

        val expectedResult = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))
        assertEquals(expectedResult, result)
    }

    @Test
    fun `upsert updates single element in single-element list`() {
        val originalList = listOf(TestUser("1", "Alice", 25))
        val updatedUser = TestUser("1", "Alice Updated", 26)
        val result = originalList.upsert(updatedUser, TestUser::id)

        val expected = listOf(TestUser("1", "Alice Updated", 26))
        assertEquals(expected, result)
    }

    @Test
    fun `upsert appends to single-element list`() {
        val originalList = listOf(TestUser("1", "Alice", 25))
        val newUser = TestUser("2", "Bob", 30)
        val result = originalList.upsert(newUser, TestUser::id)

        val expected = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))
        assertEquals(expected, result)
    }

    @Test
    fun `upsert handles consecutive operations`() {
        val originalList = listOf(TestUser("1", "Alice", 25))

        var result = originalList.upsert(TestUser("1", "Alice Updated", 26), TestUser::id)
        result = result.upsert(TestUser("1", "Alice Final", 27), TestUser::id)

        val expected = listOf(TestUser("1", "Alice Final", 27))
        assertEquals(expected, result)
    }

    @Test
    fun `upsert preserves original order when updating`() {
        val originalList =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob", 30),
                TestUser("3", "Charlie", 35),
                TestUser("4", "David", 40),
            )

        val updatedUser = TestUser("2", "Bob Updated", 31)
        val result = originalList.upsert(updatedUser, TestUser::id)

        val expected =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob Updated", 31),
                TestUser("3", "Charlie", 35),
                TestUser("4", "David", 40),
            )
        assertEquals(expected, result)
    }

    @Test
    fun `upsert appends new element to end of list`() {
        val originalList = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))

        val newUser = TestUser("3", "Charlie", 35)
        val result = originalList.upsert(newUser, TestUser::id)

        val expected =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob", 30),
                TestUser("3", "Charlie", 35),
            )
        assertEquals(expected, result)
    }

    @Test
    fun `upsertAll updates matching elements in place and appends new ones`() {
        val originalList =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob", 30),
                TestUser("3", "Charlie", 35),
            )

        val updates = listOf(TestUser("2", "Bob Updated", 31), TestUser("4", "David", 40))
        val result = originalList.upsertAll(updates, TestUser::id)

        val expected =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob Updated", 31),
                TestUser("3", "Charlie", 35),
                TestUser("4", "David", 40),
            )
        assertEquals(expected, result)
    }

    data class TestUser(val id: String, val name: String, val age: Int)
}
