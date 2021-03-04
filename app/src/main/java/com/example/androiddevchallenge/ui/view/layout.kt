/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.data.LayoutGridParams
import com.example.androiddevchallenge.ui.data.UiModel
import kotlin.math.max
import kotlin.math.min

const val XSMALL = "xsmall"
const val SMALL = "small"
const val MEDIUM = "medium"
const val LARGE = "large"
const val XLARGE = "xlarge"

val uiModel: UiModel
    @Composable
    get() = viewModel()

lateinit var layoutParams: LayoutGridParams

/**
 * Material Design layout params.
 */
fun getLayoutGridParams(dpWidth: Double): LayoutGridParams {
    val columns = when {
        dpWidth < 600 -> 4
        dpWidth < 840 -> 8
        else -> 12
    }
    val size = when {
        dpWidth < 600 -> XSMALL
        dpWidth < 1000 -> SMALL
        dpWidth < 1400 -> MEDIUM
        dpWidth < 1900 -> LARGE
        else -> XLARGE
    }
    return LayoutGridParams(size, columns, if (dpWidth < 720) 16 else 24, dpWidth / columns)
}

fun Modifier.workspaceSize() = fillMaxSize().run {
    if (layoutParams.screenType == XSMALL) this else {
        requiredWidth(
            min(
                layoutParams.cellSize * layoutParams.columnsAmount,
                min(max(600.0, layoutParams.cellSize * 6), 900.0)
            ).dp
        )
    }
}

fun Modifier.dialogSize() = requiredWidth(
    min(
        0.9 * layoutParams.cellSize * layoutParams.columnsAmount,
        if (layoutParams.screenType == XSMALL) 310.0 else 420.0
    ).dp
)

@Composable
fun MyApp() {
    val model = uiModel

    var isSetDialog by remember { mutableStateOf(false) }

//    val animatedProgress = when {
//        model.secondsCountdown == model.secondsToAlarm -> 0f
//        model.secondsToAlarm == 0 -> 1f
//        else -> 1f - model.secondsToAlarm.toFloat() / model.secondsCountdown.toFloat()
//    }

    val targetProgress = when {
        model.secondsCountdown == 0 -> 0f
        model.isDone -> 1f
        model.isRunning -> min(1f, 1f - (model.secondsToAlarm - 1f) / model.secondsCountdown.toFloat())
        model.secondsCountdown == model.secondsToAlarm -> 0f
        else -> 1f - model.secondsToAlarm.toFloat() / model.secondsCountdown.toFloat()
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = if (!model.isRunning) ProgressIndicatorDefaults.ProgressAnimationSpec else tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    if (model.isDone) AlertDialog(
        onDismissRequest = {
            model.isDone = false
        },
        title = {
            Text(text = stringResource(R.string.alarm_label))
        },
        text = {
            Text(text = stringResource(R.string.alarm_text))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    model.isDone = false
                }
            ) {
                Text(stringResource(R.string.close_label))
            }
        }
    ) else if (isSetDialog) CountdownSetDialog(
        seconds = model.secondsCountdown,
        cancelAction = {
            isSetDialog = false
        },
        doneAction = {
            model.secondsCountdown = it
            model.resetCountdown()
            isSetDialog = false
        }
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.workspaceSize().padding(32.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.requiredWidth(300.dp).offset(y = -(150).dp),
                progress = 1f,
                color = MaterialTheme.colors.primary.copy(alpha = 0.2f)
            )
            CircularProgressIndicator(
                modifier = Modifier.requiredWidth(300.dp).offset(y = -(150).dp),
                progress = animatedProgress
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val isAlarm = model.secondsToAlarm <= 0
                Text(
                    text = if (isAlarm) stringResource(R.string.alarm_label) else getTimeLabel(model.secondsToAlarm),
                    style = MaterialTheme.typography.h2
                )
                Row(modifier = Modifier.padding(top = 32.dp)) {
                    IconButton(
                        onClick = {
                            isSetDialog = true
                        }
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Set", tint = colorResource(R.color.primary))
                    }
                    Spacer(Modifier.width(16.dp))
                    IconButton(
                        onClick = {
                            model.resetCountdown()
                        }
                    ) {
                        Icon(Icons.Outlined.RestartAlt, contentDescription = "Reset", tint = colorResource(R.color.reset))
                    }
                    Spacer(Modifier.width(16.dp))
                    if (model.isRunning) {
                        IconButton(
                            onClick = {
                                model.stopCountdown()
                            },
                            enabled = !isAlarm
                        ) {
                            Icon(Icons.Outlined.PauseCircle, contentDescription = "Stop", tint = colorResource(if (isAlarm) R.color.gray else R.color.stop))
                        }
                    } else {
                        IconButton(
                            onClick = {
                                model.startCountdown()
                            },
                            enabled = !isAlarm
                        ) {
                            Icon(Icons.Outlined.PlayCircle, contentDescription = "Start", tint = colorResource(if (isAlarm) R.color.gray else R.color.start))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownSetDialog(seconds: Int, cancelAction: () -> Unit, doneAction: (Int) -> Unit) {
    Dialog(
        onDismissRequest = {
            cancelAction()
        }
    ) {
        Surface(modifier = Modifier.dialogSize(), shape = MaterialTheme.shapes.medium) {
            Column {
                val data = splitSeconds(seconds)
                var h by remember { mutableStateOf(data.first) }
                var m by remember { mutableStateOf(data.second) }
                var s by remember { mutableStateOf(data.third) }
                Column(modifier = Modifier.padding(16.dp)) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Text(text = stringResource(R.string.dialog_set_title), style = MaterialTheme.typography.h6)
                    }
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$h h", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Light)
                        Slider(
                            value = h.toFloat(),
                            onValueChange = { h = it.toInt() },
                            modifier = Modifier.fillMaxWidth(),
                            valueRange = 0f..100f
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(text = "$m m", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Light)
                        Slider(
                            value = m.toFloat(),
                            onValueChange = { m = it.toInt() },
                            modifier = Modifier.fillMaxWidth(),
                            valueRange = 0f..59f
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(text = "$s s", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Light)
                        Slider(
                            value = s.toFloat(),
                            onValueChange = { s = it.toInt() },
                            modifier = Modifier.fillMaxWidth(),
                            valueRange = 0f..59f
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                            cancelAction()
                        }
                    ) {
                        Text(stringResource(R.string.cancel_label))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            doneAction(3600 * h + 60 * m + s)
                        }
                    ) {
                        Text(stringResource(R.string.set_label))
                    }
                }
            }
        }
    }
}

private fun splitSeconds(seconds: Int) = Triple(
    (seconds / 3600).toInt(),
    ((seconds % 3600) / 60).toInt(),
    seconds % 60
)

private fun getTimeLabel(seconds: Int) = splitSeconds(seconds).let {
    "${it.first}:${it.second.toString().padStart(2, '0')}:${it.third.toString().padStart(2, '0')}"
}
