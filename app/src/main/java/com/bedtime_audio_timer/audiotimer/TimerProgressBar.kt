package com.bedtime_audio_timer.audiotimer

import android.view.View
import android.widget.ProgressBar
import kotlin.math.max

class TimerProgressBar{
    companion object {
        lateinit var prBar: ProgressBar

        fun setMaxValue(maxProgress: Int){
            prBar.max = maxProgress
            prBar.secondaryProgress = maxProgress
            prBar.progress = maxProgress
        }

        fun setProgress(value : Int){
            prBar.progress = value
        }

        fun resetValues(timerProgBar: ProgressBar, maxProgress: Int){
            prBar = timerProgBar
            prBar.max = maxProgress
           // val res = resources
           // val drawable = res.getDrawable(R.drawable.circular)
           // val mProgress = findViewById<View>(R.id.circularProgressbar) as ProgressBar
            prBar.progress = 0//100   // Main Progress
            prBar.secondaryProgress = 0//100 // Secondary Progress
            prBar.max = 0//100 // Maximum Progress
           // progBar.progressDrawable = drawable
        }
    }


}