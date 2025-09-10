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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.sample.components.LoadingScreen
import io.getstream.feeds.android.sample.feed.CommentsSheetViewModel.Event
import io.getstream.feeds.android.sample.ui.util.ScrolledToBottomEffect
import io.getstream.feeds.android.sample.ui.util.conditional
import io.getstream.feeds.android.sample.ui.util.rippleClickable
import io.getstream.feeds.android.sample.util.AsyncResource

data class CommentsSheetArgs(val feedId: String, val activityId: String) {
    val fid = FeedId(feedId)
}

@Destination<RootGraph>(
    style = DestinationStyleBottomSheet::class,
    navArgs = CommentsSheetArgs::class,
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(navigator: DestinationsNavigator) {
    val viewModel = hiltViewModel<CommentsSheetViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface {
        when (val state = state) {
            AsyncResource.Loading -> LoadingScreen()

            AsyncResource.Error -> {
                LaunchedEffect(Unit) { navigator.popBackStack() }
                return@Surface
            }

            is AsyncResource.Content -> {
                val comments by state.data.second.comments.collectAsStateWithLifecycle()
                val currentUserId = state.data.first.id
                val createContentState by viewModel.createContentState.collectAsStateWithLifecycle()

                Column {
                    CommentsBottomSheetContent(
                        comments = comments,
                        currentUserId = currentUserId,
                        createContentState = createContentState,
                        onEvent = viewModel::onEvent,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CommentsBottomSheetContent(
    comments: List<ThreadedCommentData>,
    currentUserId: String,
    createContentState: CreateContentState,
    onEvent: (Event) -> Unit,
) {
    var expandedCommentId: String? by remember { mutableStateOf(null) }

    Text(
        text = "Comments",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
    )

    Box(Modifier.fillMaxWidth().defaultMinSize(minHeight = 300.dp)) {
        val lazyListState = rememberLazyListState()

        ScrolledToBottomEffect(lazyListState, action = { onEvent(Event.OnScrollToBottom) })

        LazyColumn(state = lazyListState) {
            items(comments) { comment ->
                Comment(
                    data = comment,
                    currentUserId = currentUserId,
                    isExpanded = comment.id == expandedCommentId,
                    onExpandClick = {
                        expandedCommentId = comment.id.takeUnless { it == expandedCommentId }
                    },
                    onReplyClick = { commentId -> onEvent(Event.OnReply(commentId)) },
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // Spacer to ensure the FAB doesn't cover the last comment
            item { Spacer(Modifier.height(64.dp)) }
        }

        FloatingActionButton(
            onClick = { onEvent(Event.OnAddContent) },
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

    CreateContentBottomSheet(
        state = createContentState,
        config =
            ContentConfig.Comment { text, attachments -> onEvent(Event.OnPost(text, attachments)) },
        onDismiss = { onEvent(Event.OnContentCreateDismiss) },
    )
}

@Composable
private fun Comment(
    data: ThreadedCommentData,
    currentUserId: String,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onReplyClick: (commentId: String) -> Unit,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedCommentId: String? by remember { mutableStateOf(null) }
    var showContextMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val hapticFeedback = LocalHapticFeedback.current

    Column(modifier.fillMaxWidth()) {
        Column(
            Modifier.background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp),
                )
                .conditional(data.user.id == currentUserId) {
                    combinedClickable(
                        indication = null,
                        interactionSource = null,
                        onClick = {},
                        onLongClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            showContextMenu = true
                        },
                    )
                }
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(data.user.name.orEmpty(), fontWeight = FontWeight.SemiBold)
            Text(data.text.orEmpty())
            ImageAttachmentsSection(data.attachments.orEmpty())
        }

        Row(
            Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val hasOwnHeart = data.ownReactions.any { it.type == "heart" }

            Text(
                text = if (hasOwnHeart) "Unlike" else "Like",
                modifier =
                    Modifier.rippleClickable { onEvent(Event.OnLike(data)) }
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

            Icon(painter = Reaction.Heart.painter(hasOwnHeart), contentDescription = null)
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
                        currentUserId = currentUserId,
                        isExpanded = reply.id == expandedCommentId,
                        onExpandClick = {
                            expandedCommentId = reply.id.takeUnless { it == expandedCommentId }
                        },
                        onReplyClick = onReplyClick,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp),
                    )
                }
            }
        }

        // Context menu dialog for long press
        if (showContextMenu) {
            ContentContextMenuDialog(
                title = "Comment Options",
                showEdit = true,
                onDismiss = { showContextMenu = false },
                onEdit = {
                    showEditDialog = true
                    showContextMenu = false
                },
                onDelete = {
                    onEvent(Event.OnDelete(data.id))
                    showContextMenu = false
                },
            )
        }

        // Edit comment dialog
        if (showEditDialog) {
            EditContentDialog(
                data.text.orEmpty(),
                onDismiss = { showEditDialog = false },
                onSave = { newText ->
                    onEvent(Event.OnEdit(commentId = data.id, text = newText))
                    showEditDialog = false
                },
            )
        }
    }
}
