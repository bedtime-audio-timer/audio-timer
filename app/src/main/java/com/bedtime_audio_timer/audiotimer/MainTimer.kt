package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.util.Log
import java.util.*

class MainTimer {

    private var timerIsRunning: Boolean = false

    public interface TimerCallback {
        fun onTimerFinished()
        fun onVolumeChange(newVolume: Int)
    }
/*
    public interface AppTimerCallback {
        fun onVolumeChange(newVolume: Int)
    }
*/

    var timer: Timer? = null //refers to the main timer for the application
    val subscribers = mutableListOf<TimerCallback>()
    var startTime: Long = 0//Calendar? = null
    var endTime: Long = 0//Calendar? = null

    fun isRunning(): Boolean {
        return (timer != null);
    }

    fun subscribe(cb: TimerCallback?){
        subscribers.add(cb as TimerCallback)
    }

    fun unsubscribe(cb: TimerCallback?){
        subscribers.remove(cb as TimerCallback)
    }

    fun unsubscribeAll(){
        subscribers.clear()
    }

    fun startMainTimer(timerParams: TimerParameters/*, vlcb: AppTimerCallback*/){
//        val numIntervals: Int = AudioTimerMath.findNumIntervals(timerParams)
  //      val numMinutes=timerParams.getMinutes()
        timer = Timer("interval timer", false)
        startTime = System.currentTimeMillis()//Calendar.getInstance().timeInMillis //LocalDateTime.now()
        endTime = startTime

      //  var intervalLength = AudioTimerMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        val startVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)
        var nextVolume = startVolume
        //var currentVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

        // formula: volume at any time of the timer
        // v = startVolume + (targetVolume - startVolume)/target minutes * t

        Log.d("TIMER ", "start parameters: " + timerParams.getMillis().toString() + " " + timerParams.getVolume().toString())

        timer?.schedule(object : TimerTask() {
                override fun run() {

                    val currentTime = System.currentTimeMillis()// Calendar.getInstance().timeInMillis
                    val timePassed = currentTime - startTime
                    val currentVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

                    nextVolume = (startVolume + (timerParams.getVolume() - startVolume).toDouble()/timerParams.getMillis().toDouble() * timePassed).toInt()

                    if ((nextVolume < timerParams.getVolume())||(timerParams.getMillis() < 0)){
                        nextVolume = timerParams.getVolume()
                        Log.d("TIMER ", "last volume " + nextVolume.toString())
                    }


                    if((nextVolume < currentVolume) /*&& (nextVolume >= timerParams.getVolume())*/){
                        Log.d("TIMER ", "change volume from " + currentVolume.toString() + " to: " + nextVolume.toString() + "  " + timePassed.toString() + " time left " + (timerParams.getMillis()-timePassed).toString() )
                        //currentVolume = nextVolume
                        if (subscribers.any()){
                            for (sub in subscribers){
                                sub.onVolumeChange(nextVolume)
                            }
                        }
                        AudioManagerSingleton.am.setVolume(nextVolume)
                    }

                    if (timePassed >= timerParams.getMillis()) {
                        Log.d("TIMER ", "cancel with volume " + AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC).toString() + " and time passed " + timePassed.toString())

                        cancelMainTimer()

                        if (subscribers.any()) {
                            for (sub in subscribers) {
                                sub.onTimerFinished()
                            }
                        }
                    }
                }
        },  1000, 1000)

/*        timer?.schedule(object : TimerTask() {
            override fun run() {
                AudioManagerSingleton.am.setVolume(nextVolume)
                if (subscribers.any() == true){
                    for (sub in subscribers){
                        sub.onVolumeChange(nextVolume)
                    }
                }


                nextVolume -= 1
                interval += 1
                if (interval > numIntervals) {
                    cancelMainTimer()

                    if (subscribers.any() == true) {
                        for (sub in subscribers) {
                            sub.onTimerFinished()
                        }
                    }
                }
            }
        },  intervalLength, intervalLength)
*/
    }

    fun cancelMainTimer(){
        endTime = System.currentTimeMillis()
        timer?.cancel() //the ? is the safe call operator in Kotlin
        timer = null
        VolumeSlider.resetAfterTimerCancel()
        Log.d("TIMER ", "progress: " + getProgress().toString())
    }

    fun getProgress(): Long{
        if(isRunning()){
            return System.currentTimeMillis() - startTime
        }
        else {
            return endTime - startTime
        }
    }
}
