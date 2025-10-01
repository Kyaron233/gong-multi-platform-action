package com.sky31.gongmultiplatform.ui.component.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.left_arrow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DrawerMenuItem(
    iconResource: DrawableResource? = null,
    name: String,
    click: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                click()
            }
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(iconResource !== null) {
            Icon(
                painter = painterResource(iconResource),
                contentDescription = "icon",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
            )
        }

        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
        )

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

        content()

        Icon(
            painter = painterResource(Res.drawable.left_arrow),
            contentDescription = "rightArrow",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(20.dp)
                .padding(start = 8.dp)
                .rotate(180f)
        )
    }
}