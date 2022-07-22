package com.example.remotecontroller

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.remotecontroller.manager.MQTTConnectionParams
import com.example.remotecontroller.manager.MQTTmanager
import com.example.remotecontroller.protocols.UIUpdaterInterface
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_remote.*


class MainActivity : AppCompatActivity(), UIUpdaterInterface {
    var mqttManager:MQTTmanager? = null
    var status = 0
    var mod = 18

    override fun resetUIWithConnection(status: Boolean) {
        brokerField.isEnabled = !status
        topicField.isEnabled = !status
        connectBtn.isEnabled = !status

        if (status){
            updateStatusViewWith("Connected")
        }else{
            updateStatusViewWith("Disconnected")
        }
    }

    override fun updateStatusViewWith(status: String) {
        statusView.text = status

        if (status == "Connected")
            setContentView(R.layout.activity_remote)

    }

    override fun update(message: String): String {
        return message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resetUIWithConnection(false)
    }

    fun connect(view: View){

        if (!(brokerField.text.isNullOrEmpty() && topicField.text.isNullOrEmpty())) {
            var host = "tcp://broker.hivemq.com:1883"
            var topic = topicField.text.toString()
            var connectionParams = MQTTConnectionParams("MQTTSample",host,topic,"","")
            mqttManager = MQTTmanager(connectionParams,applicationContext,this)
            mqttManager?.connect()
        }else{
            updateStatusViewWith("Please enter all valid fields")
        }
    }



    fun sendMessagePowerOff(view: View){
        var text1 = "Power Off"
        status = 0
        infoView.text = null
        mqttManager?.publish(text1)
        mod = 18
    }

    fun sendMessageTempUp(view: View){
        var text1 = "Temp Up"
        if (status == 1 && mod < 30){
            mod += 1
            infoView.text = mod.toString()
        }
        mqttManager?.publish(text1)
    }

    fun sendMessageTempDown(view: View){
        var text1 = "Temp Down"
        if (status == 1 && mod >= 19){
            mod -= 1
            infoView.text = mod.toString()
        }
        mqttManager?.publish(text1)
    }
}






