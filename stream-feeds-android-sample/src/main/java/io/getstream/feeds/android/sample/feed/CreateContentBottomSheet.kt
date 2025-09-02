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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContentBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onPost: (text: String, attachments: List<Uri>) -> Unit,
    requireText: Boolean,
    extraActions: @Composable RowScope.() -> Unit = {},
) {
    var postText by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically,
            ) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Companion.Bold)

                val canPost = attachments.isNotEmpty() && !requireText || postText.isNotBlank()
                TextButton(onClick = { onPost(postText, attachments) }, enabled = canPost) {
                    Text(text = "Submit", fontWeight = FontWeight.Companion.Medium)
                }
            }

            // Text Input
            OutlinedTextField(
                value = postText,
                onValueChange = { postText = it },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                minLines = 3,
                maxLines = 6,
            )

            // Bottom toolbar with image attachment option
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val hasAttachments = attachments.isNotEmpty()

                AttachmentButton(
                    hasAttachment = hasAttachments,
                    onAttachmentsSelected = { uris -> attachments = uris },
                )

                if (hasAttachments) {
                    Text(
                        text = "Attachment selected",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                // Display any extra actions passed to the bottom sheet
                extraActions()
            }
        }
    }
}

@Composable
private fun AttachmentButton(hasAttachment: Boolean, onAttachmentsSelected: (List<Uri>) -> Unit) {
    val activityLauncher =
        rememberLauncherForActivityResult(PickMultipleVisualMedia(), onAttachmentsSelected)

    IconButton(
        onClick = {
            activityLauncher.launch(PickVisualMediaRequest(mediaType = PickVisualMedia.ImageOnly))
        }
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_gallery),
            contentDescription = "Add Image/Video",
            tint = if (hasAttachment) MaterialTheme.colorScheme.onSurface else Color.Unspecified,
            modifier = Modifier.size(24.dp),
        )
    }
}
