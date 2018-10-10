package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar



class VolumeSlider{
    companion object {
        var maxVol = AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        lateinit var greyedVolSeekBar: SeekBar
        lateinit var oldVolSeekBar: SeekBar
        lateinit var timerParams: TimerParameters

        fun realignMoons(){
            oldVolSeekBar.progress = greyedVolSeekBar.progress
        }

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

        fun setAllSlidersToCurrent(){
            changeVolumeSliderToCurrent(targetVolSeekBar)
            changeVolumeSliderToCurrent(greyedVolSeekBar)
            changeVolumeSliderToCurrent(oldVolSeekBar)
        }

        fun setSliderMaxes(){
            setVolumeSliderMax(targetVolSeekBar)
            setVolumeSliderMax(greyedVolSeekBar)
            setVolumeSliderMax(oldVolSeekBar)
        }

        fun resetValues(volSlider: SeekBar, greyedSlider: SeekBar, originalSlider: SeekBar, _timerParams: TimerParameters, volImage: ImageView, timer: MainTimer?){
            timerParams=_timerParams
            targetVolSeekBar = volSlider
            greyedVolSeekBar = greyedSlider
            oldVolSeekBar=originalSlider
            setSliderMaxes()
            setAllSlidersToCurrent()
            SpeakerIcon.updateVolumeImage(timerParams, volImage, maxVol)
            VolumeSliderListeners.setListeners(timer,volImage, targetVolSeekBar, greyedVolSeekBar, oldVolSeekBar)
        }

    }

}