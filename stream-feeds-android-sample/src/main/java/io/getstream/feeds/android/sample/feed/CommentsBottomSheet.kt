package io.getstream.feeds.android.sample.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

    ModalBottomSheet(onDismiss) {
        CommentsBottomSheetContent(
            comments = comments,
            onLoadMore = viewModel::onLoadMore,
            onPostComment = viewModel::onPostComment,
        )
    }
}

@Composable
private fun CommentsBottomSheetContent(
    comments: List<ThreadedCommentData>,
    onLoadMore: () -> Unit,
    onPostComment: (text: String) -> Unit,
) {
    var showCreateCommentBottomSheet by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        val lazyListState = rememberLazyListState()

        ScrolledToBottomEffect(lazyListState, action = onLoadMore)

        LazyColumn(state = lazyListState) {
            items(comments) { comment ->
                Comment(comment, Modifier.fillMaxWidth())
            }
        }

        FloatingActionButton(
            onClick = { showCreateCommentBottomSheet = true },
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

    if (showCreateCommentBottomSheet) {
        CreateContentBottomSheet(
            onDismiss = { showCreateCommentBottomSheet = false },
            onPost = { text ->
                showCreateCommentBottomSheet = false
                onPostComment(text)
            }
        )
    }
}

@Composable
private fun Comment(data: ThreadedCommentData, modifier: Modifier = Modifier) {
    Column(
        modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .width(IntrinsicSize.Max)
    ) {
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
            // TODO [G.] Implement actions
            Text("Like")
            Text("Reply")
            Spacer(Modifier.weight(1f))
            Text("Replies")
        }
    }
}
