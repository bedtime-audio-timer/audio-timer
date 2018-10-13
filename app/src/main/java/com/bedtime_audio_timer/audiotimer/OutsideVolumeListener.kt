package com.bedtime_audio_timer.audiotimer

import com.bedtime_audio_timer.audiotimer.R.drawable.volume

class OutsideVolumeListener : VolumeListener.Callback, MainTimer.TimerCallback{
    private var lastAppChange: Int = 0
    private var mVolumeListener: VolumeListener? = null
    private var cb: OutsideListenerMessage? = null

    init{
        mVolumeListener = VolumeListener()
        mVolumeListener?.startListening(this)
    }
    public interface OutsideListenerMessage{
        fun onOutsideVolumeChange(newVolume: Int, lastAppChangeVolume: Int)
    }

    fun startListening(receiver: OutsideListenerMessage){
        cb = receiver
    }

    override fun onVolumeChange(setVolume: Int) {
        lastAppChange = setVolume
    }

    override fun onVolumeChangeDetected(detectedVolume: Int) {
        if (detectedVolume != lastAppChange) { // volume was changed outside
            cb?.onOutsideVolumeChange(detectedVolume, lastAppChange)
        }
    }

    override fun onTimerFinished() {
        lastAppChange = lastAppChange
    }


}