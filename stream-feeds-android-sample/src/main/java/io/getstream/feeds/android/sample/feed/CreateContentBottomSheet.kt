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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CreateContentState {
    Hidden,
    Composing,
    Posting,
}

sealed class ContentConfig(
    val title: String,
    val requireText: Boolean,
    val showStoryToggle: Boolean,
) {
    data class Post(
        val onSubmit: (text: String, attachments: List<Uri>, isStory: Boolean) -> Unit
    ) : ContentConfig(title = "Create Post", requireText = false, showStoryToggle = true)

    data class Comment(val onSubmit: (text: String, attachments: List<Uri>) -> Unit) :
        ContentConfig(title = "Add Comment", requireText = true, showStoryToggle = false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContentBottomSheet(
    state: CreateContentState,
    config: ContentConfig,
    onDismiss: () -> Unit,
) {
    if (state == CreateContentState.Hidden) return

    val state by rememberUpdatedState(state)
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            // Do not allow hiding while posting
            confirmValueChange = { it != SheetValue.Hidden || state != CreateContentState.Posting },
        )
    val inputEnabled = state == CreateContentState.Composing

    var text by rememberSaveable { mutableStateOf("") }
    var attachments by rememberSaveable { mutableStateOf(emptyList<Uri>()) }
    var isStory by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically,
            ) {
                Text(text = config.title, fontSize = 18.sp, fontWeight = FontWeight.Companion.Bold)

                if (state == CreateContentState.Posting) {
                    CircularProgressIndicator()
                } else {
                    val canPost =
                        attachments.isNotEmpty() && !config.requireText || text.isNotBlank()
                    TextButton(
                        onClick = {
                            when (config) {
                                is ContentConfig.Post -> config.onSubmit(text, attachments, isStory)

                                is ContentConfig.Comment -> config.onSubmit(text, attachments)
                            }
                        },
                        enabled = canPost,
                    ) {
                        Text(text = "Submit", fontWeight = FontWeight.Companion.Medium)
                    }
                }
            }

            // Text Input
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                enabled = inputEnabled,
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                minLines = 3,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )

            // Bottom toolbar with options
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val hasAttachments = attachments.isNotEmpty()

                AttachmentButton(
                    hasAttachment = hasAttachments,
                    onAttachmentsSelected = { uris -> attachments = uris },
                    enabled = inputEnabled,
                )

                if (config.showStoryToggle) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Post as story",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Switch(
                        checked = isStory,
                        onCheckedChange = { isStory = it },
                        enabled = inputEnabled,
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentButton(
    hasAttachment: Boolean,
    onAttachmentsSelected: (List<Uri>) -> Unit,
    enabled: Boolean,
) {
    val activityLauncher =
        rememberLauncherForActivityResult(PickMultipleVisualMedia(), onAttachmentsSelected)

    IconButton(
        onClick = {
            activityLauncher.launch(PickVisualMediaRequest(mediaType = PickVisualMedia.ImageOnly))
        },
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_gallery),
            contentDescription = "Add Image/Video",
            tint = if (hasAttachment) MaterialTheme.colorScheme.onSurface else Color.Unspecified,
            modifier = Modifier.size(24.dp),
        )
    }
}
