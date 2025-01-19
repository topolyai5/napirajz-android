package hu.napirajz.android.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import hu.napirajz.android.icons.forwardingPainter
import hu.napirajz.android.icons.photoErrorPlaceholder
import hu.napirajz.android.icons.photoPlaceholder
import hu.napirajz.android.viewmodel.FetchImageByIdViewModel
import hu.napirajz.android.viewmodel.FetchImageByIdViewModelFactory

@Composable
fun ImageDetailsScreen(id: String?) {
    val fetchImageByIdViewModel: FetchImageByIdViewModel = viewModel(factory = FetchImageByIdViewModelFactory())
    if (id != null) {
        fetchImageByIdViewModel.update(id.toLong())
    } else {
        Text("Nem található azonositó.")
    }
    val images = fetchImageByIdViewModel.items.collectAsState().value
    val image = images.firstOrNull { it.id == id }
    if (image != null) {
        LazyColumn {
            item {
                Column {
                    ListItem(
                        headlineContent = { Text(image.cim) },
                        supportingContent = { Text(image.datum.toString()) },
                    )
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image.url)
                            .crossfade(true)
                            .build(),
                        placeholder = forwardingPainter(
                            painter = photoPlaceholder(),
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        ),
                        error = forwardingPainter(
                            painter = photoErrorPlaceholder(),
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        ),
                        contentDescription = image.url,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Párbeszéd", fontSize = 18.sp)
                    Text(image.parbeszed)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Egyéb", fontSize = 18.sp)
                    Text(image.egyeb)
                }
            }

        }
    }
}