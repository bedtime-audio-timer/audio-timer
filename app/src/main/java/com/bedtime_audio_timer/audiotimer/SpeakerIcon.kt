package com.bedtime_audio_timer.audiotimer

import android.widget.ImageView

class SpeakerIcon{
    companion object {

        fun updateVolumeImage(timerParams: TimerParameters, imgVolume: ImageView, maxVol: Int) {

            when (timerParams.getVolume()){
                0 -> imgVolume.setImageResource(R.drawable.mute)
                in 1..maxVol/4 -> imgVolume.setImageResource(R.drawable.volume_min)
                in maxVol/4+1..maxVol/2 -> imgVolume.setImageResource(R.drawable.volume_med)
                else -> imgVolume.setImageResource(R.drawable.volume_max)
            }
        }
    }
}