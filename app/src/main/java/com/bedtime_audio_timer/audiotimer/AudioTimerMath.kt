package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import java.util.concurrent.TimeUnit

class AudioTimerMath{
    fun currentVolumeToPercentage(am: AudioManager): Int {
        return 100*am.getStreamVolume(AudioManager.STREAM_MUSIC)/am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    fun percentageToMultipleOfIncrement(percentage: Int, increment: Int): Int{
        var roundedPercent = increment*Math.round(percentage.toDouble()/increment.toDouble()) //this rounding results in a Long
        if (roundedPercent>100){
            roundedPercent=100
        }
        return roundedPercent.toInt()
    }

    fun percentageToVolume(percentage: Int, am: AudioManager): Int {
        return percentage*am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/100
    }

    fun findEqualIntervalsInMilliseconds(numMinutes: Int, numIntervals: Int): Long {
        val numMilliseconds = TimeUnit.MINUTES.toMillis(numMinutes.toLong())
        return if (numIntervals==0) numMilliseconds else numMilliseconds/numIntervals.toLong()
    }

}