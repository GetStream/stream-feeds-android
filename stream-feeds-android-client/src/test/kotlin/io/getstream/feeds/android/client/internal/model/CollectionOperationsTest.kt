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

import io.getstream.feeds.android.client.api.model.CollectionData
import io.getstream.feeds.android.client.api.model.CollectionStatus
import io.getstream.feeds.android.network.models.CollectionResponse
import io.getstream.feeds.android.network.models.EnrichedCollectionResponse
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectionOperationsTest {

    @Test
    fun `CollectionResponse toModel maps all fields correctly`() {
        val createdAt = Date(1000L)
        val updatedAt = Date(2000L)
        val customData = mapOf("key1" to "value1", "key2" to 42)

        val collectionResponse =
            CollectionResponse(
                id = "collection-123",
                name = "movies",
                createdAt = createdAt,
                updatedAt = updatedAt,
                userId = "user-456",
                custom = customData,
            )

        val result = collectionResponse.toModel()

        val expected =
            CollectionData(
                id = "collection-123",
                name = "movies",
                status = null,
                createdAt = createdAt,
                updatedAt = updatedAt,
                userId = "user-456",
                custom = customData,
            )

        assertEquals(expected, result)
    }

    @Test
    fun `EnrichedCollectionResponse toModel maps all fields correctly`() {
        val createdAt = Date(1000L)
        val updatedAt = Date(2000L)
        val customData = mapOf("genre" to "action", "rating" to 8.5)

        val enrichedResponse =
            EnrichedCollectionResponse(
                id = "enriched-789",
                name = "products",
                status = EnrichedCollectionResponse.Status.Unknown("abcd"),
                createdAt = createdAt,
                updatedAt = updatedAt,
                userId = "user-123",
                custom = customData,
            )

        val result = enrichedResponse.toModel()

        val expected =
            CollectionData(
                id = "enriched-789",
                name = "products",
                status = CollectionStatus.Unknown("abcd"),
                createdAt = createdAt,
                updatedAt = updatedAt,
                userId = "user-123",
                custom = customData,
            )

        assertEquals(expected, result)
    }
}
