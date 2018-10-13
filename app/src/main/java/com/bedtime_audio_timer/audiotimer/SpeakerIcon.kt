package com.bedtime_audio_timer.audiotimer

import android.widget.ImageView
import com.bedtime_audio_timer.audiotimer.R.id.imgVolume
import com.bedtime_audio_timer.audiotimer.VolumeSlider.Companion.maxVol
import com.bedtime_audio_timer.audiotimer.VolumeSlider.Companion.timerParams

class SpeakerIcon{
    companion object {

        fun updateVolumeImage(/*timerParams: TimerParameters*/volume: Int, imgVolume: ImageView, maxVol: Int) {

            when (/*timerParams.getVolume()*/volume){
                0 -> imgVolume.setImageResource(R.drawable.mute)
                in 1..maxVol/4 -> imgVolume.setImageResource(R.drawable.volume_min)
                in maxVol/4+1..maxVol/2 -> imgVolume.setImageResource(R.drawable.volume_med)
                else -> imgVolume.setImageResource(R.drawable.volume_max)
            }
        }
    }
}