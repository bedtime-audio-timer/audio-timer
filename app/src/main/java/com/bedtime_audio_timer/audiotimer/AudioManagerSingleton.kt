package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity

class AudioManagerSingleton {
    companion object {
        val am: AudioManager = AudioTimerApplication.instance!!.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
    }
}

/*
public object AudioManagerSingleton
{

    var am: AudioManager = getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager

    init {
        am: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

}*/