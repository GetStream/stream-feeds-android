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

public interface FilterField {
    public val raw: String
}

public open class DefaultFilterField(override val raw: String) : FilterField

public sealed class Filter<T : FilterField>

/**
 * Filter that matches values that are equal to a specified value.
 *
 * @param field The field name to compare against
 * @param value The value to check equality against
 */
public data class EqualFilter<T : FilterField> internal constructor(val field: T, val value: Any) :
    Filter<T>()

/**
 * Filter that matches values that are greater than a specified value.
 *
 * @param field The field name to compare
 * @param value The threshold value for comparison
 */
public data class GreaterThanFilter<T : FilterField>
internal constructor(val field: T, val value: Any) : Filter<T>()

/**
 * Filter that matches values that are greater than or equal to a specified value.
 *
 * @param field The field name to compare
 * @param value The threshold value for comparison
 */
public data class GreaterThanOrEqualFilter<T : FilterField>
internal constructor(val field: T, val value: Any) : Filter<T>()

/**
 * Filter that matches values that are less than a specified value.
 *
 * @param field The field name to compare
 * @param value The threshold value for comparison
 */
public data class LessThanFilter<T : FilterField>
internal constructor(val field: T, val value: Any) : Filter<T>()

/**
 * Filter that matches values that are less than or equal to a specified value.
 *
 * @param field The field name to compare
 * @param value The threshold value for comparison
 */
public data class LessThanOrEqualFilter<T : FilterField>
internal constructor(val field: T, val value: Any) : Filter<T>()

/**
 * Filter that matches values that are contained in a specified set of values.
 *
 * @param field The field name to check
 * @param values The set of values to check against for membership
 */
public data class InFilter<T : FilterField>
internal constructor(val field: T, val values: Set<Any>) : Filter<T>()

/**
 * Filter that performs a full-text query on a specified field.
 *
 * @param field The field name to search within
 * @param value The query string to search for
 */
public data class QueryFilter<T : FilterField>
internal constructor(val field: T, val value: String) : Filter<T>()

/**
 * Filter that performs autocomplete matching on a specified field.
 *
 * @param field The field name to perform autocomplete matching on
 * @param value The string to match against for autocomplete suggestions
 */
public data class AutocompleteFilter<T : FilterField>
internal constructor(val field: T, val value: String) : Filter<T>()

/**
 * Filter that checks whether a specified field exists or doesn't exist.
 *
 * @param field The field name to check for existence
 * @param value `true` to match when the field exists, `false` to match when it doesn't exist
 */
public data class ExistsFilter<T : FilterField>
internal constructor(val field: T, val value: Boolean) : Filter<T>()

/**
 * Filter that combines multiple filters with a logical AND operation.
 *
 * @param filters The set of filters to combine with AND logic
 */
public data class AndFilter<T : FilterField> internal constructor(val filters: Set<Filter<T>>) :
    Filter<T>()

/**
 * Filter that combines multiple filters with a logical OR operation.
 *
 * @param filters The set of filters to combine with OR logic
 */
public data class OrFilter<T : FilterField> internal constructor(val filters: Set<Filter<T>>) :
    Filter<T>()

/**
 * Filter that checks if a field contains a specific value.
 *
 * @param field The field name to check for containment
 * @param value The value to check for within the field
 */
public data class ContainsFilter<T : FilterField>
internal constructor(val field: T, val value: Any) : Filter<T>()

/**
 * Filter that checks if a specific path exists within a field.
 *
 * @param field The field name to check
 * @param value The path string to check for existence within the field
 */
public data class PathExistsFilter<T : FilterField>
internal constructor(val field: T, val value: String) : Filter<T>()

/** Converts a [Filter] instance to a request map suitable for API queries. */
internal fun Filter<*>.toRequest(): Map<String, Any> =
    when (this) {
        is EqualFilter -> mapOf(field.raw to value)
        is GreaterThanFilter -> mapOf(field.raw to mapOf(FilterOperator.GREATER to value))
        is GreaterThanOrEqualFilter ->
            mapOf(field.raw to mapOf(FilterOperator.GREATER_OR_EQUAL to value))
        is LessThanFilter -> mapOf(field.raw to mapOf(FilterOperator.LESS to value))
        is LessThanOrEqualFilter -> mapOf(field.raw to mapOf(FilterOperator.LESS_OR_EQUAL to value))
        is InFilter -> mapOf(field.raw to mapOf(FilterOperator.IN to values))
        is QueryFilter -> mapOf(field.raw to mapOf(FilterOperator.QUERY to value))
        is AutocompleteFilter -> mapOf(field.raw to mapOf(FilterOperator.AUTOCOMPLETE to value))
        is ExistsFilter -> mapOf(field.raw to mapOf(FilterOperator.EXISTS to value))
        is AndFilter -> mapOf(FilterOperator.AND to filters.map(Filter<*>::toRequest))
        is OrFilter -> mapOf(FilterOperator.OR to filters.map(Filter<*>::toRequest))
        is ContainsFilter -> mapOf(field.raw to mapOf(FilterOperator.CONTAINS to value))
        is PathExistsFilter -> mapOf(field.raw to mapOf(FilterOperator.PATH_EXISTS to value))
    }
