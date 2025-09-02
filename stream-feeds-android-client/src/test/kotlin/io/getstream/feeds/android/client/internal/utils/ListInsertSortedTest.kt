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

import io.getstream.android.core.api.filter.Sort
import io.getstream.android.core.api.filter.SortDirection
import io.getstream.android.core.api.filter.SortField
import org.junit.Assert.assertEquals
import org.junit.Test

internal class InsertSortedTest {

    data class TestUser(val name: String?, val age: Int?, val score: Double?)

    // MARK: - Single Sort Parameter Tests

    @Test
    fun testInsertSorted_singleSort_insertAtStart() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = listOf(Sort(nameField, SortDirection.FORWARD))

        val users =
            mutableListOf(
                TestUser("Bob", 30, 85.0),
                TestUser("Charlie", 25, 90.0),
                TestUser("David", 35, 80.0),
            )

        val result = users.insertSorted(TestUser("Alice", 28, 88.0), sort)

        // Alice should be inserted at the start (alphabetically first)
        assertEquals(4, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals("Charlie", result[2].name)
        assertEquals("David", result[3].name)
    }

    @Test
    fun testInsertSorted_singleSort_insertAtMiddle() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val sort = listOf(Sort(ageField, SortDirection.FORWARD))

        val users =
            mutableListOf(
                TestUser("Alice", 20, 85.0),
                TestUser("Bob", 25, 90.0),
                TestUser("David", 35, 80.0),
            )

        val result = users.insertSorted(TestUser("Charlie", 30, 88.0), sort)

