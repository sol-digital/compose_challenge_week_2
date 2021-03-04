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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.data.LayoutGridParams
import com.example.androiddevchallenge.ui.data.PuppiesScreen
import com.example.androiddevchallenge.ui.data.PuppyDetailsScreen
import com.example.androiddevchallenge.ui.data.PuppyItem
import com.example.androiddevchallenge.ui.data.ScreenData
import com.example.androiddevchallenge.ui.data.UiModel
import com.example.androiddevchallenge.ui.data.puppiesData
import com.example.androiddevchallenge.ui.theme.getNavBgColor
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

@Composable
fun getVerticalScrollState(screenData: ScreenData) = rememberScrollState(
    remember { screenData.verticalScrollState?.value ?: 0 }
).also {
    screenData.verticalScrollState = it
}

@Composable
fun getHorizontalScrollState(screenData: ScreenData) = rememberScrollState(
    remember { screenData.horizontalScrollState?.value ?: 0 }
).also {
    screenData.horizontalScrollState = it
}

@Composable
fun getLazyListState(screenData: ScreenData) = rememberLazyListState(
    remember { screenData.lazyListState?.firstVisibleItemIndex ?: 0 },
    remember { screenData.lazyListState?.firstVisibleItemScrollOffset ?: 0 }
).also {
    screenData.lazyListState = it
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
    val screenState = model.currentScreen
    val isRoot = model.isRootScreen
    val backIcon = @Composable {
        IconButton(
            onClick = {
                model.closeScreen()
            }
        ) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.close_label)) }
    }
    Scaffold(
        backgroundColor = colorResource(R.color.background),
        topBar = {
            TopAppBar(
                navigationIcon = if (!isRoot) backIcon else null,
                backgroundColor = getNavBgColor(),
                title = {
                    Text(
                        text = when (screenState) {
                            is PuppiesScreen -> stringResource(R.string.puppy_list)
                            is PuppyDetailsScreen -> stringResource(R.string.puppy_details, screenState.data.title)
                            else -> stringResource(R.string.app_name)
                        },
                        maxLines = 1, overflow = TextOverflow.Ellipsis, softWrap = false
                    )
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .workspaceSize()
                .padding(innerPadding),
            elevation = if (layoutParams.screenType == XSMALL) 0.dp else 1.dp
        ) {
            Crossfade(targetState = screenState) {
                when (it) {
                    is PuppiesScreen -> DrawPuppies(it)
                    is PuppyDetailsScreen -> DrawPuppyDetails(it)
                }
            }
        }
    }
}

@Composable
fun DrawPuppies(screenData: ScreenData) {
    Surface {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = getLazyListState(screenData)
        ) {
            items(items = puppiesData) { item -> PuppyItem(item) }
        }
    }
}

@Composable
fun PuppyItem(item: PuppyItem) {
    val model = uiModel
    Column(
        modifier = Modifier.fillMaxWidth().clickable {
            model.addScreen(PuppyDetailsScreen(item))
        }
    ) {
        Row(modifier = Modifier.padding(vertical = 16.dp)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = item.title, style = MaterialTheme.typography.subtitle1)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = item.description, style = MaterialTheme.typography.body2)
                }
            }
        }
        Divider()
    }
}

@Composable
fun DrawPuppyDetails(screenData: PuppyDetailsScreen) {
    val puppy = screenData.data
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(getVerticalScrollState(screenData))) {
            Spacer(modifier = Modifier.fillMaxWidth().aspectRatio(2f))
            Surface {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = puppy.title, style = MaterialTheme.typography.h2)
                    Column(modifier = Modifier.padding(start = 4.dp, top = 8.dp)) {
                        Text(text = puppy.description, style = MaterialTheme.typography.subtitle1)
                        Spacer(Modifier.height(16.dp))
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(text = puppy.text, style = MaterialTheme.typography.body2)
                            Spacer(Modifier.height(8.dp))
                            Text(text = puppy.text, style = MaterialTheme.typography.body2)
                            Spacer(Modifier.height(8.dp))
                            Text(text = puppy.text, style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }
        }
    }
}
