package com.bedtime_audio_timer.audiotimer

import android.app.Application
import android.media.AudioManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

public class AudioTimerApplication : Application() {

    private var mTimer: MainTimer? = null
    private var mOutsideVolumeListener: OutsideVolumeListener? = null

    override fun onCreate() {
        super.onCreate()
        mTimer = com.bedtime_audio_timer.audiotimer.MainTimer()
        instance = this
        mOutsideVolumeListener = com.bedtime_audio_timer.audiotimer.OutsideVolumeListener()
        mTimer?.subscribe(mOutsideVolumeListener)
    }

    override fun onTerminate() {
        mTimer?.unsubscribeAll()
        super.onTerminate()
    }

    fun getTimer(): MainTimer?{
        return mTimer
    }

    fun getListener(): OutsideVolumeListener?{
        return mOutsideVolumeListener
    }

    companion object {
        var instance : AudioTimerApplication? = null
    }
}

