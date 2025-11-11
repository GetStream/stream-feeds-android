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

package io.getstream.feeds.android.sample.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.getstream.feeds.android.network.models.Attachment

@Composable
fun ImageAttachmentsSection(attachments: List<Attachment>) {
    if (attachments.size == 1) {
        AsyncImage(
            modifier =
                Modifier.fillMaxWidth()
                    .height(240.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
            model = attachments.first().assetUrl,
            contentDescription = "Activity image",
            contentScale = ContentScale.Crop,
        )
    } else if (attachments.size > 1) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(attachments) { attachment ->
                AsyncImage(
                    modifier = Modifier.size(160.dp).clip(RoundedCornerShape(12.dp)),
                    model = attachment.assetUrl,
                    contentDescription = "Activity image",
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
