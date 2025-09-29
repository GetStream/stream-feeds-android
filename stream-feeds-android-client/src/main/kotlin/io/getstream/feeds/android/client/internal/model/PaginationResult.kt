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
package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.PaginationData

/**
 * Data class representing a paginated result containing a list of models and pagination data.
 *
 * @param T The type of the models in the list.
 * @property models The list of models in the paginated result.
 * @property pagination The pagination data for the result.
 */
internal data class PaginationResult<T>(val models: List<T>, val pagination: PaginationData)
