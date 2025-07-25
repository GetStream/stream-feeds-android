package io.getstream.android.core.query

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(StreamInternalApi::class)
internal class SortTest {

    // Test data classes
    data class TestUser(
        val name: String?,
        val age: Int?,
        val score: Double?
    )

    @Test
    fun testSortDirection_values() {
        assertEquals(1, SortDirection.FORWARD.value)
        assertEquals(-1, SortDirection.REVERSE.value)
    }

    @Test
    fun testSortField_create_withStringField() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }

        assertEquals("name", nameField.remote)

        // Test the comparator directly
        val user1 = TestUser("Alice", 25, 85.5)
        val user2 = TestUser("Bob", 30, 92.0)

        val result = nameField.comparator.compare(user1, user2, SortDirection.FORWARD)
        assertTrue("Alice should come before Bob", result < 0)
    }

    @Test
    fun testSortField_create_withIntField() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }

        assertEquals("age", ageField.remote)

        val user1 = TestUser("Alice", 25, 85.5)
        val user2 = TestUser("Bob", 30, 92.0)

        val result = ageField.comparator.compare(user1, user2, SortDirection.FORWARD)
        assertTrue("25 should come before 30", result < 0)
    }

    @Test
    fun testSort_toDto() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = Sort(nameField, SortDirection.FORWARD)

        val dto = sort.toDto()
        val expected = mapOf(
            "field" to "name",
            "direction" to 1
        )

        assertEquals(expected, dto)
    }

    @Test
    fun testSort_toDto_reverseDirection() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val sort = Sort(ageField, SortDirection.REVERSE)

        val dto = sort.toDto()
        val expected = mapOf(
            "field" to "age",
            "direction" to -1
        )

        assertEquals(expected, dto)
    }

    @Test
    fun testSort_comparator_forwardStringSort() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = Sort(nameField, SortDirection.FORWARD)

        val users = listOf(
            TestUser("Charlie", 35, 78.0),
            TestUser("Alice", 25, 85.5),
            TestUser("Bob", 30, 92.0)
        )

        val sortedUsers = users.sortedWith(sort)

        assertEquals("Alice", sortedUsers[0].name)
        assertEquals("Bob", sortedUsers[1].name)
        assertEquals("Charlie", sortedUsers[2].name)
    }

    @Test
    fun testSort_comparator_reverseStringSort() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = Sort(nameField, SortDirection.REVERSE)

        val users = listOf(
            TestUser("Alice", 25, 85.5),
            TestUser("Charlie", 35, 78.0),
            TestUser("Bob", 30, 92.0)
        )

        val sortedUsers = users.sortedWith(sort)

        assertEquals("Charlie", sortedUsers[0].name)
        assertEquals("Bob", sortedUsers[1].name)
        assertEquals("Alice", sortedUsers[2].name)
    }

    @Test
    fun testSort_comparator_forwardIntSort() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val sort = Sort(ageField, SortDirection.FORWARD)

        val users = listOf(
            TestUser("Charlie", 35, 78.0),
            TestUser("Alice", 25, 85.5),
            TestUser("Bob", 30, 92.0)
        )

        val sortedUsers = users.sortedWith(sort)

        assertEquals(25, sortedUsers[0].age)
        assertEquals(30, sortedUsers[1].age)
        assertEquals(35, sortedUsers[2].age)
    }

    @Test
    fun testSort_comparator_reverseIntSort() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val sort = Sort(ageField, SortDirection.REVERSE)

        val users = listOf(
            TestUser("Alice", 25, 85.5),
            TestUser("Charlie", 35, 78.0),
            TestUser("Bob", 30, 92.0)
        )

        val sortedUsers = users.sortedWith(sort)

        assertEquals(35, sortedUsers[0].age)
        assertEquals(30, sortedUsers[1].age)
        assertEquals(25, sortedUsers[2].age)
    }

    @Test
    fun testSort_comparator_forwardDoubleSort() {
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val sort = Sort(scoreField, SortDirection.FORWARD)

        val users = listOf(
            TestUser("Alice", 25, 85.5),
            TestUser("Charlie", 35, 78.0),
            TestUser("Bob", 30, 92.0)
        )

        val sortedUsers = users.sortedWith(sort)

        assertEquals(78.0, sortedUsers[0].score)
        assertEquals(85.5, sortedUsers[1].score)
        assertEquals(92.0, sortedUsers[2].score)
    }

    @Test
    fun testSort_comparator_withNullValues_forward() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = Sort(nameField, SortDirection.FORWARD)

        val users = listOf(
            TestUser("Bob", 30, 92.0),
            TestUser(null, 25, 85.5),
            TestUser("Alice", 35, 78.0)
        )

        val sortedUsers = users.sortedWith(sort)

        // Null values should be treated as empty strings and come first
        assertEquals(null, sortedUsers[0].name)
        assertEquals("Alice", sortedUsers[1].name)
        assertEquals("Bob", sortedUsers[2].name)
    }

    @Test
    fun testSort_comparator_withNullValues_reverse() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sort = Sort(nameField, SortDirection.REVERSE)

        val users = listOf(
            TestUser("Alice", 25, 85.5),
            TestUser(null, 30, 92.0),
            TestUser("Bob", 35, 78.0)
        )

        val sortedUsers = users.sortedWith(sort)

        // In reverse order, non-null values come first
        assertEquals("Bob", sortedUsers[0].name)
        assertEquals("Alice", sortedUsers[1].name)
        assertEquals(null, sortedUsers[2].name)
    }

    @Test
    fun testSortComparator_compare_forward() {
        val comparator = SortComparator<TestUser, String> { it.name ?: "" }

        val user1 = TestUser("Alice", 25, 85.5)
        val user2 = TestUser("Bob", 30, 92.0)

        val result = comparator.compare(user1, user2, SortDirection.FORWARD)
        assertTrue("Alice should come before Bob", result < 0)

        val reverseResult = comparator.compare(user2, user1, SortDirection.FORWARD)
        assertTrue("Bob should come after Alice", reverseResult > 0)
    }

    @Test
    fun testSortComparator_compare_reverse() {
        val comparator = SortComparator<TestUser, String> { it.name ?: "" }

        val user1 = TestUser("Alice", 25, 85.5)
        val user2 = TestUser("Bob", 30, 92.0)

        val result = comparator.compare(user1, user2, SortDirection.REVERSE)
        assertTrue("Alice should come after Bob in reverse", result > 0)

        val reverseResult = comparator.compare(user2, user1, SortDirection.REVERSE)
        assertTrue("Bob should come before Alice in reverse", reverseResult < 0)
    }

    @Test
    fun testSortComparator_compare_withNulls() {
        val comparator = SortComparator<TestUser, String> { it.name ?: "" }

        val user = TestUser("Alice", 25, 85.5)
        val nullUser = TestUser(null, 30, 92.0)

        // Test null vs non-null in forward direction
        val result1 = comparator.compare(nullUser, user, SortDirection.FORWARD)
        assertTrue("Null should come before non-null in forward", result1 < 0)

        val result2 = comparator.compare(user, nullUser, SortDirection.FORWARD)
        assertTrue("Non-null should come after null in forward", result2 > 0)

        // Test null vs non-null in reverse direction
        val result3 = comparator.compare(nullUser, user, SortDirection.REVERSE)
        assertTrue("Null should come after non-null in reverse", result3 > 0)

        val result4 = comparator.compare(user, nullUser, SortDirection.REVERSE)
        assertTrue("Non-null should come before null in reverse", result4 < 0)
    }

    @Test
    fun testSortComparator_compare_bothNull() {
        val comparator = SortComparator<TestUser, String> { it.name ?: "" }

        val nullUser1 = TestUser(null, 25, 85.5)
        val nullUser2 = TestUser(null, 30, 92.0)

        val result = comparator.compare(nullUser1, nullUser2, SortDirection.FORWARD)
        assertEquals("Two null values should be equal", 0, result)
    }

    @Test
    fun testSortComparator_compare_completelyNullObjects() {
        val comparator = SortComparator<TestUser, String> { it.name ?: "" }

        val user = TestUser("Alice", 25, 85.5)

        // Test null object vs non-null object
        val result1 = comparator.compare(null, user, SortDirection.FORWARD)
        assertTrue("Null object should come before non-null", result1 < 0)

        val result2 = comparator.compare(user, null, SortDirection.FORWARD)
        assertTrue("Non-null object should come after null", result2 > 0)

        // Test two null objects
        val result3 = comparator.compare(null, null, SortDirection.FORWARD)
        assertEquals("Two null objects should be equal", 0, result3)
    }

    @Test
    fun testAnySortComparator_delegatesToUnderlyingComparator() {
        val underlying = SortComparator<TestUser, String> { it.name ?: "" }
        val anyComparator = AnySortComparator(underlying)

        val user1 = TestUser("Alice", 25, 85.5)
        val user2 = TestUser("Bob", 30, 92.0)

        val result = anyComparator.compare(user1, user2, SortDirection.FORWARD)
        assertTrue("Should delegate to underlying comparator", result < 0)
    }

    @Test
    fun testSortComparator_toAny() {
        val comparator = SortComparator<TestUser, String> { it.name ?: "" }
        val anyComparator = comparator.toAny()

        val user1 = TestUser("Alice", 25, 85.5)
        val user2 = TestUser("Bob", 30, 92.0)

        val result = anyComparator.compare(user1, user2, SortDirection.FORWARD)
        assertTrue("Type-erased comparator should work the same", result < 0)
    }

    @Test
    fun testList_sortedWith_multipleSortCriteria_sameDirection() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }

        val sorts = listOf(
            Sort(ageField, SortDirection.FORWARD),
            Sort(nameField, SortDirection.FORWARD)
        )

        val users = listOf(
            TestUser("Charlie", 25, 78.0),
            TestUser("Alice", 25, 85.5),  // Same age as Charlie
            TestUser("Bob", 30, 92.0),
            TestUser("David", 25, 88.0)   // Same age as Charlie and Alice
        )

        val sortedUsers = users.sortedWith(sorts)

        // Should be sorted first by age (25, 25, 25, 30), then by name (Alice, Charlie, David, Bob)
        assertEquals(25, sortedUsers[0].age)
        assertEquals("Alice", sortedUsers[0].name)
        assertEquals(25, sortedUsers[1].age)
        assertEquals("Charlie", sortedUsers[1].name)
        assertEquals(25, sortedUsers[2].age)
        assertEquals("David", sortedUsers[2].name)
        assertEquals(30, sortedUsers[3].age)
        assertEquals("Bob", sortedUsers[3].name)
    }

    @Test
    fun testList_sortedWith_multipleSortCriteria_differentDirections() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }

        val sorts = listOf(
            Sort(ageField, SortDirection.FORWARD),      // Age ascending
            Sort(scoreField, SortDirection.REVERSE)     // Score descending
        )

        val users = listOf(
            TestUser("Alice", 25, 85.5),
            TestUser("Bob", 30, 92.0),
            TestUser("Charlie", 25, 95.0),   // Same age as Alice, higher score
            TestUser("David", 25, 75.0)      // Same age as Alice, lower score
        )

        val sortedUsers = users.sortedWith(sorts)

        // Should be sorted first by age (25, 25, 25, 30), then by score descending (95.0, 85.5, 75.0, 92.0)
        assertEquals(25, sortedUsers[0].age)
        assertEquals(95.0, sortedUsers[0].score)
        assertEquals("Charlie", sortedUsers[0].name)

        assertEquals(25, sortedUsers[1].age)
        assertEquals(85.5, sortedUsers[1].score)
        assertEquals("Alice", sortedUsers[1].name)

        assertEquals(25, sortedUsers[2].age)
        assertEquals(75.0, sortedUsers[2].score)
        assertEquals("David", sortedUsers[2].name)

        assertEquals(30, sortedUsers[3].age)
        assertEquals(92.0, sortedUsers[3].score)
        assertEquals("Bob", sortedUsers[3].name)
    }

    @Test
    fun testList_sortedWith_sortOrderMatters() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }

        // First sort: age first, then name
        val sortsAgeFirst = listOf(
            Sort(ageField, SortDirection.FORWARD),
            Sort(nameField, SortDirection.FORWARD)
        )

        // Second sort: name first, then age
        val sortsNameFirst = listOf(
            Sort(nameField, SortDirection.FORWARD),
            Sort(ageField, SortDirection.FORWARD)
        )

        val users = listOf(
            TestUser("Bob", 25, 92.0),
            TestUser("Alice", 30, 85.5)
        )

        val sortedByAgeFirst = users.sortedWith(sortsAgeFirst)
        val sortedByNameFirst = users.sortedWith(sortsNameFirst)

        // Age first: Bob (25) comes before Alice (30)
        assertEquals("Bob", sortedByAgeFirst[0].name)
        assertEquals("Alice", sortedByAgeFirst[1].name)

        // Name first: Alice comes before Bob
        assertEquals("Alice", sortedByNameFirst[0].name)
        assertEquals("Bob", sortedByNameFirst[1].name)
    }

    @Test
    fun testList_sortedWith_threeSortCriteria() {
        val ageField = SortField.create<TestUser, Int>("age") { it.age ?: 0 }
        val scoreField = SortField.create<TestUser, Double>("score") { it.score ?: 0.0 }
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }

        val sorts = listOf(
            Sort(ageField, SortDirection.FORWARD),
            Sort(scoreField, SortDirection.FORWARD),
            Sort(nameField, SortDirection.FORWARD)
        )

        val users = listOf(
            TestUser("Charlie", 25, 85.0),
            TestUser("Bob", 25, 85.0),      // Same age and score as Charlie
            TestUser("Alice", 25, 90.0),    // Same age, higher score
            TestUser("David", 30, 80.0)     // Different age
        )

        val sortedUsers = users.sortedWith(sorts)

        // Should be sorted by age (25, 25, 25, 30), then score (85.0, 85.0, 90.0, 80.0), then name (Bob, Charlie, Alice, David)
        assertEquals("Bob", sortedUsers[0].name)  // age=25, score=85.0, name=Bob
        assertEquals("Charlie", sortedUsers[1].name)    // age=25, score=85.0, name=Charlie
        assertEquals("Alice", sortedUsers[2].name) // age=25, score=90.0, name=Alice
        assertEquals("David", sortedUsers[3].name)  // age=30
    }

    @Test
    fun testList_sortedWith_emptySortList() {
        val users = listOf(
            TestUser("Charlie", 35, 78.0),
            TestUser("Alice", 25, 85.5),
            TestUser("Bob", 30, 92.0)
        )

        val sortedUsers = users.sortedWith(emptyList())

        // Should maintain original order when no sorts are applied
        assertEquals("Charlie", sortedUsers[0].name)
        assertEquals("Alice", sortedUsers[1].name)
        assertEquals("Bob", sortedUsers[2].name)
    }

    @Test
    fun testList_sortedWith_singleSort() {
        val nameField = SortField.create<TestUser, String>("name") { it.name ?: "" }
        val sorts = listOf(Sort(nameField, SortDirection.FORWARD))

        val users = listOf(
            TestUser("Charlie", 35, 78.0),
            TestUser("Alice", 25, 85.5),
            TestUser("Bob", 30, 92.0)
        )

        val sortedUsers = users.sortedWith(sorts)

        // Should behave the same as single Sort.comparator
        assertEquals("Alice", sortedUsers[0].name)
        assertEquals("Bob", sortedUsers[1].name)
        assertEquals("Charlie", sortedUsers[2].name)
    }
} 