package hu.napirajz.android.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import hu.napirajz.android.activity.LocalNavHostController
import hu.napirajz.android.icons.forwardingPainter
import hu.napirajz.android.icons.photoErrorPlaceholder
import hu.napirajz.android.icons.photoPlaceholder
import hu.napirajz.android.response.NapirajzData

@Composable
fun SmallImageItem(data: NapirajzData, clickable: Boolean) {
    val navHostController = LocalNavHostController.current
    ListItem(
        modifier = if (clickable) Modifier.clickable { navHostController.navigate("details/${data.id}") } else Modifier,
        headlineContent = { Text(data.cim) },
        supportingContent = { Text(data.datum.toString()) },
        leadingContent = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.url)
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
                contentDescription = data.url,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    )

}