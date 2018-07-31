package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import java.util.concurrent.TimeUnit

class AudioTimerMath{
    fun currentVolumeToPercentage(am: AudioManager): Int {
        return 100*am.getStreamVolume(AudioManager.STREAM_MUSIC)/am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    fun percentageToMultipleOfFive(percentage: Int): Int{
        return 5*(percentage/5)
    }

    fun percentageToVolume(percentage: Int, am: AudioManager): Int {
        return percentage*am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/100
    }

    fun findEqualIntervalsInMilliseconds(numMinutes: Int, numIntervals: Int): Long {
        val numMilliseconds = TimeUnit.MINUTES.toMillis(numMinutes.toLong())
        return numMilliseconds/numIntervals.toLong()
    }

}