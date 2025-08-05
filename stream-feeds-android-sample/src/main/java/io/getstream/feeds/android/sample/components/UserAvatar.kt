package io.getstream.feeds.android.sample.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest

@Composable
fun UserAvatar(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AsyncImage(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape),
        model = ImageRequest.Builder(context)
            .data(avatarUrl)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
    )
}