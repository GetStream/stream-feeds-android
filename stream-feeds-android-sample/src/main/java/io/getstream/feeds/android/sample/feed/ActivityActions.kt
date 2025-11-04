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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.sample.R

@Composable
fun ActivityActions(
    activity: ActivityData,
    onCommentClick: () -> Unit,
    onReactionClick: (Reaction) -> Unit,
    onRepostClick: () -> Unit,
    onBookmarkClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Comments
        ActionButton(
            icon = painterResource(R.drawable.comment_24),
            count = activity.commentCount,
            contentDescription = "Comments",
            onClick = onCommentClick,
        )

        Reaction.entries.forEach { reaction ->
            val reactionCount = activity.reactionGroups[reaction.value]?.count ?: 0
            val hasOwnReaction = activity.ownReactions.any { it.type == reaction.value }

            ActionButton(
                icon = reaction.painter(hasOwnReaction),
                count = reactionCount,
                contentDescription = reaction.description,
                onClick = { onReactionClick(reaction) },
                tint = reaction.color,
            )
        }

        // Reposts
        ActionButton(
            icon = painterResource(R.drawable.repost_24),
            count = activity.shareCount,
            contentDescription = "Repost",
            onClick = onRepostClick,
            tint = Color(0xFF4CAF50), // Green color for reposts
        )

        // Bookmarks
        val bookmarkIcon =
            if (activity.ownBookmarks.isNotEmpty()) {
                painterResource(R.drawable.bookmark_filled_24)
            } else {
                painterResource(R.drawable.bookmark_empty_24)
            }
        ActionButton(
            icon = bookmarkIcon,
            count = activity.bookmarkCount,
            contentDescription = "Bookmark",
            onClick = onBookmarkClick,
            tint = Color(0xFFFF9800), // Orange color for bookmarks
        )
    }
}

@Composable
fun ActionButton(
    icon: Painter,
    count: Int,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Color.Gray,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = formatCount(count),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000 -> "${count / 1000}k"
        count > 0 -> count.toString()
        else -> "0"
    }
}
