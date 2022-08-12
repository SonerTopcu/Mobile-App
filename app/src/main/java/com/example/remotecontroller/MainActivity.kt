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
    val mode = 0
    var mod = 18
    val timestamp = System.currentTimeMillis() / 1000
    var message2 = "null"


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
            mqttManager?.publish(timestamp.toString())
            setContentView(R.layout.activity_remote)
        }
    }

    override fun update(message: String) {
        val map = message.split(",").associate {
            val (left, right) = it.split(":")
            left to right.toInt()}

        val mapIndex = message.split(",").associate {
            val (left, right) = it.split(":")
            left to right.toInt()}




         /*= message
        messageHistoryView.text = "BOJİ"
        if (message == mode.toString()){
            infoView.text = "OFF"
        }
        else{
            mod = message.
            status = 1
            infoView.text = mod.toString()
          */
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


    private fun updateInfo() {
        /*if (mod == 10){
            infoView.text = ""
        }
        else
            mod = message2!!.toInt()
            status = 1
            infoView.text = mod.toString()

         */
    }

    fun statusPowerOn(view: View){
        var text = "Power On"
        status = 1
        mqttManager?.publish(text)
        infoView.text = mod.toString()

    }

    fun statusPowerOff(view: View){
        var text = "Power Off"
        status = 0
        mqttManager?.publish(text)
        infoView.text = ""
    }

    fun statusTempUp(view: View){
        var text = "Temp Up"

        if(mod < 30 && status == 1){
            mod += 1
            mqttManager?.publish(text)
            infoView.text = mod.toString()
        }
    }

    fun statusTempDown(view: View){
        var text = "Temp Down"

        if(mod > 18 && status == 1){
            mod -= 1
            mqttManager?.publish(text)
            infoView.text = mod.toString()
        }
    }
}