package hu.napirajz.android.screen.random

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.napirajz.android.R
import hu.napirajz.android.icons.listView
import hu.napirajz.android.icons.tableView
import hu.napirajz.android.view.EndlessLazyColumn
import hu.napirajz.android.viewmodel.LoadImageViewModel
import hu.napirajz.android.viewmodel.LoadImageViewModelFactory

@Composable
fun RandomRajzScreen() {
    val loadImageViewModel: LoadImageViewModel = viewModel(factory = LoadImageViewModelFactory(rememberLazyListState()))
    val data = loadImageViewModel.items.collectAsState().value
    if (data.isEmpty()) {
        loadImageViewModel.getNewRandom(count = 12)
    }
    val listState = loadImageViewModel.lazyListState
    val view = loadImageViewModel.view.collectAsState().value
    Column(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = {
                Text(stringResource(R.string.scroll_to_more_image), fontSize = 16.sp)
            },
            trailingContent = {
                IconButton(
                    onClick = {
                        if (view == "table") {
                            loadImageViewModel.changeView("list")
                        } else {
                            loadImageViewModel.changeView("table")
                        }
                    },
                    content = {
                        if (view == "table") {
                            Icon(listView(), contentDescription = "list view")
                        } else {
                            Icon(tableView(), contentDescription = "table view")
                        }
                    })
            }
        )
//        val listState = rememberLazyListState()
//        LaunchedEffect(listState) {
//            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index?:0 }.collect {
//                loadImageViewModel.lastVisible(it)
//            }
//        }
        EndlessLazyColumn(
            items = data,
            itemKey = { item, index -> "${item.id}$index" },
            itemContent = {
                RandomRajzItem(it, view)
            },
            listState = listState,
            loadMore = {
                loadImageViewModel.getNewRandom(count = 4)
            },
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            OutlinedButton(onClick = { loadImageViewModel.getNewRandom(count = 4) }) {
                Text("Még több rajzot kérek")
            }
        }
    }
}