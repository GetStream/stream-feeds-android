/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/** [How a ban or user deletion treats the user's messages.] */
/** DeleteMessages Enum */
public sealed class DeleteMessages(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): DeleteMessages =
            when (s) {
                "hard" -> Hard
                "pruning" -> Pruning
                "soft" -> Soft
                else -> Unknown(s)
            }
    }

    public object Hard : DeleteMessages("hard")

    public object Pruning : DeleteMessages("pruning")

    public object Soft : DeleteMessages("soft")

    public data class Unknown(val unknownValue: String) : DeleteMessages(unknownValue)

    public class DeleteMessagesAdapter : JsonAdapter<DeleteMessages>() {
        @FromJson
        override fun fromJson(reader: JsonReader): DeleteMessages? {
            val s = reader.nextString() ?: return null
            return DeleteMessages.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: DeleteMessages?) {
            writer.value(value?.value)
        }
    }
}
