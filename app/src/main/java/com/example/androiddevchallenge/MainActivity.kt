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
package com.example.androiddevchallenge

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.androiddevchallenge.ui.data.UiModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.view.MyApp
import com.example.androiddevchallenge.ui.view.getLayoutGridParams
import com.example.androiddevchallenge.ui.view.layoutParams

class MainActivity : AppCompatActivity() {

    val model: UiModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                layoutParams = getLayoutGridParams(maxWidth.value.toDouble())
                MyTheme {
                    MyApp()
                }
            }
        }
    }

    var backToast: Toast? = null

    override fun onBackPressed() {
        backToast?.cancel()

        val isHome = model.isRootScreen
        var needShowToast = isHome
        if (backToast != null) {
            backToast = null
            needShowToast = false
        }
        when {
            !model.isRootScreen -> model.closeScreen()
            needShowToast -> backToast = Toast.makeText(applicationContext, getString(R.string.toast_back_label), Toast.LENGTH_SHORT).apply {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
            else -> super.onBackPressed()
        }
    }
}
