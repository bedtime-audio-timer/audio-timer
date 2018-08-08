package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import java.util.*
import kotlin.concurrent.schedule
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private fun AudioManager.setVolume(volumeIndex: Int){
        this.setStreamVolume(
                AudioManager.STREAM_MUSIC, volumeIndex, AudioManager.FLAG_SHOW_UI // this shows audio control bar every time volume changes
        )
    }

    var m_volume: Int = 0
    var m_minutes: Int = 0

    val atMath = AudioTimerMath()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val am : AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        m_volume = atMath.percentageToMultipleOfIncrement(atMath.currentVolumeToPercentage(am), 5) //Change 5 argument when increment variable is introduced in MainActivity
        m_minutes = 30

        updateVolume()
        updateTimer()
    }

    fun increaseVolume(view: View) {
        if (m_volume < 100)
            m_volume += 5
        updateVolume()
    }

    fun decreaseVolume(view: View) {
        if (m_volume > 0)
            m_volume -= 5
        updateVolume()
    }

    fun updateVolume() {
        val showVolumeTextView = findViewById(R.id.textVolume) as TextView
        showVolumeTextView.text = (String.format("%3d", m_volume) + "%")
    }

    fun updateTimer() {
        //Get the ID of the button that was clicked
        val hours: Int
        val minutes: Int

        hours = m_minutes / 60
        minutes = m_minutes % 60

        val showTimerTextView = findViewById(R.id.textTimer) as TextView
        showTimerTextView.text = (String.format("%02d:%02d", hours, minutes))

    }

    fun increaseTimer(view: View) {
        if (m_minutes < 100)
            m_minutes += 5
        updateTimer()
    }

    fun decreaseTimer(view: View) {
        if (m_minutes > 0)
            m_minutes -= 5
        updateTimer()
    }

    fun startTimer(view: View)  {
        val am : AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        var numIntervals: Int
        numIntervals = am.getStreamVolume(AudioManager.STREAM_MUSIC) - atMath.percentageToVolume(m_volume, am)
        if (numIntervals < 0) {
            numIntervals = 0
        }

        mainTimer(m_minutes, numIntervals, am)
    }

    private fun mainTimer(numMinutes: Int, numIntervals: Int, am: AudioManager){ 
        val intervalLength = atMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        for (interval in 1..numIntervals){
            val myToast = Toast.makeText(this, "I changed the volume!", Toast.LENGTH_SHORT) //delete when audio behavior is finalized
            val startVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            var nextVolume = startVolume - 1
            Timer("interval timer", false).schedule(intervalLength*interval) {
                myToast.show() //delete when audio behavior is finalized
                am.setVolume(nextVolume)
                nextVolume -= 1
            }
        }
    }

}
