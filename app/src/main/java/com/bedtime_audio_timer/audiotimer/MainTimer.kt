package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import com.bedtime_audio_timer.audiotimer.R.drawable.timer
import java.util.*
import java.util.function.ToLongFunction
import kotlin.concurrent.schedule

class MainTimer {

    private var timerIsRunning: Boolean = false

    public interface TimerCallback {
        fun onTimerFinished()
        fun onVolumeChange()
    }
    var timer: Timer? = null //refers to the main timer for the application


    fun isRunning(): Boolean {
        return (timer != null);
    }

    fun startMainTimer(numMinutes: Int, numIntervals: Int, am: AudioManager, cb: TimerCallback){

        timer = Timer("interval timer", false)

        var intervalLength = atMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        val startVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        var nextVolume = startVolume - 1
        var interval = 1

/*
        for (interval in 1..numIntervals){
            timer.schedule(intervalLength*interval) {
                am.setVolume(nextVolume)
                nextVolume -= 1
            }
        }
*/
        timer?.schedule(object : TimerTask() {
                override fun run() {
                    am.setVolume(nextVolume)
                    cb.onVolumeChange();
                    nextVolume -= 1
                    interval += 1
                    if (interval > numIntervals) {
                        cancelMainTimer()
                        cb.onTimerFinished()
                    }
                }
        },  intervalLength, intervalLength)

    }

    fun cancelMainTimer(){
        timer?.cancel() //the ? is the safe call operator in Kotlin
        timer = null
    }

}