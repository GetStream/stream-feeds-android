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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.commentResponse
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentOperationsTest {

    @Test
    fun `toModel maps hidden`() {
        val result = commentResponse().copy(hidden = true).toModel()

        assertEquals(true, result.hidden)
    }

    @Test
    fun `update keeps hidden, because events do not carry it`() {
        val original = commentData("c1").copy(hidden = true)

        val result = original.update(commentData("c1", text = "Edited"))

        assertEquals(true, result.hidden)
        assertEquals("Edited", result.text)
    }

    @Test
    fun `threaded update keeps hidden, because events do not carry it`() {
        val original = ThreadedCommentData(commentData("c1").copy(hidden = true))

        val result = original.update(commentData("c1", text = "Edited"))

        assertEquals(true, result.hidden)
        assertEquals("Edited", result.text)
    }
}
