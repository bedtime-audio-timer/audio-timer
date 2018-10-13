package com.bedtime_audio_timer.audiotimer

import android.util.Log
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
                    /*if (timer!!.isRunning() && volSeekBar.progress== greyedVolSeekBar.progress){
                        timer.cancelMainTimer()
                    }*/
                    // moving setVolume to onStopTrackingTouch
                    //VolumeSlider.timerParams.setVolume(volSeekBar.getProgress())
                    SpeakerIcon.updateVolumeImage(/*VolumeSlider.timerParams*/volSeekBar.progress, imgVolume, VolumeSlider.maxVol)
                }
                override fun onStartTrackingTouch(volSeekBar: SeekBar) {
                    //Even though this is currently empty, removing it causes a Kotlin error in this file
                }

                override fun onStopTrackingTouch(volSeekBar: SeekBar) {

                    VolumeSlider.timerParams.setVolume(volSeekBar.progress)

                    if (timer!!.isRunning()){
                        var params = timer.getParameters()
                        Log.d("VolumeSliderListener ", "Old Params " + (params.getMillis()).toString() + ' ' + (params.getLeftMills()).toString() + ' ' + (params.getVolume()).toString() + ' ' + (params.getStartVolume()).toString() + ' ' + (params.getStartTime()).toString())
                        params.setVolume(volSeekBar.progress)
                        //params?.setStartTime()
                        params.setMillis(params.getMillis() - timer.getProgress())
                        params.setLeftMills(timer.getProgress())
                        params.setStartParams()
                        Log.d("VolumeSliderListener ", "updating RunningParams")
                        Log.d("VolumeSliderListener ", "New Params " + (params.getMillis()).toString() + ' ' + (params.getLeftMills()).toString() + ' ' + (params.getVolume()).toString() + ' ' + (params.getStartVolume()).toString() + ' ' + (params.getStartTime()).toString())

                    }
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
                        if (greyedVolSeekBar.progress!=oldVolSeekBar.progress){
                            VolumeSlider.greyedVolSeekBar.setVisibility(View.VISIBLE)
                            if (greyedVolSeekBar.progress>oldVolSeekBar.progress){
                                oldVolSeekBar.progress=greyedVolSeekBar.progress
                                greyedVolSeekBar.setVisibility(View.INVISIBLE)
                            }
                        }
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