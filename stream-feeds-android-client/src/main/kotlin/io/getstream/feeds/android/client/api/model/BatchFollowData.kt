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

package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.api.FeedsClient

/**
 * Represents the result of a batch follow operation, i.e. [FeedsClient.getOrCreateFollows].
 *
 * @property created The follows that were created as a result of the getOrCreate operation.
 * @property follows All follows, including existing and newly created ones.
 */
public data class BatchFollowData(val created: List<FollowData>, val follows: List<FollowData>)
