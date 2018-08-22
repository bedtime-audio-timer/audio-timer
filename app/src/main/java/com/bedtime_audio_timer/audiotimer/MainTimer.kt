package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import java.util.*
import kotlin.concurrent.schedule

class MainTimer {

    private var timerIsRunning: Boolean = false

    var timer= Timer("interval timer", false) //refers to the main timer for the application

    fun getTimerIsRunning(): Boolean {
        return timerIsRunning
    }

    fun setTimerIsRunning(isRunning: Boolean) {
        timerIsRunning=isRunning
    }

    fun startMainTimer(numMinutes: Int, numIntervals: Int, am: AudioManager){
        val intervalLength = atMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        val startVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        var nextVolume = startVolume - 1

        for (interval in 1..numIntervals){
            timer.schedule(intervalLength*interval) {
                am.setVolume(nextVolume)
                nextVolume -= 1
            }
        }
    }

    fun cancelMainTimer(){
        timer?.cancel() //the ? is the safe call operator in Kotlin
        timer = Timer("interval timer", false)
    }
}