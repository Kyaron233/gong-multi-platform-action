package com.sky31.gongmultiplatform.ui.component.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sky31.gongmultiplatform.network.dto.UpdateDto
import com.sky31.gongmultiplatform.network.service.InstallService
import com.sky31.gongmultiplatform.ui.component.CustomScrollBox
import com.sky31.gongmultiplatform.ui.component.DialogState
import com.sky31.gongmultiplatform.ui.component.NotificationDialog
import com.sky31.gongmultiplatform.ui.component.rememberDialogState
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun UpdateNotificationDialog(
    state: DialogState = rememberDialogState(),
    data: UpdateDto = UpdateDto()
) {
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    NotificationDialog(
        state = state,
        modifier = Modifier
            .fillMaxWidth(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp),
                text = data.updateTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier
                    .padding(start = 15.dp, top = 10.dp, end = 15.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.lastVersion,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = data.updateDate,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            CustomScrollBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp)
                    .padding(5.dp)
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = data.updateNotice,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val y = 0f + strokeWidth / 2
                        drawLine(
                            color = colorScheme.background,   // 边框颜色
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            state.hide()
                        }
                        .padding(top = 10.dp, bottom = 10.dp),
                    text = "暂不更新",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(MaterialTheme.colorScheme.background)
                )

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            scope.launch {
                                InstallService.downloadApk(data.updateUrl)

                                InstallService.installApk()
                                state.hide()
                            }
                        }
                        .padding(top = 10.dp, bottom = 10.dp),
                    text = "下载安装",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}