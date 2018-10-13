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

    private var timerParams = TimerParameters()
    private var timerRunningParams = TimerParameters(true)

    private val handlerProgress = Handler()
    internal lateinit var tv: TextView
    private var timerProgress : Timer? = null

    // enum that defines what button is hold so the TimerRunnable object could increases/decreases volume/minutes
    enum class ButtonAction {
        HOUR_UP, HOUR_DOWN, TIMER_UP, TIMER_DOWN
    }

    // class for to update view from the timer thread when a button is hold
    internal inner class TimerRunnable : Runnable {

        var buttonAction: ButtonAction = ButtonAction.HOUR_UP

        override fun run() {
            if (buttonAction == ButtonAction.HOUR_UP)
                timerParams.increaseHours()
            else if (buttonAction == ButtonAction.HOUR_DOWN)
                timerParams.decreaseHours()
            else if (buttonAction == ButtonAction.TIMER_UP)
                timerParams.increaseMinutes()
            else if (buttonAction == ButtonAction.TIMER_DOWN)
                timerParams.decreaseMinutes()

            updateMinutesTextView(timerParams.getSeconds())
        }

    }

    // class for listeners when a button is hold
    internal inner class TimerOnTouchListener : View.OnTouchListener {

        var buttonAction: ButtonAction = ButtonAction.HOUR_UP

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
        mTimer?.subscribe(this)

        outsideVolumeListener = (application as AudioTimerApplication).getListener()
        outsideVolumeListener?.startListening(this)

        TimerProgressBar.setBar(circularProgressbar)

        if (savedInstanceState != null) {
            timerParams.loadFromBundle(savedInstanceState)
            timerRunningParams.loadFromBundle(savedInstanceState)
        }
        else {
            timerParams.loadInitialSetting()
            timerRunningParams.loadInitialSetting()
            TimerProgressBar.resetValues()
        }

        updateMinutesTextView(timerParams.getSeconds())
        updateTimerButtonImage()

        val imgBtnMain = findViewById<View>(R.id.btnMain) as ImageButton

        imgBtnMain.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                updateTimerButtonImage()
            } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (!mTimer?.isRunning()!!) {
                    imgBtnMain.setImageResource(R.drawable.start_word_pressed2)
                } else {
                    imgBtnMain.setImageResource(R.drawable.stop_word_pressed2)
                }
            }
            false
        })

        val viewTimerUp = findViewById<View>(R.id.imgBtnTimerUp) as View
        val timerOnTouchListenerTimerUp = TimerOnTouchListener()
        timerOnTouchListenerTimerUp.buttonAction = ButtonAction.TIMER_UP
        viewTimerUp.setOnTouchListener(timerOnTouchListenerTimerUp)

        val viewTimerDown = findViewById<View>(R.id.imgBtnTimerDown) as View
        val timerOnTouchListenerTimerDown = TimerOnTouchListener()
        timerOnTouchListenerTimerDown.buttonAction = ButtonAction.TIMER_DOWN
        viewTimerDown.setOnTouchListener(timerOnTouchListenerTimerDown)

        val viewHoursUp = findViewById<View>(R.id.imgBtnHoursUp) as View
        val timerOnTouchListenerHoursUp = TimerOnTouchListener()
        timerOnTouchListenerHoursUp.buttonAction = ButtonAction.HOUR_UP
        viewHoursUp.setOnTouchListener(timerOnTouchListenerHoursUp)

        val viewHoursDown = findViewById<View>(R.id.imgBtnHoursDown) as View
        val timerOnTouchListenerHoursDown = TimerOnTouchListener()
        timerOnTouchListenerHoursDown.buttonAction = ButtonAction.HOUR_DOWN
        viewHoursDown.setOnTouchListener(timerOnTouchListenerHoursDown)

        val imgVolume = findViewById(R.id.imgVolume) as ImageView
        VolumeSlider.resetValues(targetVolSeekBar, greyedVolseekBar, originalVolseekBar, timerParams, imgVolume,mTimer)

        val res = resources
        val drawable = res.getDrawable(R.drawable.circular)
        circularProgressbar.progressDrawable = drawable
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.unsubscribe(this)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        timerParams.saveToBundle(outState)
        timerRunningParams.saveToBundle(outState)
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

    fun updateMinutesTextView(seconds: Int) {
        val hours_format: Int
        val minutes_format: Int
        val seconds_format: Int

        hours_format = seconds / 3600 //timerParams.getMinutes() / 60
        minutes_format = (seconds - hours_format * 3600)/ 60
        seconds_format = (seconds - hours_format * 3600 - minutes_format * 60) % 60//timerParams.getMinutes() % 60


        textTimer.text = (String.format("%02d:%02d", hours_format, minutes_format))
        textSeconds.text = (String.format("%02d", seconds_format))

    }

    fun increaseMinutes(view: View?) {
        timerParams.increaseMinutes()
        updateMinutesTextView(timerParams.getSeconds())
    }

    fun decreaseMinutes(view: View?) {
        timerParams.decreaseMinutes()
        updateMinutesTextView(timerParams.getSeconds())
    }

    fun increaseHours(view: View?) {
        timerParams.increaseHours()
        updateMinutesTextView(timerParams.getSeconds())
    }

    fun decreaseHours(view: View?) {
        timerParams.decreaseHours()
        updateMinutesTextView(timerParams.getSeconds())
    }

    fun updateTimerButtonImage() {
        //val imgBtnMain = findViewById<View>(R.id.btnMain) as ImageButton
        if (mTimer?.isRunning()!!) {
            btnMain.setImageResource(R.drawable.stop_word1)
            imgBtnTimerUp.isEnabled = false
            imgBtnTimerDown.isEnabled = false
            imgBtnHoursUp.isEnabled = false
            imgBtnHoursDown.isEnabled = false
        } else {
            btnMain.setImageResource(R.drawable.start_word1)
            imgBtnTimerUp.isEnabled = true
            imgBtnTimerDown.isEnabled = true
            imgBtnHoursUp.isEnabled = true
            imgBtnHoursDown.isEnabled = true
        }
    }

    fun toggleTimer(view: View) { //suggesting this be renamed in the upcoming refactor to reflect fact that it now starts or stops timer (toggleTimer perhaps?)
        VolumeSlider.realignMoons()
        VolumeSlider.resetAfterTimerCancel()
        if (mTimer?.isRunning()!!) {
            val myToast = Toast.makeText(this, "I will cancel", Toast.LENGTH_SHORT)
            myToast.show() //delete this Toast when interface makes cancellation clear.
            mTimer?.cancelMainTimer()
            fullReset()
/*            cancelCheckingProgress()
            TimerProgressBar.resetValues()
            updateProgress()
            updateMinutesTextView(timerParams.getSeconds())*/
            Log.d("MainActivity ", "I will cancel")
            Log.d("Check", "1")

        } else if (AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC) <= timerParams.getVolume()){  // Move to MainTimer!!!
            val myToast = Toast.makeText(this, "Nothing to change", Toast.LENGTH_SHORT)
            myToast.show() //delete this Toast when interface makes cancellation clear.
            Log.d("Check", "2")
        }
        else
        {
            timerRunningParams.set(timerParams.getMillis(), timerParams.getVolume())
            TimerProgressBar.setMaxValues((timerRunningParams.getMillis()/1000).toInt())
            mTimer?.startMainTimer(timerRunningParams)
            startCheckingProgress()
            Log.d("Check", "3")
        }
        updateTimerButtonImage()
        Log.d("Check", "4")
    }

    override fun onTimerFinished() {
        handler.post(object : Runnable {
            override fun run() {
                val myToast = Toast.makeText(this@MainActivity, "Timer is finished!", Toast.LENGTH_SHORT)
                myToast.show() //delete this Toast when interface another message about finished timer pops up.
                fullReset()
/*                updateTimerButtonImage()
                updateProgress()
                updateMinutesTextView(timerParams.getSeconds())
                cancelCheckingProgress()
                TimerProgressBar.resetValues()
                VolumeSlider.resetAfterTimerCancel()
*/
                Log.d("Check", "6")
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
                VolumeSlider.changeVolumeSliderToCurrent(greyedVolseekBar)
                TimerProgressBar.setProgress(((timerRunningParams.getLeftMills() - mTimer?.getProgress()!!)/1000).toInt())
                Log.d("Check", "7")
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
                VolumeSlider.changeVolumeSliderToCurrent(greyedVolseekBar)

                if (mTimer?.isRunning()!!) {
                    //  if volume is increased: recalculate timer by default, remind user they can cancel timer
                    //  if volume is decreased but higher then target: recalculate timer by default, remind user they can cancel timer
                    Log.d("MainActivity ", "timer is running")
                    Log.d("Check", "8")
                    if (newVolume > timerRunningParams.getVolume()) {

                        timerRunningParams.setMillis(timerRunningParams.getMillis() - mTimer?.getProgress()!!)
                        timerRunningParams.setLeftMills(mTimer?.getProgress()!!)
                        timerRunningParams.setStartParams() //startTime + startVolume
                        Log.d("MainActivity ", "recalculating")
                        val myToast = Toast.makeText(this@MainActivity, "Volume is changed, timer is recalculated", Toast.LENGTH_SHORT)
                        Log.d("Check", "9")
                    }
                }
                updateTimerButtonImage()
                Log.d("Check", "10")
            }
        })
    }

    fun startCheckingProgress(){
        timerProgress = Timer("Timer progress", false)
        timerProgress?.schedule(object : TimerTask() {
            override fun run() {
                updateProgress()
                }
            },  timerProgressPeriod.toLong(), timerProgressPeriod.toLong())

    }

    private fun updateProgress() {
        if (mTimer?.isRunning()!!){

            handlerProgress.post(object : Runnable{
                override fun run() {
                    var tmp = ((timerRunningParams.getLeftMills() - mTimer?.getProgress()!!)/1000).toInt()
                    TimerProgressBar.setProgress(tmp)
                    updateMinutesTextView(tmp)
                }
            })
        }
    }

    fun cancelCheckingProgress(){
        timerProgress?.cancel() //the ? is the safe call operator in Kotlin
        timerProgress = null
    }

    fun fullReset(){ //timer is finished or cancelled
        cancelCheckingProgress()
        updateTimerButtonImage()
        updateMinutesTextView(timerParams.getSeconds())
        TimerProgressBar.resetValues()
        VolumeSlider.resetAfterTimerCancel()
        VolumeSlider.realignMoons()
        VolumeSlider.resetAfterTimerCancel()
    }
}
