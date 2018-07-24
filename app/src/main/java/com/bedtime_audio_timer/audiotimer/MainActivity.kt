package com.bedtime_audio_timer.audiotimer

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.icu.util.UniversalTimeScale.toLong
import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bedtime_audio_timer.audiotimer.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    var targetVol=0
    var oldVol=0    //stored in case needed for restoring volume

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val audioManager : AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        halveVol.setOnClickListener {
            oldVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            targetVol = oldVol/2
            audioManager.setVolume(targetVol)
            mainTimer(5 , 30, audioManager)
        }

    }

    private fun timerHelper(numMillis: Long){
        Timer("interval timer", false).schedule(numMillis) {

        }
    }

    private fun AudioManager.setVolume(volumeIndex: Int){
        this.setStreamVolume(
                AudioManager.STREAM_MUSIC, volumeIndex, AudioManager.FLAG_SHOW_UI
        )
    }

    private fun mainTimer(numMinutes: Int, numIntervals: Int, am: AudioManager){
        val intervalLength = findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        for (interval in 1..numIntervals){
            val myToast = Toast.makeText(this, "Interval passed", Toast.LENGTH_SHORT) //delete when audio behavior has
                                                                                                    // been defined for this function
            Timer("interval timer", false).schedule(intervalLength*interval) {
                myToast.show() //replace this line with desired audio behavior
            }
        }
    }

    private fun findEqualIntervalsInMilliseconds(numMinutes: Int, numIntervals: Int): Long {
        val numMilliseconds = TimeUnit.MINUTES.toMillis(numMinutes.toLong())
        return numMilliseconds/numIntervals.toLong()
    }
}
