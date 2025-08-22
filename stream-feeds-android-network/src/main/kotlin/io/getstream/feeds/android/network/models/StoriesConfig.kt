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
@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlin.collections.*
import kotlin.io.*

/**  */
public data class StoriesConfig(
    @Json(name = "expiration_behaviour")
    public val expirationBehaviour: ExpirationBehaviour? = null,
    @Json(name = "skip_watched") public val skipWatched: kotlin.Boolean? = null,
) {

    /** ExpirationBehaviour Enum */
    public sealed class ExpirationBehaviour(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): ExpirationBehaviour =
                when (s) {
                    "hide_for_everyone" -> HideForEveryone
                    "visible_for_author" -> VisibleForAuthor
                    else -> Unknown(s)
                }
        }

        public object HideForEveryone : ExpirationBehaviour("hide_for_everyone")

        public object VisibleForAuthor : ExpirationBehaviour("visible_for_author")

        public data class Unknown(val unknownValue: kotlin.String) :
            ExpirationBehaviour(unknownValue)

        public class ExpirationBehaviourAdapter : JsonAdapter<ExpirationBehaviour>() {
            @FromJson
            override fun fromJson(reader: JsonReader): ExpirationBehaviour? {
                val s = reader.nextString() ?: return null
                return ExpirationBehaviour.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: ExpirationBehaviour?) {
                writer.value(value?.value)
            }
        }
    }
}
