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

package io.getstream.feeds.android.client.internal.file

import io.getstream.feeds.android.client.api.file.FileType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

internal class FileTypeTest {

    @Test
    fun `toAttachmentType should map all known FileTypes to the correct types`() {
        class UnknownCustomFileType : FileType

        val expectations =
            mapOf(
                FileType.Image to "image",
                FileType.Other to "file",
                UnknownCustomFileType() to null,
            )

        // Verify we map types correctly
        expectations.forEach { (fileType, expected) ->
            assertEquals(expected, fileType.toAttachmentType())
        }

        // To prevent the case where we add a new FileType but we forget to handle it
        val handled = expectations.keys.mapTo(mutableSetOf()) { it::class }
        val unhandled = FileType::class.nestedClasses - handled

        assertTrue(
            "Unhandled FileType(s): ${unhandled.joinToString { it.simpleName!! }}",
            unhandled.isEmpty(),
        )
    }
}
