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

package io.getstream.feeds.android.client.internal.state.query

import io.getstream.feeds.android.client.api.state.query.UsersQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class UsersQueryMappingTest {

    @Test
    fun `toRequest maps limit offset and includeDeactivatedUsers`() {
        val query = UsersQuery(
            limit = 25,
            offset = 10,
            includeDeactivatedUsers = true,
        )

        val payload = query.toRequest()

        assertEquals(25, payload.limit)
        assertEquals(10, payload.offset)
        assertEquals(true, payload.includeDeactivatedUsers)
    }

    @Test
    fun `toRequest with defaults produces empty filterConditions and null optionals`() {
        val query = UsersQuery(limit = 10)

        val payload = query.toRequest()

        assertTrue(payload.filterConditions.isEmpty())
        assertNull(payload.sort)
        assertEquals(10, payload.limit)
        assertNull(payload.offset)
        assertNull(payload.includeDeactivatedUsers)
    }
}
