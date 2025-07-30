package io.getstream.feeds.android.sample.ui.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun ScrolledToBottomEffect(lazyListState: LazyListState, threshold: Int = 5, action: () -> Unit) {
    val shouldTrigger by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Check if we're within *threshold* items from the bottom
            totalItems > 0 && lastVisibleItem >= (totalItems - threshold)
        }
    }
    LaunchedEffect(shouldTrigger) {
        if (shouldTrigger) {
            action()
        }
    }
}
