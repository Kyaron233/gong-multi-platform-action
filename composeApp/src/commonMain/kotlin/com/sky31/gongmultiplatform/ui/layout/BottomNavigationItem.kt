package com.sky31.gongmultiplatform.ui.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomNavigationItem(
    resource: DrawableResource,
    title: String,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                onClick()
            }
    ) {
        Icon(
            painter = painterResource(resource),
            contentDescription = "home",
            modifier = Modifier
                .size(20.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White
        )
    }
}