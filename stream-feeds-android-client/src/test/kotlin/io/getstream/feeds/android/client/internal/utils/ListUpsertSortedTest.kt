package io.getstream.feeds.android.client.internal.utils

import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

internal class ListUpsertSortedTest {

    data class TestUser(
        val id: String,
        val name: String,
        val age: Int,
        val score: Double
    )

    // MARK: - Update Existing Element Tests (Comparator version)

    @Test
    fun testUpsertSorted_updateExistingElement_withRepositioning() {
        // Original list sorted by name
        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 30, 90.0),
            TestUser("3", "Charlie", 35, 80.0)
        )

        // Update Bob's name to "Zoe" - should move to end
        val updatedUser = TestUser("2", "Zoe", 31, 91.0)
        val nameComparator = compareBy<TestUser> { it.name }
        val result = originalList.upsertSorted(updatedUser, { it.id }, nameComparator)

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Charlie", result[1].name)
        assertEquals("Zoe", result[2].name)
        assertEquals("2", result[2].id) // Same ID, different position
    }

    @Test
    fun testUpsertSorted_updateExistingElement_samePosition() {
        // Original list sorted by age
        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 30, 90.0),
            TestUser("3", "Charlie", 35, 80.0)
        )

        // Update Bob's score but keep same age (position shouldn't change)
        val updatedUser = TestUser("2", "Bob", 30, 95.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(updatedUser, { it.id }, ageComparator)

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals(95.0, result[1].score, 0.01)
        assertEquals("Charlie", result[2].name)
    }

    @Test
    fun testUpsertSorted_updateExistingElement_moveToStart() {
        // Original list sorted by age
        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 30, 90.0),
            TestUser("3", "Charlie", 35, 80.0)
        )

        // Update Charlie to be youngest
        val updatedUser = TestUser("3", "Charlie", 20, 80.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(updatedUser, { it.id }, ageComparator)

        assertEquals(3, result.size)
        assertEquals("Charlie", result[0].name)
        assertEquals(20, result[0].age)
        assertEquals("Alice", result[1].name)
        assertEquals("Bob", result[2].name)
    }

    // MARK: - Insert New Element Tests (Comparator version)

    @Test
    fun testUpsertSorted_insertNewElement_atStart() {
        val originalList = listOf(
            TestUser("2", "Bob", 30, 90.0),
            TestUser("3", "Charlie", 35, 80.0)
        )

        val newUser = TestUser("1", "Alice", 25, 85.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(newUser, { it.id }, ageComparator)

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[0].age)
        assertEquals("Bob", result[1].name)
        assertEquals("Charlie", result[2].name)
    }

    @Test
    fun testUpsertSorted_insertNewElement_atMiddle() {
        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("3", "Charlie", 35, 80.0)
        )

        val newUser = TestUser("2", "Bob", 30, 90.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(newUser, { it.id }, ageComparator)

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals(30, result[1].age)
        assertEquals("Charlie", result[2].name)
    }

    @Test
    fun testUpsertSorted_insertNewElement_atEnd() {
        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 30, 90.0)
        )

        val newUser = TestUser("3", "Charlie", 35, 80.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(newUser, { it.id }, ageComparator)

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals("Charlie", result[2].name)
        assertEquals(35, result[2].age)
    }

    @Test
    fun testUpsertSorted_insertIntoEmptyList() {
        val originalList = emptyList<TestUser>()
        val newUser = TestUser("1", "Alice", 25, 85.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(newUser, { it.id }, ageComparator)

        assertEquals(1, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("1", result[0].id)
    }

    // MARK: - Sort Configuration Tests

    @Test
    fun testUpsertSorted_singleSort_updateWithRepositioning() {
        val nameField = SortField.create<TestUser, String>("name") { it.name }
        val sort = listOf(Sort(nameField, SortDirection.FORWARD))

        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 30, 90.0),
            TestUser("3", "Charlie", 35, 80.0)
        )

        // Update Bob to Zoe - should move to end
        val updatedUser = TestUser("2", "Zoe", 31, 91.0)
        val result = originalList.upsertSorted(updatedUser, { it.id }, sort)

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Charlie", result[1].name)
        assertEquals("Zoe", result[2].name)
    }

    @Test
    fun testUpsertSorted_singleSort_insertNewElement() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age }
        val sort = listOf(Sort(ageField, SortDirection.FORWARD))

        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("3", "Charlie", 35, 80.0)
        )

        val newUser = TestUser("2", "Bob", 30, 90.0)
        val result = originalList.upsertSorted(newUser, { it.id }, sort)

        assertEquals(3, result.size)
        assertEquals(25, result[0].age)
        assertEquals(30, result[1].age)
        assertEquals(35, result[2].age)
    }

    @Test
    fun testUpsertSorted_multipleSort_primarySecondary() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age }
        val nameField = SortField.create<TestUser, String>("name") { it.name }
        val sort = listOf(
            Sort(ageField, SortDirection.FORWARD),
            Sort(nameField, SortDirection.FORWARD)
        )

        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 25, 90.0), // Same age as Alice
            TestUser("3", "Charlie", 30, 80.0)
        )

        // Insert someone with same age as Alice/Bob but different name
        val newUser = TestUser("4", "Anna", 25, 88.0)
        val result = originalList.upsertSorted(newUser, { it.id }, sort)

        assertEquals(4, result.size)
        assertEquals("Alice", result[0].name) // Age 25, name Alice
        assertEquals("Anna", result[1].name)  // Age 25, name Anna
        assertEquals("Bob", result[2].name)   // Age 25, name Bob
        assertEquals("Charlie", result[3].name) // Age 30
    }

    @Test
    fun testUpsertSorted_reverseSort() {
        val scoreField = SortField.create<TestUser, Double>("score") { it.score }
        val sort = listOf(Sort(scoreField, SortDirection.REVERSE))

        val originalList = listOf(
            TestUser("1", "Alice", 25, 95.0),
            TestUser("2", "Bob", 30, 85.0),
            TestUser("3", "Charlie", 35, 75.0)
        )

        // Insert user with score that should go in middle
        val newUser = TestUser("4", "David", 28, 90.0)
        val result = originalList.upsertSorted(newUser, { it.id }, sort)

        assertEquals(4, result.size)
        assertEquals(95.0, result[0].score, 0.01) // Alice
        assertEquals(90.0, result[1].score, 0.01) // David
        assertEquals(85.0, result[2].score, 0.01) // Bob
        assertEquals(75.0, result[3].score, 0.01) // Charlie
    }

    // MARK: - Edge Cases

    @Test
    fun testUpsertSorted_singleElementList_update() {
        val originalList = listOf(TestUser("1", "Alice", 25, 85.0))
        val updatedUser = TestUser("1", "Alice Updated", 26, 86.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(updatedUser, { it.id }, ageComparator)

        assertEquals(1, result.size)
        assertEquals("Alice Updated", result[0].name)
        assertEquals(26, result[0].age)
    }

    @Test
    fun testUpsertSorted_singleElementList_insert() {
        val originalList = listOf(TestUser("1", "Alice", 30, 85.0))
        val newUser = TestUser("2", "Bob", 25, 90.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(newUser, { it.id }, ageComparator)

        assertEquals(2, result.size)
        assertEquals("Bob", result[0].name)
        assertEquals(25, result[0].age)
        assertEquals("Alice", result[1].name)
        assertEquals(30, result[1].age)
    }

    @Test
    fun testUpsertSorted_duplicateValues_maintainStableSort() {
        // TODO: Fix this test - upsertSorted switches the order of Alice and Bob
        val ageField = SortField.create<TestUser, Int>("age") { it.age }
        val sort = listOf(Sort(ageField, SortDirection.FORWARD))

        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 25, 90.0),
            TestUser("3", "Charlie", 30, 80.0)
        )

        // Update Alice with same age
        val updatedUser = TestUser("1", "Alice Updated", 25, 87.0)
        val result = originalList.upsertSorted(updatedUser, { it.id }, sort)

        assertEquals(3, result.size)
        assertEquals("Alice Updated", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals("Charlie", result[2].name)
    }

    // MARK: - Immutability Tests

    @Test
    fun testUpsertSorted_originalListUnchanged_onUpdate() {
        val originalList = listOf(
            TestUser("1", "Alice", 25, 85.0),
            TestUser("2", "Bob", 30, 90.0)
        )

        val updatedUser = TestUser("1", "Alice Updated", 20, 87.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(updatedUser, { it.id }, ageComparator)

        // Original list should be unchanged
        assertEquals(2, originalList.size)
        assertEquals("Alice", originalList[0].name)
        assertEquals(25, originalList[0].age)

        // Result should be different
        assertNotSame(originalList, result)
        assertEquals("Alice Updated", result[0].name)
        assertEquals(20, result[0].age)
    }

    @Test
    fun testUpsertSorted_originalListUnchanged_onInsert() {
        val originalList = listOf(TestUser("1", "Alice", 30, 85.0))
        val newUser = TestUser("2", "Bob", 25, 90.0)
        val ageComparator = compareBy<TestUser> { it.age }
        val result = originalList.upsertSorted(newUser, { it.id }, ageComparator)

        // Original list should be unchanged
        assertEquals(1, originalList.size)
        assertEquals("Alice", originalList[0].name)

        // Result should be different
        assertNotSame(originalList, result)
        assertEquals(2, result.size)
        assertEquals("Bob", result[0].name)
        assertEquals("Alice", result[1].name)
    }

    // MARK: - Complex Scenarios

    @Test
    fun testUpsertSorted_consecutiveOperations() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age }
        val sort = listOf(Sort(ageField, SortDirection.FORWARD))

        var result = listOf(TestUser("2", "Bob", 30, 90.0))

        // Insert younger user
        result = result.upsertSorted(TestUser("1", "Alice", 25, 85.0), { it.id }, sort)
        
        // Insert older user
        result = result.upsertSorted(TestUser("3", "Charlie", 35, 80.0), { it.id }, sort)
        
        // Update middle user to be oldest
        result = result.upsertSorted(TestUser("2", "Bob Updated", 40, 91.0), { it.id }, sort)

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals(25, result[0].age)
        assertEquals("Charlie", result[1].name)
        assertEquals(35, result[1].age)
        assertEquals("Bob Updated", result[2].name)
        assertEquals(40, result[2].age)
    }
} 