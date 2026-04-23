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

package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.network.models.Attachment
import io.getstream.feeds.android.network.models.GetOGResponse

/** Converts a [GetOGResponse] to an [Attachment]. */
public fun GetOGResponse.toAttachment(): Attachment =
    Attachment(
        assetUrl = assetUrl,
        authorIcon = authorIcon,
        authorLink = authorLink,
        authorName = authorName,
        color = color,
        fallback = fallback,
        footer = footer,
        footerIcon = footerIcon,
        imageUrl = imageUrl,
        ogScrapeUrl = ogScrapeUrl,
        originalHeight = originalHeight,
        originalWidth = originalWidth,
        pretext = pretext,
        text = text,
        thumbUrl = thumbUrl,
        title = title,
        titleLink = titleLink,
        type = type,
        actions = actions,
        fields = fields,
        giphy = giphy,
    )
