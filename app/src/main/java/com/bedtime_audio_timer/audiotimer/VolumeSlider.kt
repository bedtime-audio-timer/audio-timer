package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.widget.SeekBar

class VolumeSlider{
    companion object {
        lateinit var volSeekBar: SeekBar
        fun resetValues(volSlider: SeekBar){
            volSeekBar = volSlider
            volSeekBar.setMax(AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
            volSeekBar.setProgress(AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC))
        }
    }

}