package com.bedtime_audio_timer.audiotimer

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.icu.util.UniversalTimeScale.toLong
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bedtime_audio_timer.audiotimer.R
//import com.bedtime_audio_timer.audiotimer.R.id.halveVol
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule
import android.widget.Toast

class MainActivity : AppCompatActivity() {

  /*  var targetVol=0
    var oldVol=0    //stored in case needed for restoring volume

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val audioManager : AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        halveVol.setOnClickListener {
            oldVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            targetVol = oldVol/2
            audioManager.setVolume(targetVol)
            mainTimer(5 , 30, audioManager) //temporary test parameters to allow for rapid assessment of timer
        }

    }
*/
    private fun AudioManager.setVolume(volumeIndex: Int){
        this.setStreamVolume(
                AudioManager.STREAM_MUSIC, volumeIndex, AudioManager.FLAG_SHOW_UI
        )
    }

    var m_volume: Int = 0
    var m_minutes: Int = 0

    val atMath = AudioTimerMath()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val audioManager : AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        m_volume = atMath.percentageToMultipleOfIncrement(atMath.currentVolumeToPercentage(audioManager), 5) //Change 5 argument when increment variable is introduced in MainActivity
        m_minutes = 30

        updateVolume()
        updateTimer()
    }

    fun increaseVolume(view: View) {
        if (m_volume < 100)
            m_volume += 5
        //updateValue(view);
        updateVolume()
    }

    fun decreaseVolume(view: View) {
        if (m_volume > 0)
            m_volume -= 5
        //updateValue(view);
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
        //updateValue(view);
    }

    fun decreaseTimer(view: View) {
        if (m_minutes > 0)
            m_minutes -= 5
        updateTimer()
        //updateValue(view);
    }

    fun startTimer(view: View)  {
        val audioManager : AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mainTimer(m_minutes, 60, audioManager)
    }

    private fun mainTimer(numMinutes: Int, numIntervals: Int, am: AudioManager){ //AudioManager argument needed for future audio behavior
        val intervalLength = atMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        for (interval in 1..numIntervals){
            val myToast = Toast.makeText(this, "Interval passed", Toast.LENGTH_SHORT) //delete when audio behavior has
                                                                                                    // been defined for this function
            Timer("interval timer", false).schedule(intervalLength*interval) {
                myToast.show() //replace this line with desired audio behavior
            }
        }
    }

}
