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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.feeds.android.sample.R

@Composable
fun ContentContextMenuDialog(
    title: String,
    showEdit: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (showEdit) {
                    MenuItemRow(iconRes = R.drawable.edit_24, text = "Edit", onClick = onEdit)

                    // Divider between Edit and Delete
                    HorizontalDivider(thickness = 1.dp)
                }

                MenuItemRow(
                    iconRes = R.drawable.delete_24,
                    text = "Delete",
                    onClick = onDelete,
                    tint = MaterialTheme.colorScheme.error,
                    textColor = MaterialTheme.colorScheme.error,
                )
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@Composable
private fun MenuItemRow(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    tint: Color = Color.Unspecified,
    textColor: Color = Color.Unspecified,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = text,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
