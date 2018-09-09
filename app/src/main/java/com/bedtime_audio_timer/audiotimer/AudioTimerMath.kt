package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import com.bedtime_audio_timer.audiotimer.AudioManagerSingleton.Companion.am
import java.util.concurrent.TimeUnit

class AudioTimerMath{
    companion object {

        fun findNumIntervals(timerParams: TimerParameters): Int {
            var numIntervals: Int
            numIntervals = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC) - timerParams.getVolume()
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