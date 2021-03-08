package com.example.androiddevchallenge

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

const val TAG = "TimerView"

@Composable
fun TimerView(
    maxWidth: Dp,
    maxHeight: Dp,
) {
    val initialTimerText = "00:00:00"
    var timerText by remember { mutableStateOf(initialTimerText) }
    var buttonState by remember { mutableStateOf(ButtonState.INITIAL) }
    val countDownTimerMillis by remember { mutableStateOf(3000L) }

    val buttonStateTransition = updateTransition(targetState = buttonState)

    val positionState = buttonStateTransition.animateFloat(
        {
            TweenSpec(
                easing = LinearEasing,
                durationMillis = if (buttonState == ButtonState.INITIAL) 1 else countDownTimerMillis.toInt(),
            )
        }) {
        if (it == ButtonState.INITIAL) 1f else 0f
    }


    val timer by remember {
        mutableStateOf(
            countDownTimer(
                millisInFuture = countDownTimerMillis,
                countdownDurationInterval = 1000,
                onTickCallback = { millis ->
                    timerText = hms(millis)
                },
                onFinished = {
                    buttonState = when (buttonState) {
                        ButtonState.INITIAL -> ButtonState.LAUNCHED
                        else -> ButtonState.INITIAL
                    }
                    timerText = initialTimerText
                }
            )
        )
    }

    val onClick: () -> Unit = {
        buttonState = when (buttonState) {
            ButtonState.INITIAL -> {
                timer.start()
                ButtonState.LAUNCHED
            }
            else -> {
                Log.d(TAG, "Cancelleed-----")
                timer.cancel()
                timerText = initialTimerText
                ButtonState.INITIAL
            }
        }

        Log.d(TAG, "Position state value on value changed is $positionState")
    }

    val colorTransitionSpec = TweenSpec<Color>(
        durationMillis = countDownTimerMillis.toInt(),
        easing = FastOutSlowInEasing
    )

    val colorFirst by buttonStateTransition.animateColor(
        transitionSpec = { colorTransitionSpec }
    ) {
        when (it) {
            ButtonState.INITIAL -> Color(0xffff9897)//0xfff869d5
            ButtonState.LAUNCHED -> Color(0xffea4d2c)
        }
    }

    val height = with(LocalDensity.current) {
        maxHeight.toPx()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to colorFirst,
                    0.6f to colorFirst,
                    startY = 0f,
                    endY = height,
                )
            ),
    ) {
        SunView(buttonState, positionState, maxWidth)
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = timerText,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h2,
            )

            Box(modifier = Modifier
                .clickable { onClick() }
                .animateContentSize()
            ) {

                Button(
                    onClick = onClick,
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
}


@Composable
private fun SunView(
    buttonState: ButtonState,
    positionState: State<Float>,
    maxWidth: Dp
) {
    val circleSize = 30.dp
    val theta = positionState.value * Math.PI
    val centerPointX = maxWidth.value / 2
    val centerPointY = maxWidth.value / 2
    val radius = maxWidth.value / 2

    val x = centerPointX + (radius * cos(theta)) // cx + r*cos(theta)
    val y =
        centerPointY - (radius * sin(theta)) // cy - r*sin(theta) (modified for mobile) original cx + r*sin(theta)

    Log.d(TAG, "position value is ${positionState.value}")
    Log.d(TAG, "X $x - y $y")

    if (buttonState == ButtonState.LAUNCHED) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = Dp(x.toFloat()),
                    y = Dp(y.toFloat())
                )
        ) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

private fun hms(millis: Long) = String.format(
    Locale.getDefault(),
    "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
)

private fun countDownTimer(
    millisInFuture: Long,
    countdownDurationInterval: Long,
    onTickCallback: (Long) -> Unit,
    onFinished: () -> Unit
): CountDownTimer {
    return object : CountDownTimer(millisInFuture, countdownDurationInterval) {
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
fun TimerLightPreview() {
    MyTheme {
        TimerView(360.dp, 360.dp)
    }
}