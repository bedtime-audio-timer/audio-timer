package com.bedtime_audio_timer.audiotimer

import android.annotation.SuppressLint
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
//import com.bedtime_audio_timer.audiotimer.R.id.timerProgressBar
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

    internal var pStatus = 100

    private val handlerProgress = Handler()
    internal lateinit var tv: TextView
    private var timerProgress : Timer? = null // this object is used to increases/decreases volume/minutes when a button is hold

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
                updateMinutesTextView(timerParams.getSeconds())
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
        updateMinutesTextView(timerParams.getSeconds())
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

/*
        val viewVolumeUp = findViewById<View>(R.id.imgBtnVolumeUp) as View
        val timerOnTouchListenerVolumeUp = TimerOnTouchListener()
        timerOnTouchListenerVolumeUp.buttonAction = ButtonAction.VOLUME_UP
        imgBtnVolumeUp.setOnTouchListener(timerOnTouchListenerVolumeUp)

        val viewVolumeDown = findViewById<View>(R.id.imgBtnVolumeDown) as View
        val timerOnTouchListenerVolumeDown = TimerOnTouchListener()
        timerOnTouchListenerVolumeDown.buttonAction = ButtonAction.VOLUME_DOWN
        viewVolumeDown.setOnTouchListener(timerOnTouchListenerVolumeDown)
*/
        val viewTimerUp = findViewById<View>(R.id.imgBtnTimerUp) as View
        val timerOnTouchListenerTimerUp = TimerOnTouchListener()
        timerOnTouchListenerTimerUp.buttonAction = ButtonAction.TIMER_UP
        viewTimerUp.setOnTouchListener(timerOnTouchListenerTimerUp)

        val viewTimerDown = findViewById<View>(R.id.imgBtnTimerDown) as View
        val timerOnTouchListenerTimerDown = TimerOnTouchListener()
        timerOnTouchListenerTimerDown.buttonAction = ButtonAction.TIMER_DOWN
        viewTimerDown.setOnTouchListener(timerOnTouchListenerTimerDown)

        val imgVolume = findViewById(R.id.imgVolume) as ImageView
        VolumeSlider.resetValues(targetVolSeekBar, greyedVolseekBar, originalVolseekBar, timerParams, imgVolume,mTimer)

        // TimerProgressBar.resetValues(circularProgressbar, 100)

        val res = resources
        val drawable = res.getDrawable(R.drawable.circular)
        circularProgressbar.progressDrawable = drawable

/*        val mProgress = findViewById<View>(R.id.circularProgressbar) as ProgressBar
        mProgress.progress = 0   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress
        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable


        tv = findViewById<View>(R.id.tv) as TextView
        Thread(Runnable {
            // TODO Auto-generated method stub
            while (pStatus > 0) {
                pStatus -= 1

                handler.post {
                    // TODO Auto-generated method stub
                    mProgress.progress = pStatus//(mTimer?.getProgress()!!*100/timerRunningParams.getMillis()).toInt()//pStatus
                    tv.text = pStatus.toString() + "%"
                }
                try {
                    // Sleep for 200 milliseconds.
                    // Just to display the progress slowly
                    Thread.sleep(25) //thread will take approx 3 seconds to finish
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }).start()
*/
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


        textTimer.text = (String.format("%02d:%02d:%02d", hours_format, minutes_format, seconds_format))

    }

    fun increaseMinutes(view: View?) {
        timerParams.increaseMinutes()
        updateMinutesTextView(timerParams.getSeconds())
    }

    fun decreaseMinutes(view: View?) {
        timerParams.decreaseMinutes()
        updateMinutesTextView(timerParams.getSeconds())
    }

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
            cancelCheckingProgress()
            //updateMinutesTextView(timerParams.getSeconds())
        } else if (AudioManagerSingleton.am.getStreamVolume(AudioManager.STREAM_MUSIC) <= timerParams.getVolume()){  // Move to MainTimer!!!
            val myToast = Toast.makeText(this, "Nothing to change", Toast.LENGTH_SHORT)
            myToast.show() //delete this Toast when interface makes cancellation clear.
        }
        else
        {
            timerRunningParams.set(timerParams.getMillis(), timerParams.getVolume())

            circularProgressbar.max = (timerRunningParams.getMillis()/1000).toInt()
            circularProgressbar.progress = (timerRunningParams.getMillis()/1000).toInt()//0   // Main Progress
            circularProgressbar.secondaryProgress = (timerRunningParams.getMillis()/1000).toInt() // Secondary Progress

            mTimer?.startMainTimer(timerRunningParams)
            startCheckingProgress()
        }
        updateTimerButtonImage()
    }

    override fun onTimerFinished() {
        handler.post(object : Runnable {
            override fun run() {
                val myToast = Toast.makeText(this@MainActivity, "Timer is finished!", Toast.LENGTH_SHORT)
                myToast.show() //delete this Toast when interface another message about finished timer pops up.
                updateTimerButtonImage()
                updateProgress()
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
                TimerProgressBar.setProgress(circularProgressbar, (mTimer?.getProgress()!!/1000).toInt())
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
                       cancelCheckingProgress()
                       timerRunningParams.setMillis(timerRunningParams.getMillis() - mTimer?.getProgress()!!)
                       Log.d("MainActivity ", "recalculating")
                       val myToast = Toast.makeText(this@MainActivity, "Volume is changed, timer is recalculated", Toast.LENGTH_SHORT)
                       mTimer?.startMainTimer(timerRunningParams)
                       startCheckingProgress()
                    }
                    else if (newVolume <= timerRunningParams.getVolume()){
                        mTimer?.cancelMainTimer()
                        cancelCheckingProgress()
                        Log.d("MainActivity ", "cancelling")
                        val myToast = Toast.makeText(this@MainActivity, "Volume is decreased, timer is cancelled", Toast.LENGTH_SHORT)
                    }
                }
                updateTimerButtonImage()
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
                    var tmp = (mTimer?.getProgress()!!/1000).toInt()
                    circularProgressbar.progress = tmp
                    updateMinutesTextView(tmp)
                }
            })

        }
    }

    fun cancelCheckingProgress(){
        timerProgress?.cancel() //the ? is the safe call operator in Kotlin
        timerProgress = null
    }

}
