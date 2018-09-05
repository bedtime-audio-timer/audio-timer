package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.widget.SeekBar

class VolumeSlider{
    companion object {
        lateinit var targetVolSeekBar: SeekBar
        var currentVolume: Int = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

        fun resetValues(volSlider: SeekBar){
            currentVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)
            targetVolSeekBar = volSlider

            targetVolSeekBar.setMax(AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC))

            targetVolSeekBar.setProgress(currentVolume)

            targetVolSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(volSeekBar: SeekBar, p1: Int, p2: Boolean) {
                    if (volSeekBar.getProgress()>currentVolume){
                        volSeekBar.setProgress(currentVolume)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }
            })
        }

    }

}