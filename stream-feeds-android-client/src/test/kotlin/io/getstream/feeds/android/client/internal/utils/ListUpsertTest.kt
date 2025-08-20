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

    data class TestUser(val id: String, val name: String, val age: Int)

    // MARK: - Update Existing Element Tests

    @Test
    fun testUpsert_updateExistingElement() {
        val originalList =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob", 30),
                TestUser("3", "Charlie", 35),
            )

        val updatedUser = TestUser("2", "Bob Updated", 31)
        val result = originalList.upsert(updatedUser) { it.id }

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob Updated", result[1].name)
        assertEquals(31, result[1].age)
        assertEquals("Charlie", result[2].name)
    }

    // MARK: - Insert New Element Tests

    @Test
    fun testUpsert_insertNewElement_intoEmptyList() {
        val originalList = emptyList<TestUser>()
        val newUser = TestUser("1", "Alice", 25)
        val result = originalList.upsert(newUser) { it.id }

        assertEquals(1, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("1", result[0].id)
    }

    @Test
    fun testUpsert_insertNewElement_intoNonEmptyList() {
        val originalList = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))

        val newUser = TestUser("3", "Charlie", 35)
        val result = originalList.upsert(newUser) { it.id }

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals("Charlie", result[2].name)
    }

    // MARK: - Immutability Tests

    @Test
    fun testUpsert_originalListUnchanged_onUpdate() {
        val originalList = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))

        val updatedUser = TestUser("1", "Alice Updated", 26)
        val result = originalList.upsert(updatedUser) { it.id }

        // Original list should be unchanged
        assertEquals(2, originalList.size)
        assertEquals("Alice", originalList[0].name)
        assertEquals(25, originalList[0].age)

        // Result should be different
        assertNotSame(originalList, result)
        assertEquals("Alice Updated", result[0].name)
        assertEquals(26, result[0].age)
    }

    @Test
    fun testUpsert_originalListUnchanged_onInsert() {
        val originalList = listOf(TestUser("1", "Alice", 25))
        val newUser = TestUser("2", "Bob", 30)
        val result = originalList.upsert(newUser) { it.id }

        // Original list should be unchanged
        assertEquals(1, originalList.size)
        assertEquals("Alice", originalList[0].name)

        // Result should be different
        assertNotSame(originalList, result)
        assertEquals(2, result.size)
        assertEquals("Bob", result[1].name)
    }

    // MARK: - Edge Cases

    @Test
    fun testUpsert_singleElementList_update() {
        val originalList = listOf(TestUser("1", "Alice", 25))
        val updatedUser = TestUser("1", "Alice Updated", 26)
        val result = originalList.upsert(updatedUser) { it.id }

        assertEquals(1, result.size)
        assertEquals("Alice Updated", result[0].name)
        assertEquals(26, result[0].age)
    }

    @Test
    fun testUpsert_singleElementList_insert() {
        val originalList = listOf(TestUser("1", "Alice", 25))
        val newUser = TestUser("2", "Bob", 30)
        val result = originalList.upsert(newUser) { it.id }

        assertEquals(2, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
    }

    @Test
    fun testUpsert_duplicateConsecutiveOperations() {
        val originalList = listOf(TestUser("1", "Alice", 25))

        // Update the same element twice
        var result = originalList.upsert(TestUser("1", "Alice Updated", 26)) { it.id }
        result = result.upsert(TestUser("1", "Alice Final", 27)) { it.id }

        assertEquals(1, result.size)
        assertEquals("Alice Final", result[0].name)
        assertEquals(27, result[0].age)
    }

    // MARK: - Order Preservation Tests

    @Test
    fun testUpsert_preservesOriginalOrder_onUpdate() {
        val originalList =
            listOf(
                TestUser("1", "Alice", 25),
                TestUser("2", "Bob", 30),
                TestUser("3", "Charlie", 35),
                TestUser("4", "David", 40),
            )

        val updatedUser = TestUser("2", "Bob Updated", 31)
        val result = originalList.upsert(updatedUser) { it.id }

        assertEquals(4, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob Updated", result[1].name)
        assertEquals("Charlie", result[2].name)
        assertEquals("David", result[3].name)
    }

    @Test
    fun testUpsert_appendsToEnd_onInsert() {
        val originalList = listOf(TestUser("1", "Alice", 25), TestUser("2", "Bob", 30))

        val newUser = TestUser("3", "Charlie", 35)
        val result = originalList.upsert(newUser) { it.id }

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals("Charlie", result[2].name) // Added at the end
    }
}
