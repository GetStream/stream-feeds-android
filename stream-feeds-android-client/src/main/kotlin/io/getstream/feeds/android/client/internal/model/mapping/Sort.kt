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
package io.getstream.feeds.android.client.internal.model.mapping

import io.getstream.android.core.query.Sort
import io.getstream.feeds.android.network.models.SortParamRequest

/**
 * Converts a [Sort] operation to a [SortParamRequest] for use in API requests.
 *
 * @return A [SortParamRequest] representing the sort operation.
 */
internal fun Sort<*>.toRequest(): SortParamRequest =
    SortParamRequest(field = field.remote, direction = direction.value)
