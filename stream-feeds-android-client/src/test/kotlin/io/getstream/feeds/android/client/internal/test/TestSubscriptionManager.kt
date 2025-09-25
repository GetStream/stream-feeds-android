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
package io.getstream.feeds.android.client.internal.test

import io.getstream.android.core.api.subscribe.StreamSubscription
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager

internal class TestSubscriptionManager<T>(vararg initialListeners: T) :
    StreamSubscriptionManager<T> {
    private val subscribed: MutableSet<T> = initialListeners.toMutableSet()

    override fun subscribe(
        listener: T,
        options: StreamSubscriptionManager.Options,
    ): Result<StreamSubscription> {
        subscribed.add(listener)

        return Result.success(
            object : StreamSubscription {
                override fun cancel() {
                    subscribed.remove(listener)
                }
            }
        )
    }

    override fun clear(): Result<Unit> {
        subscribed.clear()
        return Result.success(Unit)
    }

    override fun forEach(block: (T) -> Unit): Result<Unit> {
        subscribed.forEach(block)
        return Result.success(Unit)
    }
}
