package io.getstream.feeds.android.sample.feed

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.core.generated.models.Attachment
import io.getstream.feeds.android.sample.R
import io.getstream.feeds.android.sample.components.LinkText
import io.getstream.feeds.android.sample.components.UserAvatar
import io.getstream.feeds.android.sample.profile.ProfileScreenArgs
import io.getstream.feeds.android.sample.ui.util.ScrolledToBottomEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedsScreen(
    fid: FeedId,
    feedsClient: FeedsClient,
    avatarUrl: String?,
    currentUserId: String,
    navigator: DestinationsNavigator,
    onLogout: () -> Unit,
    viewModel: FeedViewModel = viewModel(factory = feedViewModelFactory(currentUserId, fid, feedsClient))
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var showCreatePostBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        // Toolbar
        FeedsScreenToolbar(
            avatarUrl = avatarUrl,
            onUserAvatarClick = { showLogoutConfirmation = true },
            onNotificationsClick = {},
            onProfileClick = {
                navigator.navigate(ProfileScreenDestination(ProfileScreenArgs(feedId = fid.rawValue)))
            }
        )

        // Toolbar divider
        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.1f),
            thickness = 1.dp
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // Feed content
            val activities by viewModel.state.activities.collectAsStateWithLifecycle()
            val listState = rememberLazyListState()

            ScrolledToBottomEffect(listState, action = viewModel::onLoadMore)

            LazyColumn(state = listState) {
                items(activities) { activity ->
                    if (activity.parent != null) {
                        val repostText = activity.text?.let { ": $it" }.orEmpty()
                        Text(
                            text = "${activity.user.name ?: activity.user.id} reposted$repostText",
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(16.dp),
                        )
                    }

                    val baseActivity = activity.parent ?: activity
                    ActivityContent(
                        feedsClient = feedsClient,
                        feedId = fid,
                        user = baseActivity.user,
                        text = baseActivity.text.orEmpty(),
                        attachments = baseActivity.attachments,
                        data = activity,
                        currentUserId = currentUserId,
                        onHeartClick = { viewModel.onHeartClick(activity) },
                        onRepostClick = { message -> viewModel.onRepostClick(activity, message) },
                        onBookmarkClick = { viewModel.onBookmarkClick(activity) },
                        onDeleteClick = { viewModel.onDeleteClick(activity.id) },
                        onEditSave = { viewModel.onEditActivity(activity.id, it) },
                        pollSection = { poll -> PollSection(activity.id, poll, viewModel.pollController) }
                    )
                }
            }

            FloatingActionButton(
                onClick = { showCreatePostBottomSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = CircleShape,
                containerColor = Color.Blue,
                contentColor = Color.White,
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_24),
                    contentDescription = "Add Activity",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (showLogoutConfirmation) {
            LogoutConfirmationDialog(
                onDismiss = { showLogoutConfirmation = false },
                onConfirm = {
                    showLogoutConfirmation = false
                    onLogout()
                }
            )
        }

        // Create Post Bottom Sheet
        if (showCreatePostBottomSheet) {
            CreateContentBottomSheet(
                onDismiss = { showCreatePostBottomSheet = false },
                onPost = { postText, attachments ->
                    showCreatePostBottomSheet = false
                    viewModel.onCreatePost(postText, attachments)
                }
            )
        }
    }
}

