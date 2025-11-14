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

package io.getstream.feeds.android.client.api.state.query

/**
 * Represents the action to take when inserting an item into a list. Used, for example, to decide if
 * and where to insert new activities into a feed.
 *
 * @see [FeedQuery]
 */
public enum class InsertionAction {
    /** Insert the item at the start of the list. */
    AddToStart,

    /** Insert the item at the end of the list. */
    AddToEnd,

    /** Do not insert the item into the list. */
    Ignore,
}
