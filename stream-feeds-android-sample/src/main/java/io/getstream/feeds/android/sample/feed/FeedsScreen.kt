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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import io.getstream.feeds.android.network.models.NotificationStatusResponse
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

    Scaffold(
        topBar = {
            val notificationStatus by viewModel.notificationStatus.collectAsStateWithLifecycle()
            TopBarSection(notificationStatus, navigator, args)
        },
        snackbarHost = {
            val snackbarHostState = remember(::SnackbarHostState)

            LaunchedEffect(Unit) { viewModel.error.collect { snackbarHostState.showSnackbar(it) } }

            SnackbarHost(snackbarHostState)
        },
    ) { padding ->
        when (val state = state) {
            AsyncResource.Loading -> LoadingScreen(Modifier.padding(padding))

            AsyncResource.Error -> {
                LaunchedEffect(Unit) { navigator.popBackStack() }
                return@Scaffold
            }

            is AsyncResource.Content ->
                FeedsScreenContent(
                    args,
                    navigator,
                    state.data,
                    viewModel,
                    Modifier.padding(padding),
                )
        }
    }
}

@Composable
private fun FeedsScreenContent(
    args: FeedsScreenArgs,
    navigator: DestinationsNavigator,
    state: FeedState,
    viewModel: FeedViewModel,
    modifier: Modifier,
) {
    var showCreatePostBottomSheet by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(16.dp),
                            ) {
                                UserAvatar(activity.user.image, Modifier.size(24.dp))

                                val name = activity.user.name ?: activity.user.id
                                val repostText = activity.text?.let { ": $it" }.orEmpty()
                                Text(
                                    text = "$name reposted$repostText",
                                    fontStyle = FontStyle.Italic,
                                )
                            }
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

            // Create Post Bottom Sheet
            if (showCreatePostBottomSheet) {
                CreateContentBottomSheet(
                    title = "Create post",
                    onDismiss = { showCreatePostBottomSheet = false },
                    onPost = { postText, attachments ->
                        showCreatePostBottomSheet = false
                        viewModel.onCreatePost(postText, attachments)
                    },
                    requireText = false,
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
private fun TopBarSection(
    notificationStatus: NotificationStatusResponse?,
    navigator: DestinationsNavigator,
    args: FeedsScreenArgs,
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    FeedsScreenToolbar(
        avatarUrl = args.avatarUrl,
        hasUnseenNotifications = (notificationStatus?.unseen ?: 0) > 0,
        onUserAvatarClick = { showLogoutConfirmation = true },
        onNotificationsClick = {
            // Open notifications screen
            val fid = FeedId("notification", args.userId)
            navigator.navigate(
                NotificationsScreenDestination(NotificationsScreenArgs(fid.rawValue))
            )
        },
        onProfileClick = { navigator.navigate(ProfileScreenDestination(feedId = args.feedId)) },
    )

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedsScreenToolbar(
    avatarUrl: String?,
    hasUnseenNotifications: Boolean,
    onUserAvatarClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Stream Feeds") },
        navigationIcon = {
            UserAvatar(
                avatarUrl = avatarUrl,
                modifier =
                    Modifier.padding(start = 8.dp)
                        .size(36.dp)
                        .clickable(onClick = onUserAvatarClick),
            )
        },
        actions = {
            NotificationIcon(hasUnseen = hasUnseenNotifications, onClick = onNotificationsClick)
            ProfileIcon(onClick = onProfileClick)
        },
    )
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
        ImageAttachmentsSection(attachments)

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
            ContentContextMenuDialog(
                title = "Post Options",
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
            EditContentDialog(
                initialText = text,
                onDismiss = { showEditDialog = false },
                onSave = { editedText ->
                    onEditSave(editedText)
                    showEditDialog = false
                },
            )
        }

        // Add divider between activities for better separation
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), thickness = 1.dp)
    }
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
