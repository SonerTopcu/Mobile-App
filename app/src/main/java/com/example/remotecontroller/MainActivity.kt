package com.example.remotecontroller

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.remotecontroller.manager.MQTTmanager
import com.example.remotecontroller.protocols.UIUpdaterInterface
import org.eclipse.paho.android.service.MqttAndroidClient


class MainActivity : AppCompatActivity(), UIUpdaterInterface {

    var mqtTmanager:MQTTmanager? = null
    var brokerInf = findViewById<EditText>(R.id.brokerField)

    override fun resetUIWithConnection(status: Boolean) {



    }


    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var mqttClient: MqttAndroidClient
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }



    override fun updateStatusViewWith(status: String) {
        TODO("Not yet implemented")
    }

    override fun update(message: String) {
        TODO("Not yet implemented")
    }

}



