package com.bedtime_audio_timer.audiotimer

import android.app.Application
import android.media.AudioManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

public class AudioTimerApplication : Application() {

    private var mTimer: MainTimer? = null

    override fun onCreate() {
        super.onCreate()
        mTimer = com.bedtime_audio_timer.audiotimer.MainTimer()
        instance = this
    }

    fun getTimer(): MainTimer?{
        return mTimer
    }

    companion object {
        var instance : AudioTimerApplication? = null
    }
}

