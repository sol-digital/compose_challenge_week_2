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
package com.example.androiddevchallenge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.androiddevchallenge.R

@Composable
fun getNavBgColor() = if (isSystemInDarkTheme()) MaterialTheme.colors.primarySurface else colorResource(R.color.statusBar)

@Composable
fun MyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) darkColors(
            primary = colorResource(R.color.primary), primaryVariant = colorResource(R.color.primaryDark), onPrimary = colorResource(R.color.onPrimary),
            secondary = colorResource(R.color.secondary), onSecondary = colorResource(R.color.onSecondary)
        ) else lightColors(
            primary = colorResource(R.color.primary), primaryVariant = colorResource(R.color.primaryDark), onPrimary = colorResource(R.color.onPrimary),
            secondary = colorResource(R.color.secondary), secondaryVariant = colorResource(R.color.secondaryDark), onSecondary = colorResource(R.color.onSecondary)
        ),
        content = content
    )
}
