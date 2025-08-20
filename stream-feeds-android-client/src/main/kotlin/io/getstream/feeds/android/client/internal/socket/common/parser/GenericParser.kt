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
package io.getstream.feeds.android.client.internal.socket.common.parser

/** Generic parser interface for encoding and decoding objects. */
internal interface GenericParser<E, E2> {
    /** Encodes the given object into a ByteString. */
    fun encode(event: E): Result<String>

    /** Decodes the given [raw] [ByteArray] into an object of type [E2]. */
    fun decode(raw: String): Result<E2>
}
