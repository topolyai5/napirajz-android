package hu.napirajz.android.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.napirajz.android.R
import hu.napirajz.android.icons.listView
import hu.napirajz.android.icons.search
import hu.napirajz.android.icons.tableView
import hu.napirajz.android.screen.random.RandomRajzItem
import hu.napirajz.android.viewmodel.SearchImageViewModel
import hu.napirajz.android.viewmodel.SearchImageViewModelFactory

@Composable
fun SearchScreen() {
    val searchImageViewModel: SearchImageViewModel = viewModel(factory = SearchImageViewModelFactory())
    val data = searchImageViewModel.items.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var view by remember { mutableStateOf("table") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchImageViewModel.query.collectAsState().value ?: "",
            onValueChange = { searchImageViewModel.setQueryString(it) },
            label = { Text(text = stringResource(id = R.string.search_pic)) },
            trailingIcon = {
                Row {

                    IconButton(
                        onClick = {
                            searchImageViewModel.search()
                        },
                        content = {
                            Icon(search(), contentDescription = "search image")
                        })
                    IconButton(
                        onClick = {
                            if (view == "table") {
                                view = "list"
                            } else {
                                view = "table"
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

            }
        )
        if (data.isEmpty()) {
            Image(
                painterResource(R.drawable.search_background),
                contentDescription = "search background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn {
                items(data.size) {
                    val item = data[it]
                    RandomRajzItem(item, view)
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}