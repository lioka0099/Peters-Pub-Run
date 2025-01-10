package com.example.collisions_game.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.collisions_game.interfaces.TiltCallback

class TiltDetector(context: Context, private val tiltCallback: TiltCallback) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
    private lateinit var sensorEventListener: SensorEventListener

    private var timestamp: Long = 0L
    private var baseY : Float = 0f

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                calculateTilt(x, y)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //pass
            }
        }
    }

    private fun calculateTilt(x: Float, y: Float) {
        val adjustedY = y - baseY
        if(System.currentTimeMillis() - timestamp >= 300){
            timestamp = System.currentTimeMillis()
            if(x >= 3.0){
                tiltCallback?.tiltX(true)
            } else if(x <= -3.0){
                tiltCallback.tiltX(false)
            }
            if(adjustedY < -2.0){
                tiltCallback?.tiltY(true)
            } else if(adjustedY > 2.0){
                tiltCallback.tiltY(false)
            }
        }
    }

    fun calibrate() {
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                baseY = event.values[1]
                sensorManager.unregisterListener(this)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //pass
            }
        }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun start(){
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop(){
        sensorManager.unregisterListener(sensorEventListener, sensor)
    }
}