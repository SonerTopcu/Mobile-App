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
    private var mqttManager:MQTTmanager? = null
    private var status = 0
    private var mode = 0
    private val timestamp = (System.currentTimeMillis() / 1000).toInt()
    private var message2 = "null"


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
        if (status == "Connected"){
            setContentView(R.layout.activity_remote)
        }
    }

    override fun update(message: String) {
        if(message.toInt() < 31)
            message2 = message
        else(timestamp >= message.toInt())
            mode = 1
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
            if (topic == "siyaharge"){
                var connectionParams = MQTTConnectionParams("MQTTSample",host,topic,"","")
                mqttManager = MQTTmanager(connectionParams,applicationContext,this)
                mqttManager?.connect()
            }
        }else{
            updateStatusViewWith("Please enter all valid fields")
        }
    }

    fun statusPowerOn(view: View){
        var text = "Power On"
        mqttManager?.publish(text)

        if(message2 == "18" && status != 1 && mode == 1){
            status = 1
            infoView.text = message2
        }
        else
            messageHistoryView.text = "Orange pi OFFLINE"
    }

    fun statusPowerOff(view: View){
        var text = "Power Off"
        mqttManager?.publish(text)

        if(message2 == "17" && status == 1 && mode == 1){
            status = 0
            infoView.text = "OFF"
        }
        else
            messageHistoryView.text = "Orange pi OFFLINE"
    }

    fun statusTempUp(view: View){
        var text = "Temp Up"
        mqttManager?.publish(text)

        if(message2.toInt() < 30 && status == 1 && mode == 1)
            infoView.text = message2
        else
            messageHistoryView.text = "Orange pi OFFLINE"
    }

    fun statusTempDown(view: View){
        var text = "Temp Down"
        mqttManager?.publish(text)

        if(message2.toInt() > 18 && status == 1 && mode == 1)
            infoView.text = message2
        else
            messageHistoryView.text = "Orange pi OFFLINE"
    }
}