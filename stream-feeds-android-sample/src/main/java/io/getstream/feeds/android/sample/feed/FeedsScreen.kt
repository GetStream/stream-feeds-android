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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.CommentsBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.generated.destinations.NotificationsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.network.models.Attachment
import io.getstream.feeds.android.sample.R
import io.getstream.feeds.android.sample.components.LinkText
import io.getstream.feeds.android.sample.components.LoadingScreen
import io.getstream.feeds.android.sample.components.UserAvatar
import io.getstream.feeds.android.sample.notification.NotificationsScreenArgs
import io.getstream.feeds.android.sample.ui.util.ScrolledToBottomEffect
import io.getstream.feeds.android.sample.util.AsyncResource

data class FeedsScreenArgs(val feedId: String, val avatarUrl: String?, val userId: String) {
    val fid = FeedId(feedId)
}

@Destination<RootGraph>(navArgs = FeedsScreenArgs::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedsScreen(args: FeedsScreenArgs, navigator: DestinationsNavigator) {
    val viewModel = hiltViewModel<FeedViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box {
        when (val state = state) {
            AsyncResource.Loading -> LoadingScreen()

            AsyncResource.Error -> {
                LaunchedEffect(Unit) { navigator.popBackStack() }
                return
            }

            is AsyncResource.Content -> FeedsScreenContent(args, navigator, state.data, viewModel)
        }

        val snackbarHostState = remember(::SnackbarHostState)

        LaunchedEffect(Unit) { viewModel.error.collect { snackbarHostState.showSnackbar(it) } }

        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun FeedsScreenContent(
    args: FeedsScreenArgs,
    navigator: DestinationsNavigator,
    state: FeedState,
    viewModel: FeedViewModel,
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var showCreatePostBottomSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        // Toolbar
        val notificationStatus by viewModel.notificationStatus.collectAsStateWithLifecycle()

        FeedsScreenToolbar(
            avatarUrl = args.avatarUrl,
            hasUnseenNotifications = (notificationStatus?.unseen ?: 0) > 0,
            onUserAvatarClick = { showLogoutConfirmation = true },
            onNotificationsClick = {
                // Open notifications screen ()
                val fid = FeedId("notification", args.userId)
                navigator.navigate(
                    NotificationsScreenDestination(NotificationsScreenArgs(fid.rawValue))
                )
            },
            onProfileClick = { navigator.navigate(ProfileScreenDestination(feedId = args.feedId)) },
        )

        // Toolbar divider
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), thickness = 1.dp)

        Box(modifier = Modifier.fillMaxSize()) {
            // Feed content
            val activities by state.activities.collectAsStateWithLifecycle()
            val listState = rememberLazyListState()

            if (activities.isEmpty()) {
                EmptyContent()
            } else {
                ScrolledToBottomEffect(listState, action = viewModel::onLoadMore)

                LazyColumn(state = listState) {
                    items(activities) { activity ->
                        if (activity.parent != null) {
                            val repostText = activity.text?.let { ": $it" }.orEmpty()
                            Text(
                                text =
                                    "${activity.user.name ?: activity.user.id} reposted$repostText",
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(16.dp),
                            )
                        }

                        val baseActivity = activity.parent ?: activity
                        ActivityContent(
                            user = baseActivity.user,
                            text = baseActivity.text.orEmpty(),
                            attachments = baseActivity.attachments,
                            data = activity,
                            currentUserId = args.userId,
                            onCommentClick = {
                                navigator.navigate(
                                    CommentsBottomSheetDestination(
                                        feedId = args.fid.rawValue,
                                        activityId = activity.id,
                                    )
                                )
                            },
                            onHeartClick = { viewModel.onHeartClick(activity) },
                            onRepostClick = { message ->
                                viewModel.onRepostClick(activity, message)
                            },
                            onBookmarkClick = { viewModel.onBookmarkClick(activity) },
                            onDeleteClick = { viewModel.onDeleteClick(activity.id) },
                            onEditSave = { viewModel.onEditActivity(activity.id, it) },
                            pollSection = { poll ->
                                PollSection(
                                    activityId = activity.id,
                                    currentUserId = args.userId,
                                    poll = poll,
                                    controller = viewModel.pollController,
                                    navigator = navigator,
                                )
                            },
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { showCreatePostBottomSheet = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    shape = CircleShape,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24),
                        contentDescription = "Add Activity",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            if (showLogoutConfirmation) {
                LogoutConfirmationDialog(
                    onDismiss = { showLogoutConfirmation = false },
                    onConfirm = {
                        showLogoutConfirmation = false
                        navigator.navigate(MainScreenDestination(logout = true)) {
                            popUpTo(NavGraphs.root) { inclusive = true }
                        }
                    },
                )
            }

            // Create Post Bottom Sheet
            if (showCreatePostBottomSheet) {
                CreateContentBottomSheet(
                    title = "Create post",
                    onDismiss = { showCreatePostBottomSheet = false },
                    onPost = { postText, attachments ->
                        showCreatePostBottomSheet = false
                        viewModel.onCreatePost(postText, attachments)
                    },
                    extraActions = {
                        CreatePollButton { formData ->
                            showCreatePostBottomSheet = false
                            viewModel.onCreatePoll(formData)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun FeedsScreenToolbar(
    avatarUrl: String?,
    hasUnseenNotifications: Boolean,
    onUserAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left side - Avatar
        UserAvatar(
            avatarUrl = avatarUrl,
            modifier = Modifier.size(36.dp).clickable(onClick = onUserAvatarClick),
        )

        // Center - Title (using weight to ensure perfect centering)
        Text(
            text = "Stream Feeds",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )

        // Right side - Action buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            NotificationIcon(hasUnseen = hasUnseenNotifications, onClick = onNotificationsClick)
            ProfileIcon(onClick = onProfileClick)
        }
    }
}

@Composable
private fun NotificationIcon(hasUnseen: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
        Box {
            Icon(
                painter = painterResource(R.drawable.notifications_24),
                contentDescription = "Notifications",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp),
            )
            // Red indicator for unseen notifications
            if (hasUnseen) {
                Box(
                    modifier =
                        Modifier.size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun ProfileIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
        Icon(
            painter = painterResource(R.drawable.profile_24),
            contentDescription = "Profile",
            tint = Color(0xFF1976D2), // Material Blue 700
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "No activities yet. Start by creating a post!",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityContent(
    user: UserData,
    text: String,
    attachments: List<Attachment>,
    data: ActivityData,
    currentUserId: String,
    onCommentClick: () -> Unit,
    onHeartClick: () -> Unit,
    onRepostClick: (String?) -> Unit,
    onBookmarkClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditSave: ((String) -> Unit),
    pollSection: @Composable (PollData) -> Unit,
) {
    var showRepostDialog by remember { mutableStateOf(false) }
    var showContextMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    // Check if current user is the author
    val isCurrentUserAuthor = data.user.id == currentUserId
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .combinedClickable(
                    indication = null,
                    interactionSource = null,
                    onClick = { /* Regular click - do nothing */ },
                    onLongClick =
                        if (isCurrentUserAuthor) {
                            {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                showContextMenu = true
                            }
                        } else null,
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            UserAvatar(avatarUrl = user.image)

            Column(modifier = Modifier.fillMaxWidth().padding(start = 12.dp)) {
                Text(
                    text = user.name ?: user.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp),
                )

                if (text.isNotBlank()) {
                    LinkText(
                        text = text,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                data.poll?.let { pollSection(it) }
            }
        }

        // Attachments - show below the content for better visual hierarchy
        AttachmentsSection(attachments)

        // Action buttons row
        ActivityActions(
            activity = data,
            onCommentClick = onCommentClick,
            onHeartClick = onHeartClick,
            onRepostClick = { showRepostDialog = true },
            onBookmarkClick = onBookmarkClick,
        )

        // Repost dialog
        if (showRepostDialog) {
            RepostDialog(
                onDismiss = { showRepostDialog = false },
                onConfirm = { message ->
                    onRepostClick(message)
                    showRepostDialog = false
                },
            )
        }

        // Context menu dialog for long press
        if (showContextMenu) {
            ActivityContextMenuDialog(
                showEdit = data.parent == null && data.poll == null,
                onDismiss = { showContextMenu = false },
                onEdit = {
                    showEditDialog = true
                    showContextMenu = false
                },
                onDelete = {
                    onDeleteClick()
                    showContextMenu = false
                },
            )
        }

        // Edit dialog
        if (showEditDialog) {
            EditPostDialog(
                initialText = text,
                onDismiss = { showEditDialog = false },
                onSave = { editedText ->
                    onEditSave(editedText)
                    showEditDialog = false
                },
            )
        }

        // Add divider between activities for better separation
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 1.dp,
        )
    }
}

@Composable
private fun AttachmentsSection(attachments: List<Attachment>) {
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

@Composable
fun ActivityContextMenuDialog(
    showEdit: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Post Options", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (showEdit) {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clickable { onEdit() }
                                .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit_24),
                            contentDescription = "Edit",
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = "Edit Post",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }

                    // Divider between Edit and Delete
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                }

                Row(
                    modifier =
                        Modifier.fillMaxWidth().clickable { onDelete() }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = "Delete Post",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@Composable
fun EditPostDialog(initialText: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var editText by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Post", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                minLines = 3,
            )
        },
        confirmButton = { Button(onClick = { onSave(editText) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun LogoutConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Logout") },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = { Button(onClick = onConfirm) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
