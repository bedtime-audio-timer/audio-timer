package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.widget.Toast
import java.util.*
import kotlin.concurrent.schedule

class mainTimer {

    var mTimer= Timer("interval timer", false) //refers to the main timer for the application

    fun startMainTimer(numMinutes: Int, numIntervals: Int, am: AudioManager){
        val intervalLength = atMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        val startVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        var nextVolume = startVolume - 1

        for (interval in 1..numIntervals){
            mTimer.schedule(intervalLength*interval) {
                am.setVolume(nextVolume)
                nextVolume -= 1
            }
        }
    }

    fun cancelMainTimer(){
        mTimer?.cancel() //the ? is the safe call operator in Kotlin
        mTimer = Timer("interval timer", false)
    }
}