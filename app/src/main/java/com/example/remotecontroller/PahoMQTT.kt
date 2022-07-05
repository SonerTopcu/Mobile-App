package com.example.remotecontroller

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttException

private lateinit var mqttAndroidClient: MqttAndroidClient

class PahoMQTT {

    fun getApplicationContext() {
        val applicationContextContext = "@+id/button2"
        assertEquals("com.example.remotecontroller", appContext.packageName)
    }

    fun connect(applicationContext: Context) {
        mqttAndroidClient = MqttAndroidClient (context.applicationContext,"YOUR MQTT BROKER ADDRESS","YOUR CLIENT ID" )
        try {
            val token = mqttAndroidClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken)                        {
                    Log.i("Connection", "success ")
                    //connectionStatus = true
                    // Give your callback on connection established here
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    Log.i("Connection", "failure")
                    // Give your callback on connection failure here
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }

}