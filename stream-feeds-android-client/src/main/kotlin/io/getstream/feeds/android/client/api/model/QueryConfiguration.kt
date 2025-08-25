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

import io.getstream.feeds.android.client.api.query.Filter
import io.getstream.feeds.android.client.api.query.Sort

/**
 * Wrapper around the query configuration consisting of a [Filter] and a list of [Sort] operations.
 *
 * @property filter The filter for the query.
 * @property sort The list of sorting operations for the query.
 */
internal data class QueryConfiguration<S : Sort<*>>(val filter: Filter?, val sort: List<S>?)
