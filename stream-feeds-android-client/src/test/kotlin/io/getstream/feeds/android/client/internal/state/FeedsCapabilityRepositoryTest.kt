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
package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.processing.StreamBatcher
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.network.apis.FeedsApi
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
internal class FeedsCapabilityRepositoryTest {
    private val batchCallbackSlot = slot<suspend (List<FeedId>, Long, Int) -> Unit>()
    private val batcher: StreamBatcher<FeedId> =
        mockk(relaxed = true) { justRun { onBatch(capture(batchCallbackSlot)) } }
    private val retryProcessor: StreamRetryProcessor = mockk(relaxed = true)
    private val api: FeedsApi = mockk(relaxed = true)

    private val repository =
        FeedsCapabilityRepository(batcher = batcher, retryProcessor = retryProcessor, api = api)
}
