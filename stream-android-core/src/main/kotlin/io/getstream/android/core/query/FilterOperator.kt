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
package io.getstream.android.core.query

/** Contains all possible filter operators that can be used in queries. */
internal object FilterOperator {
    /**
     * Matches values that are equal to a specified value or matches all of the values in an array.
     */
    internal const val EQUAL = "\$eq"

    /** Matches values that are greater than a specified value. */
    internal const val GREATER = "\$gt"

    /** Matches values that are greater than a specified value. */
    internal const val GREATER_OR_EQUAL = "\$gte"

    /** Matches values that are less than a specified value. */
    internal const val LESS = "\$lt"

    /** Matches values that are less than or equal to a specified value. */
    internal const val LESS_OR_EQUAL = "\$lte"

    /** Matches any of the values specified in an array. */
    internal const val IN = "\$in"

    /** Matches values by performing text search with the specified value. */
    internal const val QUERY = "\$q"

    /** Matches values with the specified text. */
    internal const val AUTOCOMPLETE = "\$autocomplete"

    /** Matches values that exist/don't exist based on the specified boolean value. */
    internal const val EXISTS = "\$exists"

    /** Matches all the values specified in an array. */
    internal const val AND = "\$and"

    /** Matches at least one of the values specified in an array. */
    internal const val OR = "\$or"

    /** Matches if the key array contains the given value. */
    internal const val CONTAINS = "\$contains"

    /** Matches if the value contains JSON with the given path. */
    internal const val PATH_EXISTS = "\$path_exists"
}
