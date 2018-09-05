package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.widget.SeekBar

class VolumeSlider{
    companion object {
        lateinit var volSeekBar: SeekBar
        var currentVolume: Int = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

        fun resetValues(volSlider: SeekBar){
            currentVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)
            volSeekBar = volSlider

            volSeekBar.setMax(AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC))

            volSeekBar.setProgress(currentVolume)

            volSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(volSeekBar: SeekBar, p1: Int, p2: Boolean) {
                    if (volSeekBar.getProgress()>currentVolume){
                        volSeekBar.setProgress(currentVolume)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // Do something
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //volSeekBar.setProgress(maxOf(volSeekBar.getProgress(), currentVolume))
                }
            })
        }

    }

}