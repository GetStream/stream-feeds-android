package io.getstream.feeds.android.sample.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import io.getstream.feeds.android.client.BuildConfig
import io.getstream.feeds.android.sample.R

@Composable
fun LoginScreen(
    credentials: List<UserCredentials> = UserCredentials.BuiltIn,
    onCredentialsSelected: (UserCredentials) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Image(
            modifier = Modifier.size(width = 80.dp, height = 40.dp),
            painter = painterResource(R.drawable.ic_stream),
            contentDescription = null,
        )

        Spacer(Modifier.height(12.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.user_login_screen_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Spacer(Modifier.height(12.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.user_login_screen_subtitle),
            fontSize = 14.sp,
            color = Color.DarkGray,
        )

        Spacer(Modifier.height(20.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(credentials) { item ->
                UserLoginItem(item, onCredentialsSelected)
                DividerItem()
            }
        }

        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.sdk_version_template, BuildConfig.PRODUCT_VERSION),
            fontSize = 14.sp,
            color = Color.Gray,
        )
    }
}

@Composable
private fun UserLoginItem(
    credentials: UserCredentials,
    onClick: (UserCredentials) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                onClick = { onClick(credentials) },
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .data(credentials.user.imageURL)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = credentials.user.name ?: credentials.user.id,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Image(
            modifier = Modifier.wrapContentSize(),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }
}

@Composable
private fun DividerItem() {
    HorizontalDivider(
        thickness = 0.5.dp,
        color = Color.Gray,
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen {  }
}