package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager

fun AudioManager.setVolume(volumeIndex: Int){
    this.setStreamVolume(
            AudioManager.STREAM_MUSIC, volumeIndex,0
    )
}

//pause media functionality should go in this file as well.