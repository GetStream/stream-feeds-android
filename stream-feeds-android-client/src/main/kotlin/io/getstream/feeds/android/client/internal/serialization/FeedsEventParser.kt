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

package io.getstream.feeds.android.client.internal.serialization

import io.getstream.android.core.api.serialization.StreamEventSerialization
import io.getstream.android.core.api.serialization.StreamJsonSerialization
import io.getstream.feeds.android.network.models.WSEvent

/**
 * Parser for WebSocket events in the Feeds client.
 *
 * @property jsonParser The JSON parser used to serialize and deserialize events.
 */
internal class FeedsEventParser(private val jsonParser: StreamJsonSerialization) :
    StreamEventSerialization<WSEvent> {

    override fun serialize(data: WSEvent): Result<String> = jsonParser.toJson(data)

    override fun deserialize(raw: String): Result<WSEvent> =
        jsonParser.fromJson(raw, WSEvent::class.java)
}
