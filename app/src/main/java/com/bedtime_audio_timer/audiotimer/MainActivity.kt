package com.bedtime_audio_timer.audiotimer

import android.annotation.SuppressLint
import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import android.widget.Toast
import com.bedtime_audio_timer.audiotimer.R.drawable.volume

class MainActivity : AppCompatActivity(), MainTimer.TimerCallback {

    private var mTimer: MainTimer? = null

    private var timer: Timer? = null // this object is used to increases/decreases volume/minutes when a button is hold
    private lateinit var timerTask: TimerTask
    private val handler = Handler()
    private var timerParams = TimerParameters()

    // enum that defines what button is hold so the TimerRunnable object could increases/decreases volume/minutes
    enum class ButtonAction {
        VOLUME_UP, VOLUME_DOWN, TIMER_UP, TIMER_DOWN
    }

    // class for to update view from the timer thread when a button is hold
    internal inner class TimerRunnable : Runnable {

        var buttonAction: ButtonAction = ButtonAction.VOLUME_UP

        override fun run() {
            if (buttonAction == ButtonAction.VOLUME_UP)
                timerParams.increaseVolume()
            else if (buttonAction == ButtonAction.VOLUME_DOWN)
                timerParams.decreaseVolume()
            else if (buttonAction == ButtonAction.TIMER_UP)
                timerParams.increaseMinutes()
            else if (buttonAction == ButtonAction.TIMER_DOWN)
                timerParams.decreaseMinutes()

            if (buttonAction == ButtonAction.VOLUME_UP || buttonAction == ButtonAction.VOLUME_DOWN)
                updateVolumeTextView()
            else
                updateMinutesTextView()
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

    @SuppressLint("ClickableViewAccessibility")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTimer = (application as AudioTimerApplication).getTimer()

        if (savedInstanceState != null) {
            timerParams.loadFromBundle(savedInstanceState)
        }
        else {
            timerParams.loadInitialSetting(getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager)
        }

        updateVolumeTextView()
        updateMinutesTextView()
        updateTimerButtonImage()

        val imgBtnMain = findViewById<View>(R.id.btnMain) as ImageButton

        imgBtnMain.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                updateTimerButtonImage()
            } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (!mTimer?.isRunning()!!) {
                    imgBtnMain.setImageResource(R.drawable.start_pressed2)
                } else {
                    imgBtnMain.setImageResource(R.drawable.stop_pressed2)
                }
            }
            false
        })


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

        timerParams.saveFromBundle(outState)
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
        timerParams.increaseVolume()
        updateVolumeTextView()
    }

    fun decreaseVolume(view: View?) {
        timerParams.decreaseVolume()
        updateVolumeTextView()
    }

    fun updateVolumeTextView() {
        val showVolumeTextView = findViewById(R.id.textVolume) as TextView
        showVolumeTextView.text = (String.format("%3d", timerParams.getVolume()) + "%")

        val imgVolume = findViewById(R.id.imgVolume) as ImageView

        when (timerParams.getVolume()){
            0 -> imgVolume.setImageResource(R.drawable.mute)
            in 1..25 -> imgVolume.setImageResource(R.drawable.volume_min)
            in 26..50 -> imgVolume.setImageResource(R.drawable.volume_med)
            else -> imgVolume.setImageResource(R.drawable.volume_max)
        }
    }

    fun updateMinutesTextView() {
        val hours: Int
        val minutes: Int

        hours = timerParams.getMinutes() / 60
        minutes = timerParams.getMinutes() % 60

        val showTimerTextView = findViewById(R.id.textTimer) as TextView
        showTimerTextView.text = (String.format("%02d:%02d", hours, minutes))

    }

    fun increaseMinutes(view: View?) {
        timerParams.increaseMinutes()
        updateMinutesTextView()
    }

    fun decreaseMinutes(view: View?) {
        timerParams.decreaseMinutes()
        updateMinutesTextView()
    }

    fun updateTimerButtonImage() {
        val imgBtnMain = findViewById<View>(R.id.btnMain) as ImageButton
        if (mTimer?.isRunning()!!) {
            imgBtnMain.setImageResource(R.drawable.stop)
        } else {
            imgBtnMain.setImageResource(R.drawable.start)
        }
    }

    fun toggleTimer(view: View)  { //suggesting this be renamed in the upcoming refactor to reflect fact that it now starts or stops timer (toggleTimer perhaps?)
        if (mTimer?.isRunning()!!) {
            val myToast = Toast.makeText(this, "I will cancel", Toast.LENGTH_SHORT)
            myToast.show() //delete this Toast when interface makes cancellation clear.
            mTimer?.cancelMainTimer()
        } else {
            val am: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            var numIntervals: Int
            numIntervals = am.getStreamVolume(AudioManager.STREAM_MUSIC) - AudioTimerMath.percentageToVolume(timerParams.getVolume(), am)
            if (numIntervals < 0) {
                numIntervals = 0
            }

            mTimer?.startMainTimer(timerParams, am, this)
        }
        updateTimerButtonImage()
    }

    override fun onTimerFinished(){
        handler.post(object: Runnable{
            override fun run(){
                val myToast = Toast.makeText(this@MainActivity, "Timer is finished!", Toast.LENGTH_SHORT)
                myToast.show() //delete this Toast when interface another message about finished timer pops up.
                updateTimerButtonImage()
            }
        })
    }

    override fun onVolumeChange(){
        val curVolume = AudioTimerMath.currentVolumeToPercentage(getSystemService(AUDIO_SERVICE) as AudioManager)

        handler.post(object: Runnable{
                override fun run(){
                    val myToast = Toast.makeText(this@MainActivity, "Current volume: $curVolume%", Toast.LENGTH_SHORT)
                    myToast.show() //delete this Toast when interface is updated with current state.
            }
        })
    }
}
