package com.example.examen01

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        setContentView(R.layout.activity_main2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val monto_inicial = intent.getFloatExtra("monto_inicial", 0f) // Cambiado a Float
        val monto_disponible = intent.getFloatExtra("monto_disponible", 0f) // Cambiado a Float

        val statusMessage: TextView = findViewById(R.id.statusMessage)

        if (monto_inicial < monto_disponible) {
            statusMessage.text = getString(R.string.game_status_01) // "Eres un ganador"
        } else if (monto_inicial == monto_disponible) {
            statusMessage.text = getString(R.string.game_status_02) // "Te salvaste..."
        } else if (monto_disponible < monto_inicial) {
            statusMessage.text = getString(R.string.game_status_03) // "No deberías de jugar... Retírate"
        }

        if (monto_disponible == 0f) { // Comparación con 0 como Float
            statusMessage.text = getString(R.string.game_status_04) // "Lo perdiste todo... No vuelvas a jugar!"
        }



    }
}