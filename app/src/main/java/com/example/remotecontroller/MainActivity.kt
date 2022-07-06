package com.example.remotecontroller


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val admin = "siyah"
        val adminPwd = "siyaharge1"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        var username = findViewById<EditText>(R.id.text_username)
        var password = findViewById<EditText>(R.id.text_password)
        var login = findViewById<Button>(R.id.btn_login)

        login.setOnClickListener {
            if ((username.equals(admin)) && (password.equals(adminPwd)))


        }










    }

    private fun setContentView(companion: MainLogin.Companion) {

    }


}