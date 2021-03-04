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
package com.example.androiddevchallenge.ui.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UiModel : ViewModel() {

    var currentScreen by mutableStateOf(PuppiesScreen() as ScreenData)
        private set

    private val backStack = arrayListOf<ScreenData>()

    val isRootScreen
        get() = backStack.isEmpty()

    private fun setScreen(screen: ScreenData) {
        backStack.clear()
        currentScreen = screen
    }

    fun addScreen(screen: ScreenData) {
        backStack.add(currentScreen)
        currentScreen = screen
    }

    fun closeScreen(): Boolean = if (backStack.size > 0) {
        currentScreen = backStack.removeLast()
        true
    } else false
}
