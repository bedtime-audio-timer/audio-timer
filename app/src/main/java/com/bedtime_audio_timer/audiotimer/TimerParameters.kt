package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.os.Bundle

class TimerParameters {
    private var volume: Int = 0
    private var millis: Long = 0

    fun loadInitialSetting(){ // now it show current volume, but later presets can be loaded from file
        volume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)//AudioTimerMath.percentageToMultipleOfIncrement(AudioTimerMath.currentVolumeToPercentage(), volumeIncrement)
        millis = 0
    }

    fun loadFromBundle(state: Bundle){ // to restore the values when activity is recreated after configuration change
        volume = state.getInt("reply_volume")
        millis = state.getLong("reply_minutes")

    }

    fun saveFromBundle(state: Bundle){ // to save the values when activity is destroyed on configuration change
        state.putInt("reply_volume", volume)
        state.putLong("reply_minutes", millis)
    }

    fun set(mil: Long, vol: Int){
        millis = mil
        volume = vol
    }

    fun setMillis(mil: Long){
        millis = mil
    }

    fun getVolume(): Int{
        return volume
    }

    fun getMillis(): Long{
        return millis
    }

    fun increaseVolume(){
        if (volume < 100){
            volume  += volumeIncrement
        }
        if (volume > 100){
            volume  = 100
        }
    }

    fun decreaseVolume() {
        if (volume > 0) {
            volume -= volumeIncrement
        }
        if (volume < 0) {
            volume = 0
        }
    }

    fun increaseMinutes() {
        millis += minutesIncrement
    }

    fun decreaseMinutes() {
        if (millis > 0) {
            millis -= minutesIncrement
        }
        if (millis < 0) {
            millis = 0
        }
    }


}