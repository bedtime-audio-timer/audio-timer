package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import com.bedtime_audio_timer.audiotimer.R.drawable.timer
import java.util.*

class VolumeListener {

    private var lastVolume : Int = 0

    public interface Callback {
        fun onVolumeChangeDetected(newVolume: Int)
    }

    var timer: Timer? = null

    fun startListening(cb : Callback){
        timer = Timer("Volume listener", false)
        lastVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

        timer?.schedule(object : TimerTask() {
            override fun run() {
                var currentVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

                if (currentVolume != lastVolume){ // volume changed
                    cb.onVolumeChangeDetected(currentVolume);
                    lastVolume = currentVolume
                }
            }
        },  113, 113)

    }

    fun cancel(){
        timer?.cancel() //the ? is the safe call operator in Kotlin
        timer = null
    }
}