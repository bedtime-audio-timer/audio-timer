package com.bedtime_audio_timer.audiotimer

import android.annotation.SuppressLint
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.bedtime_audio_timer.audiotimer.R.drawable.volume
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainTimer.TimerCallback, OutsideVolumeListener.OutsideListenerMessage {

    private var mTimer: MainTimer? = null
    private var outsideVolumeListener: OutsideVolumeListener? = null

    private var timer: Timer? = null // this object is used to increases/decreases volume/minutes when a button is hold
    private lateinit var timerTask: TimerTask
    private val handler = Handler()
    //private var volume: Int = 0
    //private var minutes: Int = 0
    private var timerParams = TimerParameters()
    private var timerRunningParams = TimerParameters(true)
    // enum that defines what button is hold so the TimerRunnable object could increases/decreases volume/minutes
    enum class ButtonAction {
        VOLUME_UP, VOLUME_DOWN, TIMER_UP, TIMER_DOWN
    }

    // class for to update view from the timer thread when a button is hold
    internal inner class TimerRunnable : Runnable {

        var buttonAction: ButtonAction = ButtonAction.VOLUME_UP

        override fun run() {
            if (buttonAction == ButtonAction.VOLUME_UP)
            /*timerParams.*/ //increaseVolume()
            else if (buttonAction == ButtonAction.VOLUME_DOWN)
            /*timerParams.*/ //decreaseVolume()
            else if (buttonAction == ButtonAction.TIMER_UP)
                timerParams.increaseMinutes()
            else if (buttonAction == ButtonAction.TIMER_DOWN)
                timerParams.decreaseMinutes()

            if (buttonAction == ButtonAction.VOLUME_UP || buttonAction == ButtonAction.VOLUME_DOWN)
                //updateVolumeTextView()
            else
                updateMinutesTextView()
        }

    }

/*    // class for listeners when a button is hold
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
    }*/

    @SuppressLint("ClickableViewAccessibility")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTimer = (application as AudioTimerApplication).getTimer()
        mTimer?.subscribe(this)

        outsideVolumeListener = (application as AudioTimerApplication).getListener()
        outsideVolumeListener?.startListening(this)

        if (savedInstanceState != null) {
            timerParams.loadFromBundle(savedInstanceState)
            timerRunningParams.loadFromBundle(savedInstanceState)
        } else {
            timerParams.loadInitialSetting()
            timerRunningParams.loadInitialSetting()
        }

        //updateVolumeTextView()
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


        /*val viewVolumeUp = findViewById<View>(R.id.imgBtnVolumeUp) as View
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
        viewTimerDown.setOnTouchListener(timerOnTouchListenerTimerDown)*/

        val imgVolume = findViewById(R.id.imgVolume) as ImageView
        VolumeSlider.resetValues(targetVolSeekBar, greyedVolseekBar, originalVolseekBar, timerParams, imgVolume,mTimer)

    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.unsubscribe(this)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        timerParams.saveFromBundle(outState)
        timerRunningParams.saveFromBundle(outState)
    }
/*
    fun loadInitialSetting() { // now it show current volume, but later presets can be loaded from file
        volume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)//AudioTimerMath.percentageToMultipleOfIncrement(AudioTimerMath.currentVolumeToPercentage(), volumeIncrement)
        minutes = 5
    }

    fun loadFromBundle(state: Bundle) { // to restore the values when activity is recreated after configuration change
        volume = state.getInt("reply_volume")
        minutes = state.getInt("reply_minutes")
        timerParams.set(state.getLong("reply_timer_millis"), state.getInt("reply_timer_volume"))
    }

    fun saveFromBundle(state: Bundle) { // to save the values when activity is destroyed on configuration change
        state.putInt("reply_volume", volume)
        state.putInt("reply_minutes", minutes)
        state.putInt("reply_timer_volume", timerParams.getVolume())
        state.putLong("reply_timer_millis", timerParams.getMillis())
    }
*/

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

    /*fun increaseVolume(view: View?){
        /*timerParams.*/increaseVolume()
        //updateVolumeTextView()
    }

    // moving from TimerParameters
    fun increaseVolume() {
        if (volume < 100) {
            volume += volumeIncrement
        }
        if (volume > 100) {
            volume = 100
        }
    }


    fun decreaseVolume(view: View?) {
        /*timerParams.*/decreaseVolume()
        //updateVolumeTextView()
    }*/

    /*fun updateVolumeTextView() {
    fun decreaseVolume() {
        if (volume > 0) {
            volume -= volumeIncrement
        }
        if (volume < 0) {
            volume = 0
        }
    }

    // moved from TimerParameters
    fun increaseMinutes() {
        minutes += minutesIncrement
    }

    // moved from TimerParameters
    fun decreaseMinutes() {
        if (minutes > 0) {
            minutes -= minutesIncrement
        }
        if (minutes < 0) {
            minutes = 0
        }
    }

        val showVolumeTextView = findViewById(R.id.textVolume) as TextView
        showVolumeTextView.text = (String.format("%3d", volume/*timerParams.getVolume()*/) + "%")

        val imgVolume = findViewById(R.id.imgVolume) as ImageView

        when (/*timerParams.getVolume()*/volume) {
            0 -> imgVolume.setImageResource(R.drawable.mute)
            in 1..25 -> imgVolume.setImageResource(R.drawable.volume_min)
            in 26..50 -> imgVolume.setImageResource(R.drawable.volume_med)
            else -> imgVolume.setImageResource(R.drawable.volume_max)
        }
    }*/

    fun updateMinutesTextView() {
        val hours_format: Int
        val minutes_format: Int

        hours_format = timerParams.getMinutes() / 60
        minutes_format = timerParams.getMinutes() % 60

        val showTimerTextView = findViewById(R.id.textTimer) as TextView
        showTimerTextView.text = (String.format("%02d:%02d", hours_format, minutes_format))

    }

    fun increaseMinutes(view: View?) {
        timerParams.increaseMinutes()
        updateMinutesTextView()
    }

    fun decreaseMinutes(view: View?) {
        timerParams.decreaseMinutes()
        updateMinutesTextView()
    }
