package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.widget.SeekBar

class VolumeSlider{
    companion object {
        lateinit var targetVolSeekBar: SeekBar
        fun changeVolumeSliderToCurrent(slider: SeekBar){
            slider.setProgress(AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC))
        }

        fun setVolumeSliderMax(slider: SeekBar){
            slider.setMax(AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        }

        fun capVolume(slider: SeekBar){
            if (slider.getProgress()>AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)){
                changeVolumeSliderToCurrent(slider)
            }
        }

        fun resetValues(volSlider: SeekBar){

            targetVolSeekBar = volSlider
            setVolumeSliderMax(targetVolSeekBar)
            changeVolumeSliderToCurrent(targetVolSeekBar)

            targetVolSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(volSeekBar: SeekBar, p1: Int, p2: Boolean) {
                    capVolume(targetVolSeekBar)
                }
                override fun onStartTrackingTouch(volSeekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }

                override fun onStopTrackingTouch(volSeekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }
            })
        }

    }

}