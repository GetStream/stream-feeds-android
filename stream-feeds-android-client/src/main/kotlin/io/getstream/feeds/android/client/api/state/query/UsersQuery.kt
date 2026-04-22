/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.api.filter.Filter
import io.getstream.android.core.api.filter.FilterField
import io.getstream.android.core.api.sort.Sort
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.android.core.api.sort.SortField
import io.getstream.feeds.android.client.api.model.UserData
import java.util.Date

/**
 * A query for retrieving users with filtering, sorting, and pagination options.
 *
 * ## Example Usage
 *
 * ```kotlin
 * val query = UsersQuery(
 *   filter = UsersFilterField.role.equal("admin"),
 *   sort = listOf(UsersSort(UsersSortField.CreatedAt, SortDirection.REVERSE)),
 *   limit = 10,
 * )
 * ```
 *
 * @property filter Filter criteria for the users query.
 * @property sort Sorting criteria for the users query.
 * @property limit Maximum number of users to return in a single request.
 * @property offset Number of users to skip before returning results.
 * @property includeDeactivatedUsers Whether to include deactivated users in the results.
 */
public data class UsersQuery(
    public val filter: UsersFilter? = null,
    public val sort: List<UsersSort>? = null,
    public val limit: Int? = null,
    public val offset: Int? = null,
    public val includeDeactivatedUsers: Boolean? = null,
)

/** A type alias representing a filter specifically for [UserData] using [UsersFilterField]. */
public typealias UsersFilter = Filter<UserData, UsersFilterField>

/**
 * Represents a field that can be used to filter users.
 *
 * @property remote The field name as expected by the API in query filter conditions.
 * @property localValue Returns the corresponding local value for client-side matching.
 */
public data class UsersFilterField(
    override val remote: String,
    override val localValue: (UserData) -> Any?,
) : FilterField<UserData> {
    public companion object {
        /**
         * Filter by user ID. Supported operators: `equal`, `notEqual`, `greater`, `greaterOrEqual`,
         * `less`, `lessOrEqual`, `in`, `notIn`, `exists`, `autocomplete`
         */
        public val id: UsersFilterField = UsersFilterField("id", UserData::id)

        /**
         * Filter by user role. Supported operators: `equal`, `notEqual`, `greater`,
         * `greaterOrEqual`, `less`, `lessOrEqual`, `in`, `notIn`, `exists`
         */
        public val role: UsersFilterField = UsersFilterField("role", UserData::role)

        /** Filter by ban status. Supported operators: `equal` */
        public val banned: UsersFilterField = UsersFilterField("banned", UserData::banned)

        /**
         * Filter by creation timestamp. Supported operators: `equal`, `notEqual`, `greater`,
         * `greaterOrEqual`, `less`, `lessOrEqual`, `in`, `notIn`, `exists`
         */
        public val createdAt: UsersFilterField = UsersFilterField("created_at", UserData::createdAt)

        /**
         * Filter by update timestamp. Supported operators: `equal`, `notEqual`, `greater`,
         * `greaterOrEqual`, `less`, `lessOrEqual`, `in`, `notIn`, `exists`
         */
        public val updatedAt: UsersFilterField = UsersFilterField("updated_at", UserData::updatedAt)

        /**
         * Filter by last active timestamp. Supported operators: `equal`, `notEqual`, `greater`,
         * `greaterOrEqual`, `less`, `lessOrEqual`, `in`, `notIn`, `exists`
         */
        public val lastActive: UsersFilterField =
            UsersFilterField("last_active", UserData::lastActive)

        /** Filter by teams. Supported operators: `equal`, `contains`, `in` */
        public val teams: UsersFilterField = UsersFilterField("teams", UserData::teams)

        /** Filter by user name. Supported operators: `equal`, `autocomplete` */
        public val name: UsersFilterField = UsersFilterField("name", UserData::name)
    }
}

/**
 * Represents a sorting operation for users.
 *
 * @property field The field by which to sort users.
 * @property direction The direction of the sort operation.
 */
public class UsersSort(field: UsersSortField, direction: SortDirection) :
    Sort<UserData>(field, direction)

/** Defines the fields by which users can be sorted. */
public sealed interface UsersSortField : SortField<UserData> {

    /** Sort by user ID. */
    public data object Id :
        UsersSortField, SortField<UserData> by SortField.create("id", UserData::id)

    /** Sort by creation timestamp. */
    public data object CreatedAt :
        UsersSortField, SortField<UserData> by SortField.create("created_at", UserData::createdAt)

    /** Sort by update timestamp. */
    public data object UpdatedAt :
        UsersSortField, SortField<UserData> by SortField.create("updated_at", UserData::updatedAt)

    /** Sort by last active timestamp. */
    public data object LastActive :
        UsersSortField,
        SortField<UserData> by SortField.create("last_active", { it.lastActive ?: Date(0) })

    /** Sort by user role. */
    public data object Role :
        UsersSortField, SortField<UserData> by SortField.create("role", UserData::role)
}
