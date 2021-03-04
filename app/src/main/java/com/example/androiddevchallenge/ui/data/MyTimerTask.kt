package com.example.androiddevchallenge.ui.data

import java.util.TimerTask

class MyTimerTask(val action: () -> Unit) : TimerTask() {
    override fun run() {
        action()
    }
}