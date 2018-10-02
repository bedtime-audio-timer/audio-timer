package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.provider.ContactsContract
import android.widget.ImageView
import android.widget.SeekBar



class VolumeSlider{
    companion object {

        var maxVol = AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        lateinit var greyedVolSeekBar: SeekBar
        lateinit var oldVolSeekBar: SeekBar
        lateinit var timerParams: TimerParameters

        //since this function is no longer only called after timer cancel it should be renamed in near future.
        fun resetAfterTimerCancel(){
            timerParams.setVolume(targetVolSeekBar.progress)
            changeVolumeSliderToCurrent(oldVolSeekBar)
        }

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

        fun setSliderMaxes(){
            setVolumeSliderMax(targetVolSeekBar)
            setVolumeSliderMax(greyedVolSeekBar)
            setVolumeSliderMax(oldVolSeekBar)
        }

        fun setAllSlidersToCurrent(){
            changeVolumeSliderToCurrent(targetVolSeekBar)
            changeVolumeSliderToCurrent(greyedVolSeekBar)
            changeVolumeSliderToCurrent(oldVolSeekBar)
        }

        fun setListeners(timer: MainTimer?, imgVolume: ImageView){
            targetVolSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(volSeekBar: SeekBar, p1: Int, p2: Boolean) {
                    capVolume(targetVolSeekBar)
                    timerParams.setVolume(volSeekBar.getProgress())
                    SpeakerIcon.updateVolumeImage(timerParams, imgVolume, maxVol)
                }
                override fun onStartTrackingTouch(volSeekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }

                override fun onStopTrackingTouch(volSeekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }
            })

            greyedVolSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(volSeekBar: SeekBar, p1: Int, p2: Boolean) {
                    if (targetVolSeekBar.progress> greyedVolSeekBar.progress){
                        targetVolSeekBar.progress= greyedVolSeekBar.progress
                    }
                    if (!timer!!.isRunning()){
                        oldVolSeekBar.setProgress(greyedVolSeekBar.progress)
                        resetAfterTimerCancel()
                    }
                }
                override fun onStartTrackingTouch(volSeekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }

                override fun onStopTrackingTouch(volSeekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }
            })
        }

        fun resetValues(volSlider: SeekBar, greyedSlider: SeekBar, originalSlider: SeekBar, _timerParams: TimerParameters, volImage: ImageView, timer: MainTimer?){
            timerParams=_timerParams
            targetVolSeekBar = volSlider
            greyedVolSeekBar = greyedSlider
            oldVolSeekBar=originalSlider
            setSliderMaxes()
            setAllSlidersToCurrent()
            SpeakerIcon.updateVolumeImage(timerParams, volImage, maxVol)
            setListeners(timer,volImage)

        }

    }

}