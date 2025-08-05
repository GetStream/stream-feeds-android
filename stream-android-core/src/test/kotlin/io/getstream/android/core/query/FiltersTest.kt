package io.getstream.android.core.query

import org.junit.Assert
import org.junit.Test

internal class FiltersTest {

    @Test
    fun testEqualFilterCreation() {
        val filter = Filters.equal("field", "value")
        assert(filter is EqualFilter)
        filter as EqualFilter
        Assert.assertEquals("field", filter.field)
        Assert.assertEquals("value", filter.value)
    }

    @Test
    fun testGreaterFilterCreation() {
        val filter = Filters.greater("age", 18)
        assert(filter is GreaterThanFilter)
        filter as GreaterThanFilter
        Assert.assertEquals("age", filter.field)
        Assert.assertEquals(18, filter.value)
    }

    @Test
    fun testGreaterOrEqualFilterCreation() {
        val filter = Filters.greaterOrEqual("score", 100)
        assert(filter is GreaterThanOrEqualFilter)
        filter as GreaterThanOrEqualFilter
        Assert.assertEquals("score", filter.field)
        Assert.assertEquals(100, filter.value)
    }

    @Test
    fun testLessFilterCreation() {
        val filter = Filters.less("price", 50.0)
        assert(filter is LessThanFilter)
        filter as LessThanFilter
        Assert.assertEquals("price", filter.field)
        Assert.assertEquals(50.0, filter.value)
    }

    @Test
    fun testLessOrEqualFilterCreation() {
        val filter = Filters.lessOrEqual("quantity", 10)
        assert(filter is LessThanOrEqualFilter)
        filter as LessThanOrEqualFilter
        Assert.assertEquals("quantity", filter.field)
        Assert.assertEquals(10, filter.value)
    }

    @Test
    fun testInFilterCreation() {
        val values = listOf("active", "pending", "completed")
        val filter = Filters.`in`("status", values)
        assert(filter is InFilter)
        filter as InFilter
        Assert.assertEquals("status", filter.field)
        Assert.assertEquals(values.toSet(), filter.values)
    }

    @Test
    fun testQueryFilterCreation() {
        val filter = Filters.query("content", "search term")
        assert(filter is QueryFilter)
        filter as QueryFilter
        Assert.assertEquals("content", filter.field)
        Assert.assertEquals("search term", filter.value)
    }

    @Test
    fun testAutocompleteFilterCreation() {
        val filter = Filters.autocomplete("name", "john")
        assert(filter is AutocompleteFilter)
        filter as AutocompleteFilter
        Assert.assertEquals("name", filter.field)
        Assert.assertEquals("john", filter.value)
    }

    @Test
    fun testExistsFilterCreation() {
        val filter = Filters.exists("optional_field", true)
        assert(filter is ExistsFilter)
        filter as ExistsFilter
        Assert.assertEquals("optional_field", filter.field)
        Assert.assertEquals(true, filter.value)
    }

    @Test
    fun testExistsFilterFalseCreation() {
        val filter = Filters.exists("missing_field", false)
        assert(filter is ExistsFilter)
        filter as ExistsFilter
        Assert.assertEquals("missing_field", filter.field)
        Assert.assertEquals(false, filter.value)
    }

    @Test
    fun testAndFilterCreation() {
        val filter1 = Filters.equal("type", "post")
        val filter2 = Filters.greater("likes", 10)
        val andFilter = Filters.and(filter1, filter2)
        assert(andFilter is AndFilter)
        andFilter as AndFilter
        Assert.assertEquals(setOf(filter1, filter2), andFilter.filters)
    }

    @Test
    fun testOrFilterCreation() {
        val filter1 = Filters.equal("status", "published")
        val filter2 = Filters.equal("status", "draft")
        val orFilter = Filters.or(filter1, filter2)
        assert(orFilter is OrFilter)
        orFilter as OrFilter
        Assert.assertEquals(setOf(filter1, filter2), orFilter.filters)
    }

    @Test
    fun testContainsFilterCreation() {
        val filter = Filters.contains("tags", "important")
        assert(filter is ContainsFilter)
        filter as ContainsFilter
        Assert.assertEquals("tags", filter.field)
        Assert.assertEquals("important", filter.value)
    }

    @Test
    fun testPathExistsFilterCreation() {
        val filter = Filters.pathExists("metadata", "user.preferences.theme")
        assert(filter is PathExistsFilter)
        filter as PathExistsFilter
        Assert.assertEquals("metadata", filter.field)
        Assert.assertEquals("user.preferences.theme", filter.value)
    }
}
