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

package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.CreateCollectionsRequest
import io.getstream.feeds.android.network.models.UpdateCollectionsRequest

internal class CollectionsRepositoryImpl(private val api: FeedsApi) : CollectionsRepository {
    override suspend fun readCollections(refs: List<String>) = runSafely {
        api.readCollections(refs)
    }

    override suspend fun createCollections(request: CreateCollectionsRequest) = runSafely {
        api.createCollections(request)
    }

    override suspend fun deleteCollections(refs: List<String>) = runSafely {
        api.deleteCollections(refs)
    }

    override suspend fun updateCollections(request: UpdateCollectionsRequest) = runSafely {
        api.updateCollections(request)
    }
}
