package com.bedtime_audio_timer.audiotimer

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.R.attr.duration
import android.view.View

class WhiteNoise {
    companion object {
        var sampleRate : Int = 44100

        var duration: Int = 2000

        var bufferSize: Int = sampleRate* duration

        var soundData = ByteArray(bufferSize)

        var enabled: Boolean = false


        var track = AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_DEFAULT,
                AudioFormat.ENCODING_PCM_8BIT, bufferSize,
                AudioTrack.MODE_STATIC
        )

        fun generateWhiteNoise(){
            //does not yet generate white noise. Currently testing output of sound.
            var sample: Byte = 50
            for (i in 0..bufferSize-1){
                soundData[i]=sample
            }
            track.write(soundData, 0, bufferSize)
            track.play()


        }

    }
}