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
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.Id.equal"
    )
    public fun equal(field: String, value: Any): Filter = EqualFilter(field, value)

    /**
     * Creates a filter that checks if a field is greater than a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is greater than the specified value.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.CreatedAt.greater"
    )
    public fun greater(field: String, value: Any): Filter = GreaterThanFilter(field, value)

    /**
     * Creates a filter that checks if a field is greater than or equal to a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is greater than or equal to the specified value.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.Popularity.greaterOrEqual"
    )
    public fun greaterOrEqual(field: String, value: Any): Filter =
        GreaterThanOrEqualFilter(field, value)

    /**
     * Creates a filter that checks if a field is less than a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is less than the specified value.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.CreatedAt.less"
    )
    public fun less(field: String, value: Any): Filter = LessThanFilter(field, value)

    /**
     * Creates a filter that checks if a field is less than or equal to a specific value.
     *
     * @param field The field to compare.
     * @param value The value to check against.
     * @return A filter that matches when the field is less than or equal to the specified value.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.Popularity.lessOrEqual"
    )
    public fun lessOrEqual(field: String, value: Any): Filter = LessThanOrEqualFilter(field, value)

    /**
     * Creates a filter that checks if a field's value is in a specific list of values.
     *
     * @param field The field to check.
     * @param values The list of values to check against.
     * @return A filter that matches when the field's value is in the specified array.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.Id.`in`"
    )
    public fun `in`(field: String, values: List<Any>): Filter = InFilter(field, values.toSet())

    /**
     * Creates a filter that performs a full-text query on a field.
     *
     * @param field The field to query.
     * @param value The query string to search for.
     * @return A filter that matches based on the full-text query.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.Text.query"
    )
    public fun query(field: String, value: String): Filter = QueryFilter(field, value)

    /**
     * Creates a filter that performs autocomplete matching on a field.
     *
     * @param field The field to perform autocomplete on.
     * @param value The string to autocomplete against.
     * @return A filter that matches based on autocomplete functionality.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.SearchData.autocomplete"
    )
    public fun autocomplete(field: String, value: String): Filter = AutocompleteFilter(field, value)

    /**
     * Creates a filter that checks if a field exists or doesn't exist.
     *
     * @param field The field to check for existence.
     * @param value 'true' to check if the field exists, 'false' to check if it doesn't exist.
     * @return A filter that matches when the field exists.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.Text.exists or .doesNotExist"
    )
    public fun exists(field: String, value: Boolean): Filter = ExistsFilter(field, value)

    /**
     * Creates a filter that combines multiple filters with a logical AND operation.
     *
     * @param filters The filters to combine.
     * @return A filter that matches when all provided filters match.
     */
    @Deprecated("Use the typed version instead, e.g. Filters.and with TypedFilter instances")
    public fun and(vararg filters: Filter): Filter = AndFilter(filters.toSet())

    /**
     * Creates a filter that combines multiple filters with a logical OR operation.
     *
     * @param filter The filters to combine.
     * @return A filter that matches when any of the specified filters match.
     */
    @Deprecated("Use the typed version instead, i.e. Filters.or with TypedFilter instances")
    public fun or(vararg filter: Filter): Filter = OrFilter(filter.toSet())

    /**
     * Creates a filter that checks if a field contains a specific value.
     *
     * @param field The field to check for containment.
     * @param value The value to check for within the field.
     * @return A filter that matches when the field contains the specified value.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, i.e. ActivitiesFilterField.FilterTags.contains"
    )
    public fun contains(field: String, value: Any): Filter = ContainsFilter(field, value)

    /**
     * Creates a filter that checks if a specific path exists within a field.
     *
     * @param field The field to check.
     * @param value The path to check for existence.
     * @return A filter that matches when the specified path exists in the field.
     */
    @Deprecated(
        "Use the extension function on the relevant FilterField, e.g. ActivitiesFilterField.SearchData.pathExists"
    )
    public fun pathExists(field: String, value: String): Filter = PathExistsFilter(field, value)

    /**
     * Creates a filter that combines multiple filters with a logical AND operation.
     *
     * @param filters The filters to combine.
     * @return A filter that matches when all provided filters match.
     */
    public fun <T : FilterField> and(vararg filters: TypedFilter<T>): TypedFilter<T> =
        CollectionOperationFilter(FilterOperator.AND, filters.toSet())

    /**
     * Creates a filter that combines multiple filters with a logical OR operation.
     *
     * @param filters The filters to combine.
     * @return A filter that matches when any of the specified filters match.
     */
    public fun <T : FilterField> or(vararg filters: TypedFilter<T>): TypedFilter<T> =
        CollectionOperationFilter(FilterOperator.OR, filters.toSet())
}

