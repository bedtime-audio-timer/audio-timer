package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import com.bedtime_audio_timer.audiotimer.AudioManagerSingleton.Companion.am
import com.bedtime_audio_timer.audiotimer.R.drawable.timer
import java.sql.Time
import java.time.LocalDateTime
import java.util.*
import java.util.function.ToLongFunction
import kotlin.concurrent.schedule

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

      //  var intervalLength = AudioTimerMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        val startVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)
        var nextVolume = startVolume - 1
        var currentVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

        // formula of volume at any time of the timer
        // v = startVolume + (timerParams.getVolume() - startVolume)/timerParams.gerMinutes() * t


        timer?.schedule(object : TimerTask() {
                override fun run() {

                    val currentTime = System.currentTimeMillis()// Calendar.getInstance().timeInMillis
                    val timePassed = currentTime - startTime

                    nextVolume = (startVolume + (timerParams.getVolume() - startVolume).toDouble()/timerParams.getMinutes().toDouble() / 60000.toDouble() * timePassed).toInt()
                    if(nextVolume < currentVolume){
                        AudioManagerSingleton.am.setVolume(nextVolume)
                        currentVolume = nextVolume
                        if (subscribers.any() == true){
                            for (sub in subscribers){
                                sub.onVolumeChange(nextVolume)
                            }
                        }

                    }

                    if (timePassed >= timerParams.getMinutes() * 60000) {
                        cancelMainTimer()

                        if (subscribers.any()) {
                            for (sub in subscribers) {
                                sub.onTimerFinished()
                            }
                        }
                    }
                }
        },  2000, 2000)

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
    }
/*
    fun getProgress(): Int{
        if isRunning(){
            return System.currentTimeMillis() - startTime
        }
        else {
            return endTime - startTime
        }
    }*/
}
