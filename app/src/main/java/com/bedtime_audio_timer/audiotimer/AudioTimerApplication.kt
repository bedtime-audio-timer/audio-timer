package com.bedtime_audio_timer.audiotimer

import android.app.Application

public class AudioTimerApplication : Application() {

    private var mTimer: MainTimer? = null

    override fun onCreate() {
        super.onCreate()
        mTimer = com.bedtime_audio_timer.audiotimer.MainTimer()
    }

    fun getTimer(): MainTimer?{
        return mTimer
    }
}
