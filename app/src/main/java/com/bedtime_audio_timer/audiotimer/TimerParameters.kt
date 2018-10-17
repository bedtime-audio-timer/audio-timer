package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.os.Bundle
import com.bedtime_audio_timer.audiotimer.R.drawable.volume

class TimerParameters (val Running : Boolean = false) {
    private var volume: Int = 0
    private var millis: Long = 0
    private var leftMills: Long = 0
    private var startTime: Long = 0
    private var startVolume: Int = 0
    private var running: Boolean = false

    init {
        running = Running
    }

    fun loadInitialSetting(){ // now it show current volume, but later presets can be loaded from file
        volume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)//AudioTimerMath.percentageToMultipleOfIncrement(AudioTimerMath.currentVolumeToPercentage(), volumeIncrement)
        millis = 60000//5 * 60000
    }

    fun loadFromBundle(state: Bundle){ // to restore the values when activity is recreated after configuration change
        volume = state.getInt("reply_volume" + running.toString())
        millis = state.getLong("reply_minutes" + running.toString())
        leftMills = state.getLong("reply_progress" + running.toString())
    }

    fun saveToBundle(state: Bundle){ // to save the values when activity is destroyed on configuration change
        state.putInt("reply_volume" + running.toString(), volume)
        state.putLong("reply_minutes" + running.toString(), millis)
        state.putLong("reply_progress" + running.toString(), leftMills)
    }

    fun set(mil: Long, vol: Int){
        leftMills = mil
        millis = mil
        volume = vol
    }

    fun setStartParams(){
        startTime = System.currentTimeMillis()//Calendar.getInstance().timeInMillis //LocalDateTime.now()
        startVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    fun setStartTime(){
        startTime = System.currentTimeMillis()//Calendar.getInstance().timeInMillis //LocalDateTime.now()
    }

    fun getStartTime(): Long{
        return startTime
    }

    fun getStartVolume(): Int{
        return startVolume
    }

    fun setLeftMills(mil: Long){
        leftMills = leftMills - mil
    }

    fun getLeftMills(): Long{
        return leftMills
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

    fun getSeconds(): Int{
        return (millis / 1000).toInt()
    }

    fun getMinutes(): Int{
        return (millis / 60000).toInt()
    }

    fun increaseVolume(){
        var maxVol: Int = AudioManagerSingleton.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (volume < maxVol){
            volume  += volumeIncrement
        }
        if (volume > maxVol){
            volume  = maxVol
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
        millis += minutesIncrement * 60000
    }

    fun decreaseMinutes() {
        if (millis > 60000) {
            millis -= minutesIncrement * 60000
        }
        if (millis < 60000) {
            millis = 60000
        }
    }

    fun increaseHours() {
        if (millis < 60000 * 60 * 23) {
            millis += minutesIncrement * 60000 * 60
        }
    }

    fun decreaseHours() {
        if (millis > 60000 * 60) {
            millis -= minutesIncrement * 60000 * 60
        }
        else if (millis < 60000 * 60) {
            // leaving this expression empty, as it doesn't allow to check (millis == 60000 * 60)
        }
        else { // millis = 60000 * 60 = 1 hour -> 59 sec
            millis -= minutesIncrement * 60000
        }
    }

    fun setVolume(vol: Int){
        volume=vol
    }

}