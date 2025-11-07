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

package io.getstream.feeds.android.sample.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface AsyncResource<out T> {
    data object Loading : AsyncResource<Nothing>

    data object Error : AsyncResource<Nothing>

    data class Content<T>(val data: T) : AsyncResource<T>

    companion object
}

fun <T : Any> AsyncResource.Companion.notNull(data: T?) =
    when (data) {
        null -> AsyncResource.Error
        else -> AsyncResource.Content(data)
    }

inline fun <T : Any, R : Any> AsyncResource<T>.map(transform: (T) -> R): AsyncResource<R> =
    when (this) {
        is AsyncResource.Loading -> AsyncResource.Loading
        is AsyncResource.Error -> AsyncResource.Error
        is AsyncResource.Content -> AsyncResource.Content(transform(data))
    }

fun <T> AsyncResource<T>.getOrNull(): T? = (this as? AsyncResource.Content)?.data

suspend inline fun <T, R> Flow<AsyncResource<T>>.withFirstContent(block: suspend T.() -> R): R =
    filterIsInstance<AsyncResource.Content<T>>().first().data.block()

fun <T> Flow<AsyncResource<T>>.withFirstContent(
    scope: CoroutineScope,
    block: suspend T.() -> Unit,
) {
    scope.launch { withFirstContent(block) }
}
