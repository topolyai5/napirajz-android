package hu.napirajz.android.screen.random

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import hu.napirajz.android.R
import hu.napirajz.android.icons.forwardingPainter
import hu.napirajz.android.icons.photoErrorPlaceholder
import hu.napirajz.android.icons.photoPlaceholder
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.screen.SmallImageItem
import hu.napirajz.android.viewmodel.FetchImageByIdViewModel
import hu.napirajz.android.viewmodel.FetchImageByIdViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomRajzItem(data: NapirajzData, view: String) {
    var showDetails by remember { mutableStateOf(false) }
    val fetchImageByIdViewModel: FetchImageByIdViewModel = viewModel(factory = FetchImageByIdViewModelFactory())
    Column(modifier = Modifier.clickable {
        showDetails = true
        fetchImageByIdViewModel.update(data.id.toLong())
    }) {
        if (view == "table") {
            SmallImageItem(data, false)
        } else {
            ListItem(
                headlineContent = { Text(data.cim) },
                supportingContent = { Text(data.datum.toString()) },
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.url)
                    .crossfade(true)
                    .build(),
                placeholder = forwardingPainter(
                    painter = photoPlaceholder(),
                ),
                error = forwardingPainter(
                    painter = photoErrorPlaceholder(),
                    colorFilter = ColorFilter.tint(Color.Unspecified)
                ),
                contentDescription = data.url,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
    if (showDetails) {
        ModalBottomSheet(onDismissRequest = {
            showDetails = false
        }) {
            val images = fetchImageByIdViewModel.items.collectAsState().value
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val context = LocalContext.current
                Text(data.cim, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, data.url)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        content = {
                            Text(stringResource(R.string.share_pic))
                        })
                    if (!data.lapUrl.isNullOrBlank() && data.url != data.lapUrl) {
                        OutlinedButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, data.lapUrl)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                            content = {
                                Text(stringResource(R.string.share_page))
                            })
                    }
                }
                if (images.isNotEmpty()) {
                    LazyColumn {
                        items(images.size) {
                            val image = images[it]
                            SmallImageItem(image, true)
                        }
                    }
                }
            }
        }
    }
}