/*
    fun increaseMinutes() {
        minutes += minutesIncrement
    }

    // moved from TimerParameters
    fun decreaseMinutes() {
        if (minutes > 1) {
            minutes -= minutesIncrement
        }
        if (minutes < 1) {
            minutes = 1
        }
    }
*/
    fun updateTimerButtonImage() {
        val imgBtnMain = findViewById<View>(R.id.btnMain) as ImageButton
        if (mTimer?.isRunning()!!) {
            imgBtnMain.setImageResource(R.drawable.stop)
        } else {
            imgBtnMain.setImageResource(R.drawable.start)
        }
    }

    fun toggleTimer(view: View) { //suggesting this be renamed in the upcoming refactor to reflect fact that it now starts or stops timer (toggleTimer perhaps?)
        if (mTimer?.isRunning()!!) {
            val myToast = Toast.makeText(this, "I will cancel", Toast.LENGTH_SHORT)
            myToast.show() //delete this Toast when interface makes cancellation clear.
//            mTimer?.unsubscribe(this)
            mTimer?.cancelMainTimer()
        } else if (AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC) <= timerParams.getVolume()){  // Move to MainTimer!!!
            val myToast = Toast.makeText(this, "Nothing to change", Toast.LENGTH_SHORT)
            myToast.show() //delete this Toast when interface makes cancellation clear.
        }
        else
        {
            //val am: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            //timerRunningParams.set((minutes * 60000).toLong(), volume)
            timerRunningParams.set(timerParams.getMillis(), timerParams.getVolume())

            var numIntervals: Int
            /*numIntervals = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC) - AudioTimerMath.percentageToVolume(timerParams.getVolume())
            if (numIntervals < 0) {
                numIntervals = 0
            }*/
            //          mTimer?.subscribe(this)
            mTimer?.startMainTimer(timerRunningParams)
        }
        updateTimerButtonImage()
    }

    override fun onTimerFinished() {
        handler.post(object : Runnable {
            override fun run() {
                val myToast = Toast.makeText(this@MainActivity, "Timer is finished!", Toast.LENGTH_SHORT)
                myToast.show() //delete this Toast when interface another message about finished timer pops up.
                updateTimerButtonImage()
            }
        })
    }

    override fun onVolumeChange(newVolume: Int) {
        val curVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)//AudioTimerMath.currentVolumeToPercentage()

        handler.post(object : Runnable {
            override fun run() {
                val myToast = Toast.makeText(this@MainActivity, "Current volume: $curVolume%", Toast.LENGTH_SHORT)
       //         myToast.show() //delete this Toast when interface is updated with current state.
                timerParams.setVolume(curVolume)
                // update slider!!!
                //updateVolumeTextView()
                VolumeSlider.changeVolumeSliderToCurrent(greyedVolseekBar)
            }
        })
    }

    override fun onOutsideVolumeChange(newVolume: Int, lastAppChangeVolume: Int) {

        val curSystemVolume = AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC)

        handler.post(object : Runnable {
            override fun run() {
                val myToast = Toast.makeText(this@MainActivity, "Volume changed outside: $newVolume", Toast.LENGTH_SHORT)
            //    myToast.show() //delete this Toast when interface another message about finished timer pops up.
                Log.d("MainActivity ", "volume changed outside to " + newVolume + ", system volume " + curSystemVolume)

                timerParams.setVolume(newVolume)//volume = newVolume
                // update slider!!!
                //updateVolumeTextView()
                VolumeSlider.changeVolumeSliderToCurrent(greyedVolseekBar)

                if (mTimer?.isRunning()!!) {
                    //  if volume is increased: recalculate timer by default, remind user they can cancel timer
                    //  if volume is decreased but higher then target: recalculate timer by default, remind user they can cancel timer
                    if (newVolume > timerRunningParams.getVolume()) {

                       mTimer?.cancelMainTimer()
                        timerRunningParams.setMillis(timerRunningParams.getMillis() - mTimer?.getProgress()!!)
                       Log.d("MainActivity ", "recalculating")
                       val myToast = Toast.makeText(this@MainActivity, "Volume is changed, timer is recalculated", Toast.LENGTH_SHORT)
                       mTimer?.startMainTimer(timerRunningParams)
                    }
                    else if (newVolume <= timerRunningParams.getVolume()){
                        mTimer?.cancelMainTimer()
                        Log.d("MainActivity ", "cancelling")
                        val myToast = Toast.makeText(this@MainActivity, "Volume is decreased, timer is cancelled", Toast.LENGTH_SHORT)
                    }
                }
                updateTimerButtonImage()
            }
        })

    }
}
