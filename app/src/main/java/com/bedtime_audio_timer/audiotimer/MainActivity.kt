package com.bedtime_audio_timer.audiotimer

import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.service.autofill.Validators.and
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import kotlin.concurrent.schedule
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    var m_volume: Int = 0 // Target volume
    var m_minutes: Int = 0 // Target minutes

    // timerObjectDelay and timerObjectPeriod for Timer object when UP/DOWN buttons are hold that define its schedule
    public var timerObjectDelay = 150
    public var timerObjectPeriod = 150
    public var volumeIncrement = 1 // Volume increment on UP/DOWN button
    public var minutesIncrement = 1 // Minutes increment on UP/DOWN button


    private var timer: Timer? = null // this object is used to increases/decreases volume/minutes when a button is hold
    private lateinit var timerTask: TimerTask
    private val handler = Handler()

    // enum that defines what button is hold so the TimerRunnable object could increases/decreases volume/minutes
    enum class ButtonAction {
        VOLUME_UP, VOLUME_DOWN, TIMER_UP, TIMER_DOWN
    }

    // class for to update view from the timer thread when a button is hold
    internal inner class TimerRunnable : Runnable {

        var buttonAction: ButtonAction = ButtonAction.VOLUME_UP

        override fun run() {
            if (buttonAction == ButtonAction.VOLUME_UP)
                increaseVolume(null)
            else if (buttonAction == ButtonAction.VOLUME_DOWN)
                decreaseVolume(null)
            else if (buttonAction == ButtonAction.TIMER_UP)
                increaseTimer(null)
            else if (buttonAction == ButtonAction.TIMER_DOWN)
                decreaseTimer(null)

            if (buttonAction == ButtonAction.VOLUME_UP || buttonAction == ButtonAction.VOLUME_DOWN)
                updateVolume()
            else
                updateTimer()
        }

    }

    // class for listeners when a button is hold
    internal inner class TimerOnTouchListener : View.OnTouchListener {

        var buttonAction: ButtonAction = ButtonAction.VOLUME_UP

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                stopTimerTask()
            } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                startTimer(buttonAction)
            }
            return false
        }
    }

    private fun AudioManager.setVolume(volumeIndex: Int){
        this.setStreamVolume(
                AudioManager.STREAM_MUSIC, volumeIndex, AudioManager.FLAG_SHOW_UI // this shows audio control bar every time volume changes
        )
    }

    val atMath = AudioTimerMath()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            m_volume = savedInstanceState.getInt("reply_volume")
            m_minutes = savedInstanceState.getInt("reply_minutes")
        }
        else {
            val am : AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            m_volume = atMath.percentageToMultipleOfIncrement(atMath.currentVolumeToPercentage(am), 5) //Change 5 argument when increment variable is introduced in MainActivity
            m_minutes = 30
        }

        updateVolume()
        updateTimer()

        val viewVolumeUp = findViewById<View>(R.id.imgBtnVolumeUp) as View
        val timerOnTouchListenerVolumeUp = TimerOnTouchListener()
        timerOnTouchListenerVolumeUp.buttonAction = ButtonAction.VOLUME_UP
        viewVolumeUp.setOnTouchListener(timerOnTouchListenerVolumeUp)

        val viewVolumeDown = findViewById<View>(R.id.imgBtnVolumeDown) as View
        val timerOnTouchListenerVolumeDown = TimerOnTouchListener()
        timerOnTouchListenerVolumeDown.buttonAction = ButtonAction.VOLUME_DOWN
        viewVolumeDown.setOnTouchListener(timerOnTouchListenerVolumeDown)

        val viewTimerUp = findViewById<View>(R.id.imgBtnTimerUp) as View
        val timerOnTouchListenerTimerUp = TimerOnTouchListener()
        timerOnTouchListenerTimerUp.buttonAction = ButtonAction.TIMER_UP
        viewTimerUp.setOnTouchListener(timerOnTouchListenerTimerUp)

        val viewTimerDown = findViewById<View>(R.id.imgBtnTimerDown) as View
        val timerOnTouchListenerTimerDown = TimerOnTouchListener()
        timerOnTouchListenerTimerDown.buttonAction = ButtonAction.TIMER_DOWN
        viewTimerDown.setOnTouchListener(timerOnTouchListenerTimerDown)

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("reply_volume", m_volume)
        outState.putInt("reply_minutes", m_minutes)
    }


    fun startTimer(btnAct: ButtonAction) {
        timer = Timer()

        initializeTimerTask(btnAct)

        timer?.schedule(timerTask, timerObjectDelay.toLong(), timerObjectPeriod.toLong())

    }

    fun stopTimerTask() {
        timer?.cancel()
        timer = null
    }


    fun initializeTimerTask(btnAction: ButtonAction) {
        timerTask = object : TimerTask() {
            internal var buttonAction = btnAction
            override fun run() {
                val buttonAction = btnAction
                val timerRunnable = TimerRunnable()
                timerRunnable.buttonAction = btnAction
                handler.post(timerRunnable)
            }
        }
    }

    fun increaseVolume(view: View?){
        if (m_volume < 100)
            m_volume += volumeIncrement
        updateVolume()
    }

    fun decreaseVolume(view: View?) {
        if (m_volume > 0)
            m_volume -= volumeIncrement
        updateVolume()
    }

    fun updateVolume() {
        val showVolumeTextView = findViewById(R.id.textVolume) as TextView
        showVolumeTextView.text = (String.format("%3d", m_volume) + "%")

        val imgVolume = findViewById(R.id.imgVolume) as ImageView

        when (m_volume){
            0 -> imgVolume.setImageResource(R.drawable.mute)
            in 1..25 -> imgVolume.setImageResource(R.drawable.volume_min)
            in 26..50 -> imgVolume.setImageResource(R.drawable.volume_med)
            else -> imgVolume.setImageResource(R.drawable.volume_max)
        }
    }
    /*   if (m_volume == 0) {
           imgVolume.setImageResource(R.drawable.mute)
       } elseif (m_volume > 0) && (m_volume <=25){
           imgVolume.setImageResource(R.drawable.volume)

   }*/

    fun updateTimer() {
        val hours: Int
        val minutes: Int

        hours = m_minutes / 60
        minutes = m_minutes % 60

        val showTimerTextView = findViewById(R.id.textTimer) as TextView
        showTimerTextView.text = (String.format("%02d:%02d", hours, minutes))

    }

    fun increaseTimer(view: View?) {
        if (m_minutes < 100)
            m_minutes += minutesIncrement
        updateTimer()
    }

    fun decreaseTimer(view: View?) {
        if (m_minutes > 0)
            m_minutes -= minutesIncrement
        updateTimer()
    }

    var timerIsRunning: Boolean = false

    fun startTimer(view: View)  { //suggesting this be renamed in the upcoming refactor to reflect fact that it now starts or stops timer (toggleTimer perhaps?)
        if (timerIsRunning) {
            val myToast = Toast.makeText(this, "I should cancel", Toast.LENGTH_SHORT)
            myToast.show() //delete when timer cancellation works.
            cancelMainTimer()
        } else {
            val am: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            var numIntervals: Int
            numIntervals = am.getStreamVolume(AudioManager.STREAM_MUSIC) - atMath.percentageToVolume(m_volume, am)
            if (numIntervals < 0) {
                numIntervals = 0
            }

            mainTimer(m_minutes, numIntervals, am)
        }
        timerIsRunning = !timerIsRunning
    }

    var mTimer = Timer("interval timer", false) //refers to the main timer for the application

    private fun mainTimer(numMinutes: Int, numIntervals: Int, am: AudioManager){
        val intervalLength = atMath.findEqualIntervalsInMilliseconds(numMinutes, numIntervals)
        val startVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        var nextVolume = startVolume - 1
        for (interval in 1..numIntervals){
            val myToast = Toast.makeText(this, "I changed the volume!", Toast.LENGTH_SHORT) //delete when audio behavior is finalized
            mTimer.schedule(intervalLength*interval) {
                myToast.show() //delete when audio behavior is finalized
                am.setVolume(nextVolume)
                nextVolume -= 1
            }
        }
    }

    private fun cancelMainTimer(){
        mTimer.cancel()
        mTimer.purge()
    }

}