package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.util.Log
import java.util.*

class MainTimer {

    public interface TimerCallback {
        fun onTimerFinished()
        fun onVolumeChange(newVolume: Int)
    }

    var timer: Timer? = null //refers to the main timer for the application
    val subscribers = mutableListOf<TimerCallback>()

/*  //moved to timerParams
    var startTime: Long = 0
    var startVolume: Int = 0
*/
    lateinit var params: TimerParameters
    private var endTime: Long = 0

    fun isRunning(): Boolean {
        return (timer != null)
    }

    fun subscribe(cb: TimerCallback?){
        subscribers.add(cb as TimerCallback)
    }

    fun unsubscribe(cb: TimerCallback?){
        subscribers.remove(cb as TimerCallback)
    }

    fun getParameters(): TimerParameters{
        return params
    }

    fun unsubscribeAll(){
        subscribers.clear()
    }

    fun startMainTimer(timerParams: TimerParameters){
        timer = Timer("interval timer", false)
        params = timerParams
        params.setStartParams()
        /*
        //moved to timerParams

        startTime = System.currentTimeMillis()//
        startVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)
        */

        endTime = params.getStartTime()
        var nextVolume = params.getStartVolume()

        // formula: volume at any time of the timer
        // v = startVolume + (targetVolume - startVolume)/target minutes * t

        Log.d("TIMER ", "start parameters: " + params.getMillis().toString() + " " + params.getVolume().toString())

        timer?.schedule(object : TimerTask() {
                override fun run() {

                    val currentTime = System.currentTimeMillis()
                    val timePassed = currentTime - params.getStartTime()
                    val currentVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

                    nextVolume = (params.getStartVolume() + (params.getVolume() - params.getStartVolume()).toDouble()/params.getMillis().toDouble() * timePassed + 1).toInt()

                    if ((nextVolume < params.getVolume())||(params.getMillis() < 0)){
                        nextVolume = params.getVolume()
                        Log.d("TIMER ", "last volume " + nextVolume.toString())
                    }


                    if((nextVolume < currentVolume) /*&& (nextVolume >= params.getVolume())*/){
                        Log.d("TIMER ", "change volume from " + currentVolume.toString() + " to: " + nextVolume.toString() + "  " + timePassed.toString() + " time left " + (params.getMillis()-timePassed).toString() )
                        //currentVolume = nextVolume
                        if (subscribers.any()){
                            for (sub in subscribers){
                                sub.onVolumeChange(nextVolume)
                            }
                        }
                        AudioManagerSingleton.am.setVolume(nextVolume)
                    }

                    if ((timePassed >= params.getMillis()) || (params.getVolume() >= nextVolume)) {
                        Log.d("TIMER ", "cancel with volume " + AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC).toString() + " and time passed " + timePassed.toString())

                        cancelMainTimer()

                        if (subscribers.any()) {
                            for (sub in subscribers) {
                                sub.onTimerFinished()
                            }
                        }
                    }
                }
        },  100, 100)
    }

    fun cancelMainTimer(){
        endTime = System.currentTimeMillis()
        timer?.cancel()
        timer = null
    }

    fun getProgress(): Long{
        if(isRunning()){
            return System.currentTimeMillis() - params.getStartTime()
        }
        else {
            return endTime - params.getStartTime()
        }
    }
}
