package com.bedtime_audio_timer.audiotimer

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar

class VolumeSliderListeners {
    companion object {

        fun setListeners(timer: MainTimer?, imgVolume: ImageView, targetVolSeekBar: SeekBar,
                         greyedVolSeekBar: SeekBar, oldVolSeekBar: SeekBar){
            targetVolSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(volSeekBar: SeekBar, p1: Int, p2: Boolean) {
                    VolumeSlider.capVolume(targetVolSeekBar)
                    if (timer!!.isRunning() && volSeekBar.progress== greyedVolSeekBar.progress){
                        timer.cancelMainTimer()
                    }
                    VolumeSlider.timerParams.setVolume(volSeekBar.getProgress())
                    SpeakerIcon.updateVolumeImage(VolumeSlider.timerParams, imgVolume, VolumeSlider.maxVol)
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
                        VolumeSlider.resetAfterTimerCancel()
                    }
                    if (greyedVolSeekBar.progress!=oldVolSeekBar.progress){
                        VolumeSlider.greyedVolSeekBar.setVisibility(View.VISIBLE)
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
    }
}