package com.example.examen01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import java.util.Calendar
import android.content.pm.ActivityInfo
import android.content.Intent

import android.widget.ArrayAdapter;
import android.widget.Spinner;


class MainActivity : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var fecNacido: DatePicker
    private lateinit var apuesta: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        setContentView(R.layout.activity_main)

        // Inicializar el Spinner
        val spinnerDados: Spinner = findViewById(R.id.spinnerDados)

        // Crear una lista de valores para el spinner
        val datos = listOf("2", "3")

        // Crear un adaptador para el spinner
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, datos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al spinner
        spinnerDados.adapter = adapter

        txtNombre   = findViewById(R.id.txtNombre)
        fecNacido   = findViewById(R.id.datePickerNacido)
        apuesta =findViewById(R.id.txtApuesta)
    }

    fun ingresar(view: View) {
        var aplicar = true

        // Validar el nombre
        if (txtNombre.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Nombre requerido", Toast.LENGTH_SHORT).show()
            txtNombre.requestFocus()
            aplicar = false
        }

        // Validar la fecha de nacimiento (al menos 21 años)
        val calendar = Calendar.getInstance()
        val year = fecNacido.year
        val currentYear = calendar.get(Calendar.YEAR)

        if ((currentYear - year) < 21) {
            Toast.makeText(this, "Debes ser mayor de 21 años", Toast.LENGTH_SHORT).show()
            aplicar = false
        }

        // Validar la apuesta (mayor o igual a 2,000,000)
        val apuestaValue = apuesta.text.toString().toIntOrNull()
        if (apuestaValue == null || apuestaValue < 2000000) {
            Toast.makeText(this, "La apuesta debe ser al menos 2 000 000", Toast.LENGTH_SHORT).show()
            apuesta.requestFocus()
            aplicar = false
        }

        // Si todo es válido
        if (aplicar) {

            val spinnerDados: Spinner = findViewById(R.id.spinnerDados)
            val dadosSeleccionados = spinnerDados.selectedItem.toString() // Obtiene el valor seleccionado


            // Crear un Intent para iniciar ComposeActivity
            val intent = Intent(this, ComposeActivity::class.java).apply {
                putExtra("nombre", txtNombre.text.toString())
                putExtra("apuesta", apuestaValue)
                putExtra("dados", dadosSeleccionados.toInt())
            }
            startActivity(intent)
        }
    }
}