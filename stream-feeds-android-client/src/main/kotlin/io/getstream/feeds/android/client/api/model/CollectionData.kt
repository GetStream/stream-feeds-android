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

package io.getstream.feeds.android.client.api.model

import java.util.Date

/**
 * Content like products, articles, or any custom objects.
 *
 * @property id The unique identifier of the collection.
 * @property name The name of the collection.
 * @property status The status of the collection when enriched. This field is only present when the
 *   collection is embedded in an activity. It indicates whether the collection was found during
 *   enrichment.
 * @property createdAt The date and time when the collection was created.
 * @property updatedAt The date and time when the collection was last updated.
 * @property userId The ID of the user who owns this collection.
 * @property custom Custom data associated with the collection.
 */
public data class CollectionData(
    val id: String,
    val name: String,
    val status: CollectionStatus? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val userId: String? = null,
    val custom: Map<String, Any?>? = null,
)

/**
 * The status of a collection when enriched. This indicates whether the collection was successfully
 * found during activity enrichment.
 */
public sealed class CollectionStatus(public val value: String) {

    /** The collection was found and enriched successfully. */
    public data object Ok : CollectionStatus("ok")

    /** The collection was not found during enrichment. */
    public data object NotFound : CollectionStatus("notfound")

    /** Unknown status. */
    public data class Unknown(val unknownValue: String) : CollectionStatus(unknownValue)
}
