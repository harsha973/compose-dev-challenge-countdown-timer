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

import android.content.IntentSender
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        TimerView()
    }
}

@Composable
fun TimerView() {
    var timerText by remember { mutableStateOf("00:00:00") }
    val onFinished = { timerText = "00:00:00" }

    val timer by remember {
        mutableStateOf(
            countDownTimer(
                onTickCallback = { timerText = it.toString() },
                onFinished = onFinished
            )
        )
    }

    var buttonState by remember { mutableStateOf(ButtonState.INITIAL) }
    val onClick: () -> Unit = {
        buttonState = when (buttonState) {
            ButtonState.INITIAL -> {
                timer.start()
                ButtonState.LAUNCHED
            }
            else -> {
                timer.cancel()
                onFinished()
                ButtonState.INITIAL
            }
        }
    }
    val transition = updateTransition(targetState = buttonState)

    val buttonColor by transition.animateColor {
        when (it) {
            ButtonState.INITIAL -> MaterialTheme.colors.primary
            ButtonState.LAUNCHED -> Color.Red
        }
    }

    val roundedCornerSize by transition.animateDp {
        when (it) {
            ButtonState.INITIAL -> 6.dp
            ButtonState.LAUNCHED -> 50.dp
        }
    }

    Box(
        Modifier
            .padding(16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = timerText)
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = buttonColor,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                modifier = Modifier.animateContentSize(),
                shape = RoundedCornerShape(roundedCornerSize)
            )
            {
                when (buttonState) {
                    ButtonState.INITIAL -> {
                        Text("Launch")
                    }
                    ButtonState.LAUNCHED -> {
                        Text("Stop")
                    }
                }
            }
        }
    }

}

private fun countDownTimer(
    onTickCallback: (Long) -> Unit,
    onFinished: () -> Unit
): CountDownTimer {
    return object : CountDownTimer(10000L, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            onTickCallback(millisUntilFinished)
        }

        override fun onFinish() {
            onFinished()
        }
    }
}

enum class ButtonState {
    INITIAL, LAUNCHED
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
