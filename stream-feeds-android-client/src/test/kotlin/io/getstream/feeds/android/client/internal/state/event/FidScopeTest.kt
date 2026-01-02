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

package io.getstream.feeds.android.client.internal.state.event

import io.getstream.feeds.android.client.api.model.FeedId
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FidScopeTest {
    private val specificScope = FidScope.of("feed:id")
    private val matchingId = FeedId("feed", "id")
    private val nonMatchingId = FeedId("feed", "but-different")

    @Test
    fun `unknown scope should match any feed`() {
        assertTrue(FidScope.unknown matches matchingId)
    }

    @Test
    fun `specific scope should match same feed`() {
        assertTrue(specificScope matches matchingId)
    }

    @Test
    fun `specific scope should not match different feed`() {
        assertFalse(specificScope matches nonMatchingId)
    }

    @Test
    fun `unknown scope should not strictly match any feed`() {
        assertFalse(FidScope.unknown strictlyMatches matchingId)
    }

    @Test
    fun `specific scope should strictly match same feed`() {
        assertTrue(specificScope strictlyMatches matchingId)
    }

    @Test
    fun `specific scope should not strictly match different feed`() {
        assertFalse(specificScope strictlyMatches nonMatchingId)
    }
}
