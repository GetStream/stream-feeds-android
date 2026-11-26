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

package io.getstream.feeds.android.client.internal.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

internal class ListPrependAllTest {

    @Test
    fun `prependAll puts the new elements first`() {
        val result = listOf("c" to 1, "d" to 1).prependAll(listOf("a" to 1, "b" to 1)) { it.first }

        assertEquals(listOf("a" to 1, "b" to 1, "c" to 1, "d" to 1), result)
    }

    @Test
    fun `prependAll drops the existing elements that the new ones replace`() {
        val result = listOf("b" to 1, "c" to 1).prependAll(listOf("a" to 2, "b" to 2)) { it.first }

        assertEquals(listOf("a" to 2, "b" to 2, "c" to 1), result)
    }

    @Test
    fun `prependAll with nothing to add returns the same list`() {
        val list = listOf("a" to 1)

        assertSame(list, list.prependAll(emptyList()) { it.first })
    }
}
