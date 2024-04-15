package com.example.spotvibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val Recordame = findViewById<CheckBox>(R.id.checkbox_remember_me)
        Recordame.isEnabled
        val intent= Intent(this, Home::class.java)

        val intent2= Intent(this, CrearCuenta::class.java)
        val intent3=Intent(this, ForgotPassword::class.java)
        val intent4= Intent(this, HomeDuenio::class.java)
        val imageButton=findViewById<Button>(R.id.button_login)
        imageButton.setOnClickListener{
            if(Recordame.isChecked){
                startActivity(intent4)
            }
            else {
                startActivity(intent)
            }
        }
        val linkCreate =findViewById<TextView>(R.id.text_create_account)
        linkCreate.setOnClickListener {
            startActivity(intent2)
        }
        val linkforgot=findViewById<TextView>(R.id.textview_forgot_password)
        linkforgot.setOnClickListener {
            startActivity(intent3)
        }
    }
}