package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import com.bedtime_audio_timer.audiotimer.AudioManagerSingleton.Companion.am
import java.util.concurrent.TimeUnit

class AudioTimerMath{
    companion object {
        fun currentVolumeToPercentage(): Int {
            return 100*AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)/AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        }

        fun percentageToMultipleOfIncrement(percentage: Int, increment: Int): Int{
            var roundedPercent = increment*Math.round(percentage.toDouble()/increment.toDouble()) //this rounding results in a Long
            if (roundedPercent>100){
                roundedPercent=100
            }
            return roundedPercent.toInt()
        }

        fun percentageToVolume(percentage: Int): Int {
            return percentage*AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/100
        }

        fun findNumIntervals(timerParams: TimerParameters): Int {
            var numIntervals: Int
            numIntervals = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC) - AudioTimerMath.percentageToVolume(timerParams.getVolume())
            if (numIntervals < 0) {
                numIntervals = 0
            }
            return numIntervals
        }

        fun findEqualIntervalsInMilliseconds(numMinutes: Int, numIntervals: Int): Long {
            val numMilliseconds = TimeUnit.MINUTES.toMillis(numMinutes.toLong())
            return if (numIntervals==0) numMilliseconds else numMilliseconds/numIntervals.toLong()
        }
    }

}