        // Charlie (age 30) should be inserted between Bob (25) and David (35)
        assertEquals(4, result.size)
        assertEquals(20, result[0].age)
        assertEquals(25, result[1].age)
        assertEquals(30, result[2].age) // Charlie inserted here
        assertEquals("Charlie", result[2].name)
        assertEquals(35, result[3].age)
    }

    @Test
    fun testInsertSorted_singleSort_insertAtEnd() {
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val sort = listOf(Sort(scoreField, SortDirection.FORWARD))

        val users =
            mutableListOf(
                TestUser("Alice", 25, 80.0),
                TestUser("Bob", 30, 85.0),
                TestUser("Charlie", 28, 90.0),
            )

        val result = users.insertSorted(TestUser("David", 35, 95.0), sort)

        // David (score 95.0) should be inserted at the end (highest score)
        assertEquals(4, result.size)
        assertEquals(80.0, result[0].score)
        assertEquals(85.0, result[1].score)
        assertEquals(90.0, result[2].score)
        assertEquals(95.0, result[3].score) // David inserted here
        assertEquals("David", result[3].name)
    }

    @Test
    fun testInsertSorted_singleSort_reverseDirection() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val sort = listOf(Sort(ageField, SortDirection.REVERSE))

        val users =
            mutableListOf(
                TestUser("David", 35, 80.0),
                TestUser("Charlie", 30, 88.0),
                TestUser("Bob", 25, 90.0),
            )

        val result = users.insertSorted(TestUser("Alice", 40, 85.0), sort)

        // Alice (age 40) should be inserted at the start (highest age in reverse order)
        assertEquals(4, result.size)
        assertEquals(40, result[0].age) // Alice inserted here
        assertEquals(35, result[1].age)
        assertEquals(30, result[2].age)
        assertEquals(25, result[3].age)
        assertEquals("Alice", result[0].name)
    }

    // MARK: - Two Sort Parameters Tests

    @Test
    fun testInsertSorted_twoSorts_insertAtStart() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sorts =
            listOf(Sort(ageField, SortDirection.FORWARD), Sort(nameField, SortDirection.FORWARD))

        val users =
            mutableListOf(
                TestUser("Bob", 25, 85.0),
                TestUser("Charlie", 25, 90.0),
                TestUser("David", 30, 80.0),
            )

        val result = users.insertSorted(TestUser("Alice", 25, 88.0), sorts)

        // Alice (age 25, name Alice) should be inserted at start (same age as Bob/Charlie, but name
        // comes first)
        assertEquals(4, result.size)
        assertEquals(25, result[0].age)
        assertEquals("Alice", result[0].name) // Alice inserted here
        assertEquals(25, result[1].age)
        assertEquals("Bob", result[1].name)
        assertEquals(25, result[2].age)
        assertEquals("Charlie", result[2].name)
        assertEquals(30, result[3].age)
        assertEquals("David", result[3].name)
    }

    @Test
    fun testInsertSorted_twoSorts_insertAtMiddle() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val sorts =
            listOf(Sort(ageField, SortDirection.FORWARD), Sort(scoreField, SortDirection.FORWARD))

        val users =
            mutableListOf(
                TestUser("Alice", 25, 80.0),
                TestUser("Bob", 25, 90.0),
                TestUser("David", 30, 85.0),
            )

        val result = users.insertSorted(TestUser("Charlie", 25, 85.0), sorts)

        // Charlie (age 25, score 85.0) should be inserted between Alice (80.0) and Bob (90.0)
        assertEquals(4, result.size)
        assertEquals(25, result[0].age)
        assertEquals(80.0, result[0].score)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[1].age)
        assertEquals(85.0, result[1].score)
        assertEquals("Charlie", result[1].name) // Charlie inserted here
        assertEquals(25, result[2].age)
        assertEquals(90.0, result[2].score)
        assertEquals("Bob", result[2].name)
        assertEquals(30, result[3].age)
        assertEquals("David", result[3].name)
    }

    @Test
    fun testInsertSorted_twoSorts_insertAtEnd() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sorts =
            listOf(Sort(ageField, SortDirection.FORWARD), Sort(nameField, SortDirection.FORWARD))

        val users =
            mutableListOf(
                TestUser("Alice", 25, 80.0),
                TestUser("Bob", 25, 85.0),
                TestUser("Charlie", 30, 90.0),
            )

        val result = users.insertSorted(TestUser("David", 30, 95.0), sorts)

        // David (age 30, name David) should be inserted at end (same age as Charlie, but name comes
        // after)
        assertEquals(4, result.size)
        assertEquals(25, result[0].age)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[1].age)
        assertEquals("Bob", result[1].name)
        assertEquals(30, result[2].age)
        assertEquals("Charlie", result[2].name)
        assertEquals(30, result[3].age)
        assertEquals("David", result[3].name) // David inserted here
    }

    @Test
    fun testInsertSorted_twoSorts_differentDirections() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val sorts =
            listOf(
                Sort(ageField, SortDirection.FORWARD), // Age ascending
                Sort(scoreField, SortDirection.REVERSE), // Score descending
            )

        val users =
            mutableListOf(
                TestUser("Alice", 25, 90.0),
                TestUser("Bob", 25, 80.0),
                TestUser("David", 30, 85.0),
            )

        val result = users.insertSorted(TestUser("Charlie", 25, 85.0), sorts)

        // Charlie (age 25, score 85.0) should be inserted between Alice (90.0) and Bob (80.0)
        assertEquals(4, result.size)
        assertEquals(25, result[0].age)
        assertEquals(90.0, result[0].score)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[1].age)
        assertEquals(85.0, result[1].score)
        assertEquals("Charlie", result[1].name) // Charlie inserted here
        assertEquals(25, result[2].age)
        assertEquals(80.0, result[2].score)
        assertEquals("Bob", result[2].name)
        assertEquals(30, result[3].age)
        assertEquals("David", result[3].name)
    }

    // MARK: - Three Sort Parameters Tests

    @Test
    fun testInsertSorted_threeSorts_insertAtStart() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sorts =
            listOf(
                Sort(ageField, SortDirection.FORWARD),
                Sort(scoreField, SortDirection.FORWARD),
                Sort(nameField, SortDirection.FORWARD),
            )

        val users =
            mutableListOf(
                TestUser("Bob", 25, 85.0),
                TestUser("Charlie", 25, 85.0),
                TestUser("David", 30, 90.0),
            )

        val result = users.insertSorted(TestUser("Alice", 25, 85.0), sorts)

        // Alice (age 25, score 85.0, name Alice) should be inserted at start (same age/score as
        // Bob/Charlie, but name comes first)
        assertEquals(4, result.size)
        assertEquals(25, result[0].age)
        assertEquals(85.0, result[0].score)
        assertEquals("Alice", result[0].name) // Alice inserted here
        assertEquals(25, result[1].age)
        assertEquals(85.0, result[1].score)
        assertEquals("Bob", result[1].name)
        assertEquals(25, result[2].age)
        assertEquals(85.0, result[2].score)
        assertEquals("Charlie", result[2].name)
        assertEquals(30, result[3].age)
        assertEquals("David", result[3].name)
    }

    @Test
    fun testInsertSorted_threeSorts_insertAtMiddle() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sorts =
            listOf(
                Sort(ageField, SortDirection.FORWARD),
                Sort(scoreField, SortDirection.FORWARD),
                Sort(nameField, SortDirection.FORWARD),
            )

        val users =
            mutableListOf(
                TestUser("Alice", 25, 80.0),
                TestUser("Bob", 25, 85.0),
                TestUser("David", 25, 90.0),
                TestUser("Eve", 30, 85.0),
            )

        val result = users.insertSorted(TestUser("Charlie", 25, 85.0), sorts)

        // Charlie (age 25, score 85.0, name Charlie) should be inserted between Bob and David
        assertEquals(5, result.size)
        assertEquals(25, result[0].age)
        assertEquals(80.0, result[0].score)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[1].age)
        assertEquals(85.0, result[1].score)
        assertEquals("Bob", result[1].name)
        assertEquals(25, result[2].age)
        assertEquals(85.0, result[2].score)
        assertEquals("Charlie", result[2].name) // Charlie inserted here
        assertEquals(25, result[3].age)
        assertEquals(90.0, result[3].score)
        assertEquals("David", result[3].name)
        assertEquals(30, result[4].age)
        assertEquals("Eve", result[4].name)
    }

    @Test
    fun testInsertSorted_threeSorts_insertAtEnd() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sorts =
            listOf(
                Sort(ageField, SortDirection.FORWARD),
                Sort(scoreField, SortDirection.FORWARD),
                Sort(nameField, SortDirection.FORWARD),
            )

        val users =
            mutableListOf(
                TestUser("Alice", 25, 80.0),
                TestUser("Bob", 30, 85.0),
                TestUser("Charlie", 30, 90.0),
            )

        val result = users.insertSorted(TestUser("David", 30, 90.0), sorts)

        // David (age 30, score 90.0, name David) should be inserted at end (same age/score as
        // Charlie, but name comes after)
        assertEquals(4, result.size)
        assertEquals(25, result[0].age)
        assertEquals("Alice", result[0].name)
        assertEquals(30, result[1].age)
        assertEquals(85.0, result[1].score)
        assertEquals("Bob", result[1].name)
        assertEquals(30, result[2].age)
        assertEquals(90.0, result[2].score)
        assertEquals("Charlie", result[2].name)
        assertEquals(30, result[3].age)
        assertEquals(90.0, result[3].score)
        assertEquals("David", result[3].name) // David inserted here
    }

    @Test
    fun testInsertSorted_threeSorts_mixedDirections() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sorts =
            listOf(
                Sort(ageField, SortDirection.FORWARD), // Age ascending
                Sort(scoreField, SortDirection.REVERSE), // Score descending
                Sort(nameField, SortDirection.FORWARD), // Name ascending
            )

        val users =
            mutableListOf(
                TestUser("Alice", 25, 90.0),
                TestUser("Charlie", 25, 85.0),
                TestUser("Bob", 30, 95.0),
            )

        val result = users.insertSorted(TestUser("David", 25, 85.0), sorts)

        // David (age 25, score 85.0, name David) should be inserted after Charlie (same age/score,
        // but name comes after)
        assertEquals(4, result.size)
        assertEquals(25, result[0].age)
        assertEquals(90.0, result[0].score)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[1].age)
        assertEquals(85.0, result[1].score)
        assertEquals("Charlie", result[1].name)
        assertEquals(25, result[2].age)
        assertEquals(85.0, result[2].score)
        assertEquals("David", result[2].name) // David inserted here
        assertEquals(30, result[3].age)
        assertEquals("Bob", result[3].name)
    }

    // MARK: - Edge Cases

    @Test
    fun testInsertSorted_emptyList() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = listOf(Sort(nameField, SortDirection.FORWARD))

        val users = mutableListOf<TestUser>()

        val result = users.insertSorted(TestUser("Alice", 25, 85.0), sort)

        assertEquals(1, result.size)
        assertEquals("Alice", result[0].name)
    }

    @Test
    fun testInsertSorted_singleItemList() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val sort = listOf(Sort(ageField, SortDirection.FORWARD))

        val users = mutableListOf(TestUser("Bob", 30, 85.0))

        val result = users.insertSorted(TestUser("Alice", 25, 90.0), sort)

        assertEquals(2, result.size)
        assertEquals(25, result[0].age) // Alice should come first
        assertEquals("Alice", result[0].name)
        assertEquals(30, result[1].age)
        assertEquals("Bob", result[1].name)
    }

    @Test
    fun testInsertSorted_duplicateValues() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val sort = listOf(Sort(ageField, SortDirection.FORWARD))

        val users = mutableListOf(TestUser("Alice", 20, 85.0), TestUser("Bob", 25, 90.0))

        val result = users.insertSorted(TestUser("Charlie", 20, 88.0), sort)

        // Charlie should be inserted after existing age 25 users (binary search behavior)
        assertEquals(3, result.size)
        assertEquals(20, result[0].age)
        assertEquals("Alice", result[0].name)
        assertEquals(20, result[1].age)
        assertEquals("Charlie", result[1].name) // Charlie inserted after Alice (both age = 20)
        assertEquals(25, result[2].age)
        assertEquals("Bob", result[2].name)
    }

    @Test
    fun testInsertSorted_nullValues() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = listOf(Sort(nameField, SortDirection.FORWARD))

        val users = mutableListOf(TestUser(null, 25, 85.0), TestUser("Bob", 30, 90.0))

        val result = users.insertSorted(TestUser("Alice", 28, 88.0), sort)

        assertEquals(3, result.size)
        assertEquals(null, result[0].name) // Null (empty string) comes first
        assertEquals("Alice", result[1].name) // Alice inserted here
        assertEquals("Bob", result[2].name)
    }
}
