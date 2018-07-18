package com.bedtime_audio_timer.audiotimer

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bedtime_audio_timer.audiotimer.R
import kotlinx.android.synthetic.main.activity_main.*

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
        }

    }

    private fun AudioManager.setVolume(volumeIndex: Int){
        this.setStreamVolume(
                AudioManager.STREAM_MUSIC, volumeIndex, AudioManager.FLAG_SHOW_UI
        )
    }
}
