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

internal fun EnrichedCollectionResponse.toModel(): CollectionData =
    CollectionData(
        id = id,
        name = name,
        status = this.status.toModel(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        userId = userId,
        custom = custom,
    )

internal fun CollectionResponse.toModel(): CollectionData =
    CollectionData(
        id = id,
        name = name,
        status = null,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userId = userId,
        custom = custom,
    )

private fun EnrichedCollectionResponse.Status.toModel(): CollectionStatus =
    when (this) {
        EnrichedCollectionResponse.Status.Notfound -> CollectionStatus.NotFound
        EnrichedCollectionResponse.Status.Ok -> CollectionStatus.Ok
        is EnrichedCollectionResponse.Status.Unknown -> CollectionStatus.Unknown(unknownValue)
    }