@Composable
fun FeedsScreenToolbar(
    avatarUrl: String?,
    onUserAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left side - Avatar
        UserAvatar(
            avatarUrl = avatarUrl,
            modifier = Modifier
                .size(36.dp)
                .clickable(onClick = onUserAvatarClick)
        )

        // Center - Title (using weight to ensure perfect centering)
        Text(
            text = "Stream Feeds",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        // Right side - Action buttons
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.notifications_24),
                    contentDescription = "Notifications",
                    tint = Color(0xFF1976D2), // Material Blue 700
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.profile_24),
                    contentDescription = "Profile",
                    tint = Color(0xFF1976D2), // Material Blue 700
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityContent(
    feedsClient: FeedsClient,
    feedId: FeedId,
    user: UserData,
    text: String,
    attachments: List<Attachment>,
    data: ActivityData,
    currentUserId: String,
    onHeartClick: () -> Unit,
    onRepostClick: (String?) -> Unit,
    onBookmarkClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditSave: ((String) -> Unit),
    pollSection: @Composable (PollData) -> Unit,
) {
    var showCommentsBottomSheet by remember { mutableStateOf(false) }
    var showRepostDialog by remember { mutableStateOf(false) }
    var showContextMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    // Check if current user is the author
    val isCurrentUserAuthor = data.user.id == currentUserId
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                indication = null,
                interactionSource = null,
                onClick = { /* Regular click - do nothing */ },
                onLongClick = if (isCurrentUserAuthor) {
                    {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        showContextMenu = true
                    }
                } else null
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            UserAvatar(
                avatarUrl = user.image,
                modifier = Modifier.size(40.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = user.name ?: user.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (text.isNotBlank()) {
                    LinkText(
                        text = text,
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.8f),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
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
            onCommentClick = { showCommentsBottomSheet = true },
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
                }
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
                }
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
                }
            )
        }

        if (showCommentsBottomSheet) {
            val context = LocalContext.current
            CommentsBottomSheet(
                // TODO [G.] migrate to Compose navigation
                viewModel = remember {
                    CommentsSheetViewModel
                        .Factory(
                            feedsClient.activity(activityId = data.id, fid = feedId),
                            context.applicationContext
                        )
                        .create(CommentsSheetViewModel::class.java)
                },
                onDismiss = { showCommentsBottomSheet = false },
            )
        }
        // Add divider between activities for better separation
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 1.dp
        )
    }
}

@Composable
private fun AttachmentsSection(attachments: List<Attachment>) {
    if (attachments.size == 1) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(12.dp)),
            model = attachments.first().assetUrl,
            contentDescription = "Activity image",
            contentScale = ContentScale.Crop,
        )
    } else if (attachments.size > 1) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(attachments) { attachment ->
                AsyncImage(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
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
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Post Options",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (showEdit) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEdit() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit_24),
                            contentDescription = "Edit",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Edit Post",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    // Divider between Edit and Delete
                    HorizontalDivider(
                        color = Color.Gray.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDelete() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24),
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Delete Post",
                        fontSize = 16.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun EditPostDialog(
    initialText: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var editText by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Post",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                minLines = 3
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(editText) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContentBottomSheet(
    onDismiss: () -> Unit,
    onPost: (text: String, attachments: List<Uri>) -> Unit
) {
    var postText by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create Post",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Post",
                    color = if (postText.isNotBlank()) Color.Blue else Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(
                        enabled = postText.isNotBlank()
                    ) {
                        onPost(postText, attachments)
                    }
                )
            }

            // Text Input
            OutlinedTextField(
                value = postText,
                onValueChange = { postText = it },
                placeholder = { Text("Add post") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                minLines = 3,
                maxLines = 6
            )

            // Bottom toolbar with image attachment option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hasAttachments = attachments.isNotEmpty()

                AttachmentButton(
                    hasAttachment = hasAttachments,
                    onAttachmentsSelected = { uris -> attachments = uris }
                )

                if (hasAttachments) {
                    Text(
                        text = "Attachment selected",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentButton(hasAttachment: Boolean, onAttachmentsSelected: (List<Uri>) -> Unit) {
    val activityLauncher = rememberLauncherForActivityResult(PickMultipleVisualMedia(), onAttachmentsSelected)

    IconButton(
        onClick = {
            activityLauncher.launch(PickVisualMediaRequest(mediaType = PickVisualMedia.ImageOnly))
        }
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_gallery),
            contentDescription = "Add Image/Video",
            tint = if (hasAttachment) Color.Blue else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun LogoutConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Logout") },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
