package io.getstream.feeds.android.sample.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.sample.ui.util.ScrolledToBottomEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    viewModel: CommentsSheetViewModel,
    onDismiss: () -> Unit,
) {
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
    onPostComment: (text: String, parentCommentId: String?) -> Unit,
) {
    var createCommentData: CreateCommentData? by remember { mutableStateOf(null) }
    var expandedCommentId: String? by remember { mutableStateOf(null) }

    Text(
        text = "Comments",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )

    Box(Modifier.fillMaxWidth()) {
        val lazyListState = rememberLazyListState()

        ScrolledToBottomEffect(lazyListState, action = onLoadMore)

        LazyColumn(state = lazyListState) {
            items(comments) { comment ->
                Comment(
                    data = comment,
                    isExpanded = comment.id == expandedCommentId,
                    onExpandClick = { expandedCommentId = comment.id.takeUnless { it == expandedCommentId } },
                    onReplyClick = { commentId -> createCommentData = CreateCommentData(commentId) },
                    onLikeClick = onLikeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = { createCommentData = CreateCommentData() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
            containerColor = Color.Blue,
            contentColor = Color.White,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Comment",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (createCommentData != null) {
        CreateContentBottomSheet(
            onDismiss = { createCommentData = null },
            onPost = { text, attachments ->
                onPostComment(text, createCommentData?.replyParentId)
                createCommentData = null
            }
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
    modifier: Modifier = Modifier
) {
    var expandedCommentId: String? by remember { mutableStateOf(null) }

    Column(modifier.width(IntrinsicSize.Max)) {
        Column(
            Modifier
                .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(data.user.name.orEmpty(), fontWeight = FontWeight.SemiBold)
            Text(data.text.orEmpty())
        }

        Row(
            Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val hasOwnHeart = data.ownReactions.any { it.type == "heart" }

            Text(
                text = "Like",
                modifier = Modifier.clickable { onLikeClick(data) },
                color = if (hasOwnHeart) Color.Red else Color.Unspecified
            )
            Text(
                text = "Reply",
                modifier = Modifier.clickable(onClick = { onReplyClick(data.id) })
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Replies (${data.replyCount}) ",
                modifier = Modifier.clickable(onClick = onExpandClick),
            )
        }

        AnimatedVisibility(isExpanded && !data.replies.isNullOrEmpty()) {
            Column {
                data.replies?.forEach { reply ->
                    Comment(
                        data = reply,
                        isExpanded = reply.id == expandedCommentId,
                        onExpandClick = { expandedCommentId = reply.id.takeUnless { it == expandedCommentId } },
                        onReplyClick = onReplyClick,
                        onLikeClick = onLikeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp)
                    )
                }
            }
        }
    }
}

@JvmInline
private value class CreateCommentData(
    val replyParentId: String? = null,
)
