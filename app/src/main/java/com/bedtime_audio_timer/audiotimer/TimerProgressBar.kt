package com.bedtime_audio_timer.audiotimer

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import com.bedtime_audio_timer.audiotimer.R.drawable.volume
import kotlin.math.max

class TimerProgressBar{
    companion object {


        //private var progressLeft: Long = 0
//    private var maxProgress: Int = 0

        lateinit var prBar: ProgressBar

        fun setMaxValues(progress: Int) {
            //        maxProgress = progress
            prBar.max = progress
            prBar.secondaryProgress = progress
            prBar.progress = progress
        }

        fun setProgress(/*timerProgBar: ProgressBar,*/ value: Int) {
            prBar.progress = value
        }

        fun setBar(timerProgBar: ProgressBar) {
            prBar = timerProgBar
        }
/*
        fun loadFromBundle(state: Bundle) { // to restore the values when activity is recreated after configuration change
            progressLeft = state.getLong("progress_left")
        }

        fun saveToBundle(state: Bundle) { // to save the values when activity is destroyed on configuration change
            state.putLong("progress_left", progressLeft)
        }
*/
        fun resetValues() {
            //      prBar = timerProgBar
            prBar.max = 100
            // val res = resources
            // val drawable = res.getDrawable(R.drawable.circular)
            // val mProgress = findViewById<View>(R.id.circularProgressbar) as ProgressBar
            prBar.progress = 0//100   // Main Progress
            prBar.secondaryProgress = 100 // Secondary Progress
//            prBar.max = 0//100 // Maximum Progress
            // progBar.progressDrawable = drawable
        }
    }
}