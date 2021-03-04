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



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotStarted
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    if (model.isDone) AlertDialog(
        onDismissRequest = {
            model.isDone = false
        },
        title = {
            Text(text = "Title")
        },
        text = {
            Text("This area typically contains the supportive text ")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    model.isDone = false
                }
            ) {
                Text("Close")
            }
        }
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Timer", style = MaterialTheme.typography.h2)
                Row(modifier = Modifier.padding(top = 32.dp)) {
                    IconButton(onClick = {
                        model.isDone = true
                    }) {
                        Icon(Icons.Default.NotStarted, contentDescription = "Start")
                    }
                    IconButton(onClick = {
                        model.isDone = true
                    }) {
                        Icon(Icons.Default.NotStarted, contentDescription = "Start")
                    }
                    IconButton(onClick = {
                        model.isDone = true
                    }) {
                        Icon(Icons.Default.NotStarted, contentDescription = "Start")
                    }
                }
            }
        }
    }
}

