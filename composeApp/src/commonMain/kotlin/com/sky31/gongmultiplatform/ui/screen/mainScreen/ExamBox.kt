package com.sky31.gongmultiplatform.ui.screen.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky31.gongmultiplatform.model.ExamElem
import com.sky31.gongmultiplatform.util.CustomTime
import com.sky31.gongmultiplatform.util.customTimeToString
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.baseline_access_time_filled_24
import gongmultiplatform.composeapp.generated.resources.baseline_location_on_24
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.painterResource
import kotlin.time.ExperimentalTime

/**
 * mainScreen考试容器
 *
 * @param exam 考试数据
 * @param currentTime 当前时间
 */
@OptIn(ExperimentalTime::class)
@Composable
fun ExamBox(exam: ExamElem, currentTime: LocalDateTime) {
    val countdown by remember(currentTime, exam) {
        if (exam.startTime.isNotEmpty()) {
            val examInstant = LocalDateTime.parse(exam.startTime).toInstant(TimeZone.currentSystemDefault())
            val currentInstant = currentTime.toInstant(TimeZone.currentSystemDefault())

            mutableStateOf<Long?>(
                (examInstant - currentInstant).inWholeDays + 1
            )
        } else {
            mutableStateOf(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 5.dp, bottom = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(.6f),
                text = exam.name,
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .weight(.4f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                if (countdown != null) {
                    if (countdown!! > 0) {
                        Text(
                            text = "$countdown",
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "天",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else if (countdown!!.toInt() == 0) {
                        Text(
                            text = "今天",
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Text(
                            text = "已结束",
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_location_on_24),
                    contentDescription = "location",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(16.dp)
                )

                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = if (exam.location == "") "无地点安排" else exam.location,
                    fontSize = 12.sp,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_access_time_filled_24),
                    contentDescription = "time",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(16.dp)
                )

                if (exam.startTime.isNotEmpty()) {
                    val startTime = LocalDateTime.parse(exam.startTime)
                        .let { CustomTime(it.hour, it.minute) }
                    val endTime = LocalDateTime.parse(exam.endTime)
                        .let { CustomTime(it.hour, it.minute) }

                    Text(
                        text = "${customTimeToString(startTime)}-${
                            customTimeToString(
                                endTime
                            )
                        }",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "无时间安排",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}