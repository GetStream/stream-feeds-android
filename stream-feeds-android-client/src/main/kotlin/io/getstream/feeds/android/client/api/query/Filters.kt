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
package io.getstream.feeds.android.client.api.query

/** Utility class for building filters. */
public object Filters {

    /**
     * Creates a filter that checks if a field equals a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check equality against.
     * @return A filter that matches when the field equals the specified value.
     */
    public fun <T : FilterField> equal(field: T, value: Any): Filter<T> = EqualFilter(field, value)

    /**
     * Creates a filter that checks if a field is greater than a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is greater than the specified value.
     */
    public fun <T : FilterField> greater(field: T, value: Any): Filter<T> =
        GreaterThanFilter(field, value)

    /**
     * Creates a filter that checks if a field is greater than or equal to a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is greater than or equal to the specified value.
     */
    public fun <T : FilterField> greaterOrEqual(field: T, value: Any): Filter<T> =
        GreaterThanOrEqualFilter(field, value)

    /**
     * Creates a filter that checks if a field is less than a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is less than the specified value.
     */
    public fun <T : FilterField> less(field: T, value: Any): Filter<T> =
        LessThanFilter(field, value)

    /**
     * Creates a filter that checks if a field is less than or equal to a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is less than or equal to the specified value.
     */
    public fun <T : FilterField> lessOrEqual(field: T, value: Any): Filter<T> =
        LessThanOrEqualFilter(field, value)

    /**
     * Creates a filter that checks if a field's value is in a specific list of values.
     *
     * @param field The field to check.
     * @param values The list of values to check against.
     * @return A filter that matches when the field's value is in the specified array.
     */
    public fun <T : FilterField> `in`(field: T, values: Set<Any>): Filter<T> =
        InFilter(field, values)

    /**
     * Creates a filter that performs a full-text query on a field.
     *
     * @param field The field to query.
     * @param value The query string to search for.
     * @return A filter that matches based on the full-text query.
     */
    public fun <T : FilterField> query(field: T, value: String): Filter<T> =
        QueryFilter(field, value)

    /**
     * Creates a filter that performs autocomplete matching on a field.
     *
     * @param field The field to perform autocomplete on.
     * @param value The string to autocomplete against.
     * @return A filter that matches based on autocomplete functionality.
     */
    public fun <T : FilterField> autocomplete(field: T, value: String): Filter<T> =
        AutocompleteFilter(field, value)

    /**
     * Creates a filter that checks if a field exists or doesn't exist.
     *
     * @param field The field to check for existence.
     * @param value 'true' to check if the field exists, 'false' to check if it doesn't exist.
     * @return A filter that matches when the field exists.
     */
    public fun <T : FilterField> exists(field: T, value: Boolean): Filter<T> =
        ExistsFilter(field, value)

    /**
     * Creates a filter that combines multiple filters with a logical AND operation.
     *
     * @param filters The filters to combine.
     * @return A filter that matches when all provided filters match.
     */
    public fun <T : FilterField> and(vararg filters: Filter<T>): Filter<T> =
        AndFilter(filters.toSet())

    /**
     * Creates a filter that combines multiple filters with a logical OR operation.
     *
     * @param filters The filters to combine.
     * @return A filter that matches when any of the specified filters match.
     */
    public fun <T : FilterField> or(vararg filters: Filter<T>): Filter<T> =
        OrFilter(filters.toSet())

    /**
     * Creates a filter that checks if a field contains a specific value.
     *
     * @param field The field to check for containment.
     * @param value The value to check for within the field.
     * @return A filter that matches when the field contains the specified value.
     */
    public fun <T : FilterField> contains(field: T, value: Any): Filter<T> =
        ContainsFilter(field, value)

    /**
     * Creates a filter that checks if a specific path exists within a field.
     *
     * @param field The field to check.
     * @param value The path to check for existence.
     * @return A filter that matches when the specified path exists in the field.
     */
    public fun <T : FilterField> pathExists(field: T, value: String): Filter<T> =
        PathExistsFilter(field, value)
}

/** @see Filters.equal */
public fun <T : FilterField> T.equal(value: Any): Filter<T> = EqualFilter(this, value)

/** @see Filters.greater */
public fun <T : FilterField> T.greater(value: Any): Filter<T> = GreaterThanFilter(this, value)

/** @see Filters.greaterOrEqual */
public fun <T : FilterField> T.greaterOrEqual(value: Any): Filter<T> =
    GreaterThanOrEqualFilter(this, value)

/** @see Filters.less */
public fun <T : FilterField> T.less(value: Any): Filter<T> = LessThanFilter(this, value)

/** @see Filters.lessOrEqual */
public fun <T : FilterField> T.lessOrEqual(value: Any): Filter<T> =
    LessThanOrEqualFilter(this, value)

/** @see Filters.in */
public fun <T : FilterField> T.`in`(values: List<Any>): Filter<T> = InFilter(this, values.toSet())

/** @see Filters.in */
public fun <T : FilterField> T.`in`(vararg values: Any): Filter<T> = InFilter(this, values.toSet())

/** @see Filters.query */
public fun <T : FilterField> T.query(value: String): Filter<T> = QueryFilter(this, value)

/** @see Filters.autocomplete */
public fun <T : FilterField> T.autocomplete(value: String): Filter<T> =
    AutocompleteFilter(this, value)

/** @see Filters.exists */
public fun <T : FilterField> T.exists(): Filter<T> = ExistsFilter(this, true)

/** @see Filters.exists */
public fun <T : FilterField> T.doesNotExist(): Filter<T> = ExistsFilter(this, false)

/** @see Filters.contains */
public fun <T : FilterField> T.contains(value: Any): Filter<T> = ContainsFilter(this, value)

/** @see Filters.pathExists */
public fun <T : FilterField> T.pathExists(value: String): Filter<T> = PathExistsFilter(this, value)
