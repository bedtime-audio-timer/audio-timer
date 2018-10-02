package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.widget.ImageView
import android.widget.SeekBar



class VolumeSlider{
    companion object {

        var maxVol = AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        lateinit var imgVolume: ImageView
        lateinit var greyedVolSeekBar: SeekBar
        lateinit var oldVolSeekBar: SeekBar
        lateinit var timerParams: TimerParameters


        fun updateVolumeImage(timerParams: TimerParameters) {

            when (timerParams.getVolume()){
                0 -> imgVolume.setImageResource(R.drawable.mute)
                in 1..maxVol/4 -> imgVolume.setImageResource(R.drawable.volume_min)
                in maxVol/4+1..maxVol/2 -> imgVolume.setImageResource(R.drawable.volume_med)
                else -> imgVolume.setImageResource(R.drawable.volume_max)
            }
        }

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

        fun setListeners(timer: MainTimer?){
            targetVolSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(volSeekBar: SeekBar, p1: Int, p2: Boolean) {
                    capVolume(targetVolSeekBar)
                    timerParams.setVolume(volSeekBar.getProgress())
                    updateVolumeImage(timerParams)
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
            imgVolume = volImage
            targetVolSeekBar = volSlider
            greyedVolSeekBar = greyedSlider
            oldVolSeekBar=originalSlider
            setSliderMaxes()
            setAllSlidersToCurrent()
            updateVolumeImage(timerParams)
            setListeners(timer)

        }

    }

}