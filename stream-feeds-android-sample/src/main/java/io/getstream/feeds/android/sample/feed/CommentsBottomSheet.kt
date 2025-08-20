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

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.sample.R
import io.getstream.feeds.android.sample.ui.theme.LighterGray
import io.getstream.feeds.android.sample.ui.util.ScrolledToBottomEffect
import io.getstream.feeds.android.sample.ui.util.rippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(viewModel: CommentsSheetViewModel, onDismiss: () -> Unit) {
    val comments by viewModel.state.comments.collectAsStateWithLifecycle()
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismiss, sheetState = state) {
        CommentsBottomSheetContent(
            comments = comments,
            onLoadMore = viewModel::onLoadMore,
            onLikeClick = viewModel::onLikeClick,
            onPostComment = viewModel::onPostComment,
        )
    }
}

@Composable
private fun ColumnScope.CommentsBottomSheetContent(
    comments: List<ThreadedCommentData>,
    onLoadMore: () -> Unit,
    onLikeClick: (ThreadedCommentData) -> Unit,
    onPostComment: (text: String, parentCommentId: String?, attachmentUris: List<Uri>) -> Unit,
) {
    var createCommentData: CreateCommentData? by remember { mutableStateOf(null) }
    var expandedCommentId: String? by remember { mutableStateOf(null) }

    Text(
        text = "Comments",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp),
    )

    Box(Modifier.fillMaxWidth().defaultMinSize(minHeight = 300.dp)) {
        val lazyListState = rememberLazyListState()

        ScrolledToBottomEffect(lazyListState, action = onLoadMore)

        LazyColumn(state = lazyListState) {
            items(comments) { comment ->
                Comment(
                    data = comment,
                    isExpanded = comment.id == expandedCommentId,
                    onExpandClick = {
                        expandedCommentId = comment.id.takeUnless { it == expandedCommentId }
                    },
                    onReplyClick = { commentId ->
                        createCommentData = CreateCommentData(commentId)
                    },
                    onLikeClick = onLikeClick,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // Spacer to ensure the FAB doesn't cover the last comment
            item { Spacer(Modifier.height(64.dp)) }
        }

        FloatingActionButton(
            onClick = { createCommentData = CreateCommentData() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Comment",
                modifier = Modifier.size(24.dp),
            )
        }
    }

    if (createCommentData != null) {
        CreateContentBottomSheet(
            onDismiss = { createCommentData = null },
            onPost = { text, attachments ->
                onPostComment(text, createCommentData?.replyParentId, attachments)
                createCommentData = null
            },
        )
    }
}

@Composable
private fun Comment(
    data: ThreadedCommentData,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onReplyClick: (commentId: String) -> Unit,
    onLikeClick: (ThreadedCommentData) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedCommentId: String? by remember { mutableStateOf(null) }

    Column(modifier.width(IntrinsicSize.Max)) {
        Column(
            Modifier.background(LighterGray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(data.user.name.orEmpty(), fontWeight = FontWeight.SemiBold)
            Text(data.text.orEmpty())
        }

        Row(
            Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val hasOwnHeart = data.ownReactions.any { it.type == "heart" }

            Text(
                text = if (hasOwnHeart) "Unlike" else "Like",
                modifier =
                    Modifier.rippleClickable { onLikeClick(data) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Text(
                text = "Reply",
                modifier =
                    Modifier.rippleClickable { onReplyClick(data.id) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Spacer(Modifier.weight(1f))

            if (data.replyCount > 0) {
                Text(
                    text = "Replies (${data.replyCount}) ",
                    modifier =
                        Modifier.rippleClickable(onClick = onExpandClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            val heartId =
                if (hasOwnHeart) {
                    R.drawable.favorite_filled_24
                } else {
                    R.drawable.favorite_24
                }
            Icon(painter = painterResource(heartId), contentDescription = null)
            Text(
                text = data.reactionGroups["heart"]?.count?.toString() ?: "0",
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        AnimatedVisibility(isExpanded && !data.replies.isNullOrEmpty()) {
            Column {
                data.replies?.forEach { reply ->
                    Comment(
                        data = reply,
                        isExpanded = reply.id == expandedCommentId,
                        onExpandClick = {
                            expandedCommentId = reply.id.takeUnless { it == expandedCommentId }
                        },
                        onReplyClick = onReplyClick,
                        onLikeClick = onLikeClick,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp),
                    )
                }
            }
        }
    }
}

@JvmInline private value class CreateCommentData(val replyParentId: String? = null)
