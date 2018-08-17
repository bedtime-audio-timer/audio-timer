package com.bedtime_audio_timer.audiotimer

val atMath = AudioTimerMath()
public var m_volume: Int = 0 // Target volume
public var m_minutes: Int = 0 // Target minutes
public var timerIsRunning: Boolean = false
// timerObjectDelay and timerObjectPeriod for Timer object when UP/DOWN buttons are hold that define its schedule
public var timerObjectDelay = 150
public var timerObjectPeriod = 150
public var volumeIncrement = 1 // Volume increment on UP/DOWN button
public var minutesIncrement = 1 // Minutes increment on UP/DOWN button