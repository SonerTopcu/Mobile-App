package com.example.remotecontroller


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnect

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var mqttClient: MqttAndroidClient
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        val admin = "siyah"
        val adminPwd = "siyaharge1"
        var username = findViewById<EditText>(R.id.text_username)
        var password = findViewById<EditText>(R.id.text_password)
        var login = findViewById<Button>(R.id.btn_login)
        var sample = findViewById<EditText>(R.id.text_sample)

        login.setOnClickListener {
            sample.setText("Poki")

                val serverURI = "tcp://broker.hivemq.com:1883"
            mqttClient = MqttAndroidClient(applicationContext, serverURI, "kotlin_client")
                mqttClient.setCallback(object : MqttCallback {
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        Log.d(MainLogin.TAG, "Receive message: ${message.toString()} from topic: $topic")
                    }

                    override fun connectionLost(cause: Throwable?) {
                        Log.d(MainLogin.TAG, "Connection lost ${cause.toString()}")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {

                    }
                })
                val options = MqttConnectOptions()
                try {
                    mqttClient.connect(options, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(MainLogin.TAG, "Connection success")
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(MainLogin.TAG, "Connection failure")
                        }
                    })
                } catch (e: MqttException) {
                    e.printStackTrace()
                }





        }



        }


}



