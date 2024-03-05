package com.example.spotvibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CrearCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)
        val intent= Intent(this, MainActivity::class.java)
        val imageButton=findViewById<Button>(R.id.createAccountButton)
        imageButton.setOnClickListener{
            startActivity(intent)
        }
    }
}