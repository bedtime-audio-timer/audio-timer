package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.os.Bundle
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.view.View

class TimerParameters {
    private var volume: Int = 0
    private var minutes: Int = 0

    fun loadInitialSetting(){ // now it show current volume, but later presets can be loaded from file
        volume = AudioTimerMath.percentageToMultipleOfIncrement(AudioTimerMath.currentVolumeToPercentage(), volumeIncrement)
        minutes = 5
    }

    fun loadFromBundle(state: Bundle){ // to restore the values when activity is recreated after configuration change
        volume = state.getInt("reply_volume")
        minutes = state.getInt("reply_minutes")

    }

    fun saveFromBundle(state: Bundle){ // to save the values when activity is destroyed on configuration change
        state.putInt("reply_volume", volume)
        state.putInt("reply_minutes", minutes)
    }

    fun set(min: Int, vol: Int){
        minutes = min
        volume = vol
    }

    fun setMinutes(min: Int){
        minutes = min
    }

    fun getVolume(): Int{
        return volume
    }

    fun getMinutes(): Int{
        return minutes
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
        minutes += minutesIncrement
    }

    fun decreaseMinutes() {
        if (minutes > 0) {
            minutes -= minutesIncrement
        }
        if (minutes < 0) {
            minutes = 0
        }
    }


}