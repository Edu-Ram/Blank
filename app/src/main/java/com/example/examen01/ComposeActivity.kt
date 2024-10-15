package com.example.examen01

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.examen01.ui.theme.ExamenTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlin.reflect.typeOf
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import android.app.Activity

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apuesta = intent.getIntExtra("apuesta", 0)
        val dados = intent.getIntExtra("dados", 2)

        setContent {
            ExamenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceRollerFunc(nombre, apuesta.toFloat(), dados)
                }
            }
        }
    }
}

data class DiceState(
    val value: Int = 1,
    val isSelected: Boolean = true,
    val isRolling: Boolean = false
)

data class GameState(
    val playerName: String,
    val score: Int = 0,
    val monto_inicial: Float,
    val monto_disponible: Float,
    val diceNumber: Int = 2,
    val wins: Int = 0
)

@Composable
fun DiceRollerFunc(nombre: String, apuesta: Float, dados: Int) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        DiceWithButtonAndImage(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            nombre = nombre,
            apuesta = apuesta,
            dados = dados
        )
    }
}

@Composable
fun DiceWithButtonAndImage(
    modifier: Modifier = Modifier, nombre: String, apuesta: Float, dados: Int
) {
    val context = LocalContext.current

    var numDados = dados

    var diceStates by remember {
        mutableStateOf(List(numDados) { index -> DiceState(isSelected = index < 2) })
    }

    var gameState by remember {
        mutableStateOf(GameState(playerName = nombre, monto_inicial= apuesta,monto_disponible = apuesta))
    }

    var montoApostado by remember { mutableStateOf("0") }

    var diceNumber by remember { mutableStateOf(gameState.diceNumber) }

    var selectedOption = remember { mutableStateOf(diceNumber) }

    var rollingDiceCount by remember { mutableStateOf(0) }

    var onRollFinished by remember { mutableStateOf({}) }


    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.app_name) + ": ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.player) + ": ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = gameState.playerName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.Available) + ": ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = gameState.monto_disponible.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.Bet) + ": ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextField(
                    value = montoApostado,
                    onValueChange = { newValue ->
                        // Validación para aceptar solo números de punto flotante
                        if (newValue.isEmpty() || newValue.matches(Regex("^[0-9]*\\.?[0-9]*\$"))) {
                            montoApostado = newValue
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                        .height(48.dp), // Ajusta la altura según sea necesario
                    textStyle = TextStyle(fontSize = 18.sp) // Asegura que el texto tenga el mismo tamaño
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.games_dices) + ": ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                diceStates.forEachIndexed { index, diceState ->
                    SingleDice(
                        diceState = diceState,
                        onDiceClick = {
                            if (index >= 2) {
                                diceStates = diceStates.mapIndexed { i, state ->
                                    if (i == index) {
                                        val newState = state.copy(isSelected = !state.isSelected)
                                        diceNumber += if (newState.isSelected) 1 else -1
                                        newState
                                    } else state
                                }
                            }
                        },
                        finishedRolling = {
                            diceStates = diceStates.mapIndexed { i, state ->
                                if (i == index) {
                                    state.copy(
                                        value = (1..6).random(),
                                        isRolling = false
                                    )
                                } else state
                            }

                            rollingDiceCount++

                            val selectedDiceCount = diceStates.count { it.isSelected }
                            if (rollingDiceCount == selectedDiceCount) {
                                onRollFinished()
                                rollingDiceCount = 0
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth() // Asegura que el Row ocupe todo el ancho disponible
                    .padding(16.dp), // Añade padding alrededor del Row
                horizontalArrangement = Arrangement.Center // Alinea el contenido en el centro horizontalmente
            ) {
                Button(
                    onClick = {
                        if (montoApostado.toFloat() != 0f) {
                            if (montoApostado.toFloat() <= gameState.monto_disponible) {

                                diceStates = diceStates.map { state ->
                                    if (state.isSelected) state.copy(isRolling = true)
                                    else state
                                }

                                onRollFinished = {
                                    val newSum = diceStates.filter { it.isSelected }
                                        .sumOf { it.value }

                                    gameState = gameState.copy(score = newSum)

                                    // Actualizar el monto_disponible después de verificar si el jugador ha acertado
                                    gameState = gameState.copy(
                                        monto_disponible =
                                        if (selectedOption.value == gameState.score) {
                                            gameState.monto_disponible + ((montoApostado.toFloat()*10) * (diceNumber - 1))
                                        } else {
                                            gameState.monto_disponible - montoApostado.toFloat()
                                        }
                                    ).copy(
                                        wins =
                                        if (selectedOption.value == gameState.score) {
                                            gameState.wins + 1
                                        } else {
                                            gameState.wins
                                        }
                                    )

                                    if (gameState.wins >= 3 || gameState.monto_disponible == 0f) {
                                        val intent = Intent(context, MainActivity2::class.java).apply {
                                            putExtra("monto_inicial", gameState.monto_inicial)
                                            putExtra("monto_disponible", gameState.monto_disponible)

                                        }
                                        context.startActivity(intent)
                                        (context as Activity).finish()
                                    }
                                }

                            } else {
                                // Mostrar el Toast cuando montoApostado es superior a monto disponible
                                Toast.makeText(context, "No posee los fondos suficientes para hacer esta apuesta", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Mostrar el Toast cuando montoApostado es 0
                            Toast.makeText(context, "Debe ingresar una cantidad a apostar", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.roll))
                }
            }



        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val maxOptions = diceNumber * 6
            val rows = (maxOptions - diceNumber + 1 + 3) / 4 // Calculamos el número de filas necesarias

            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 4) {
                        val i = diceNumber + row * 4 + col
                        if (i <= maxOptions) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = (selectedOption.value == i),
                                    onClick = { selectedOption.value = i },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(text = i.toString())
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp)) // Espacio entre filas
            }
        }

        Column(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (gameState.score != 0) {

                val imageResource = if (selectedOption.value == gameState.score) {
                    R.drawable.smileface
                } else {
                    R.drawable.sadface
                }

                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = if (selectedOption.value == gameState.score) "Cara feliz" else "Cara triste",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun SingleDice(
    diceState: DiceState,
    onDiceClick: () -> Unit,
    finishedRolling: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (diceState.isRolling) 360f * 5 else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            if (diceState.isRolling) {
                finishedRolling()
            }
        }
    )

    val scale by animateFloatAsState(
        targetValue = if (diceState.isRolling) 1f else 1.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val imageResource = when (diceState.value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    val colorMatrix = if (!diceState.isSelected) {
        ColorMatrix().apply {
            setToScale(0.5f, 0.5f, 0.5f, 1f)
        }
    } else {
        ColorMatrix().apply {
            setToScale(1f, 1f, 1f, 1f)
        }
    }

    Image(
        painter = painterResource(imageResource),
        contentDescription = diceState.value.toString(),
        colorFilter = ColorFilter.colorMatrix(colorMatrix),
        modifier = Modifier
            .size(80.dp)
            .clickable { onDiceClick() }
            .graphicsLayer {
                if (diceState.isSelected && diceState.isRolling) {
                    rotationX = rotation
                    rotationY = rotation
                    scaleX = scale
                    scaleY = scale
                    cameraDistance = 12f
                }
            }
    )
}