/**
 * Creates a filter that checks if this field equals a specific value.
 *
 * @param value The value to check equality against.
 * @return A filter that matches when this field equals the specified value.
 */
public fun <T : FilterField> T.equal(value: Any): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.EQUAL, this, value)

/**
 * Creates a filter that checks if this field is greater than a specific value.
 *
 * @param value The value to check against.
 * @return A filter that matches when this field is greater than the specified value.
 */
public fun <T : FilterField> T.greater(value: Any): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.GREATER, this, value)

/**
 * Creates a filter that checks if this field is greater than or equal to a specific value.
 *
 * @param value The value to check against.
 * @return A filter that matches when this field is greater than or equal to the specified value.
 */
public fun <T : FilterField> T.greaterOrEqual(value: Any): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.GREATER_OR_EQUAL, this, value)

/**
 * Creates a filter that checks if this field is less than a specific value.
 *
 * @param value The value to check against.
 * @return A filter that matches when this field is less than the specified value.
 */
public fun <T : FilterField> T.less(value: Any): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.LESS, this, value)

/**
 * Creates a filter that checks if this field is less than or equal to a specific value.
 *
 * @param value The value to check against.
 * @return A filter that matches when this field is less than or equal to the specified value.
 */
public fun <T : FilterField> T.lessOrEqual(value: Any): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.LESS_OR_EQUAL, this, value)

/**
 * Creates a filter that checks if this field's value is in a specific list of values.
 *
 * @param values The list of values to check against.
 * @return A filter that matches when this field's value is in the specified list.
 */
public fun <T : FilterField> T.`in`(values: List<Any>): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.IN, this, values.toSet())

/**
 * Creates a filter that checks if this field's value is in a specific set of values.
 *
 * @param values The values to check against.
 * @return A filter that matches when this field's value is in the specified values.
 */
public fun <T : FilterField> T.`in`(vararg values: Any): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.IN, this, values.toSet())

/**
 * Creates a filter that performs a full-text query on this field.
 *
 * @param value The query string to search for.
 * @return A filter that matches based on the full-text query.
 */
public fun <T : FilterField> T.query(value: String): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.QUERY, this, value)

/**
 * Creates a filter that performs autocomplete matching on this field.
 *
 * @param value The string to autocomplete against.
 * @return A filter that matches based on autocomplete functionality.
 */
public fun <T : FilterField> T.autocomplete(value: String): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.AUTOCOMPLETE, this, value)

/**
 * Creates a filter that checks if this field exists.
 *
 * @return A filter that matches when this field exists.
 */
public fun <T : FilterField> T.exists(): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.EXISTS, this, true)

/**
 * Creates a filter that checks if this field does not exist.
 *
 * @return A filter that matches when this field does not exist.
 */
public fun <T : FilterField> T.doesNotExist(): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.EXISTS, this, false)

/**
 * Creates a filter that checks if this field contains a specific value.
 *
 * @param value The value to check for within this field.
 * @return A filter that matches when this field contains the specified value.
 */
public fun <T : FilterField> T.contains(value: Any): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.CONTAINS, this, value)

/**
 * Creates a filter that checks if a specific path exists within this field.
 *
 * @param value The path to check for existence.
 * @return A filter that matches when the specified path exists in this field.
 */
public fun <T : FilterField> T.pathExists(value: String): TypedFilter<T> =
    BinaryOperationFilter(FilterOperator.PATH_EXISTS, this, value)
