package hu.napirajz.android.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import hu.napirajz.android.extension.logInfo

@Composable
internal fun <T> EndlessLazyColumn(
    items: List<T>,
    itemKey: (T, Int) -> Any,
    itemContent: @Composable (T) -> Unit,
    loadMore: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    loadingItem: (@Composable () -> Unit)? = null,
    buffer: Int = 1
) {

    val reachedBottom: Boolean by remember { derivedStateOf { listState.reachedBottom(buffer) } }


    // load more if scrolled to bottom
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            loadMore()
        }
    }

    LazyColumn(modifier = modifier, state = listState) {
        items(
            items = items,
            key = { item: T -> itemKey(item, items.indexOf(item)) },
        ) { item ->
            itemContent(item)
        }

        if (loading && loadingItem != null) {
            logInfo("The bottom is reached. Loading indicator added", "EndlessLazyColumn")
            item {
                loadingItem()
            }
        }
    }
}

internal fun LazyListState.reachedBottom(buffer: Int): Boolean {
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    val reachedBottom = lastVisibleItem == this.layoutInfo.totalItemsCount - buffer
    logInfo("Check the bottom, is reached: $reachedBottom, buffer: $buffer, lastVisibleItem: ${lastVisibleItem}, totalItem: ${layoutInfo.totalItemsCount}", "EndlessLazyColumn")
    return reachedBottom
}