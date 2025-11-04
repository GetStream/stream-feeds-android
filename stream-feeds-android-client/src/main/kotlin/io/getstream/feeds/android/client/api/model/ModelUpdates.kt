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

/**
 * Model representing updates to a collection of items.
 *
 * @param T The type of items in the collection.
 * @property added A list of items that have been added to the collection.
 * @property removedIds A list of IDs of items that have been removed from the collection.
 * @property updated A list of items that have been updated in the collection.
 */
public data class ModelUpdates<T>(
    public val added: List<T>,
    public val removedIds: List<String>,
    public val updated: List<T>,
)
