/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
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
import io.getstream.feeds.android.network.models.WSEvent

/**
 * A value class that wraps a nullable FID for matching feed identifiers. Adds type safety and
 * clarity around the [matches] function (vs using a nullable string as `fid` in
 * [StateUpdateEvent]s).
 *
 * Needed because `fid` in most **WebSocket** events is reliable and can be used for filtering the
 * event at handling time, but the same isn't true for [StateUpdateEvent]s that we generate
 * internally on successful API calls where we can't know affected fids for sure.
 */
@JvmInline
internal value class FidScope private constructor(private val fid: String?) {
    /** Checks if this matcher matches the given [feedId]. This happens */
    infix fun matches(feedId: FeedId): Boolean = fid == null || fid == feedId.rawValue

    companion object Companion {
        /**
         * Matches all feeds. Useful when constructing [StateUpdateEvent]s internally on successful
         * API calls, since we can't know all fids affected by that call
         */
        val all = FidScope(null)

        /**
         * Matches only the specific feed with the given [fid]. To be used in [StateUpdateEvent]s
         * when we know we can trust the [fid] we pass, e.g. on many [WSEvent]s
         */
        fun of(fid: String) = FidScope(fid)

        /** Matches only the specific feed with the given [fid]. */
        fun of(fid: FeedId) = FidScope(fid.rawValue)
    }
}
