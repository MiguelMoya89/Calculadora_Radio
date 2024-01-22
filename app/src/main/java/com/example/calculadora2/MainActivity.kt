package com.example.calculadora2

import android.content.BroadcastReceiver
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    // Declaración de la variable textoResultado
    private lateinit var textoResultado: TextView   // Nuevo TextView para el resultado
    private lateinit var textOperacion: TextView  // Nuevo TextView para la operación
    private var canAddOperator = false
    private var canAddDecimal = true
    private var potenciaAction = false
    private var newOperation =
        false  // Nueva variable para controlar si se ha realizado una operación
    private var lastResult = ""  // Nueva variable para almacenar el último resultado
    private var isMuted = false
    private lateinit var exoPlayer: ExoPlayer


    // Define un array con los colores que definiste en colors.xml
    val colors = arrayOf(
        R.color.color1,
        R.color.color2,
        R.color.color3,
        R.color.color4,
        R.color.color5,
        R.color.color6,
        R.color.color8,
        R.color.color9,
        R.color.color10,
        R.color.color11,
        R.color.color12,
        R.color.color13
    )
    // Define un array con los sonidos que definiste en sounds.xml
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    // Agregue su lista de URLs aquí
    private val urlList = listOf(
        "https://21273.live.streamtheworld.com/CADENADIAL_01.mp3",
        "https://25583.live.streamtheworld.com/CADENADIAL_03.mp3",
        "https://21293.live.streamtheworld.com/LOS40_DANCE.mp3",
        "https://25643.live.streamtheworld.com/LOS40_URBAN.mp3",
        "https://fluxfm.streamabc.net/flx-chillhop-mp3-320-1595440?sABC=657812so%230%232qqpnss01895rqr0s8oq129o03s183o0%23fgernzf.syhksz.qr&aw_0_1st.playerid=streams.fluxfm.de&amsparams=playerid:streams.fluxfm.de;skey:1702367995"
        // Agregue más URLs según sea necesario
    )

    private val imageList = listOf(
        R.drawable.imagen,
        R.drawable.imagen1,
        R.drawable.imagen2
        // Agregue más imágenes según sea necesario
    )

    private var currentUrlIndex = 0

    // Función llamada al crear la actividad principal de la app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización del nuevo TextView
        textoResultado = findViewById(R.id.textResultado)
        textOperacion = findViewById(R.id.textOperacion)

        // Prepara el ExoPlayer con la canción de fondo
        prepareExoPlayer(urlList[currentUrlIndex])

        // Inicia la app con el sonido en silencio
        exoPlayer.playWhenReady = false
        isMuted = true

        //llama al metodo para mutear
        val btnMutear = findViewById<Button>(R.id.btnMutear)
        btnMutear.setOnClickListener {
            mutearAction()
        }

        // Encuentra el botón Siguiente y configura su onClickListener
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)
        btnSiguiente.setOnClickListener {
            siguienteAction()
        }

        // Registra el NetworkCallback
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    prepareExoPlayer(urlList[currentUrlIndex])
                }
            }

            // Función llamada cuando se pierde la conexión a Internet
            override fun onLost(network: Network) {
                runOnUiThread {
                    exoPlayer.playWhenReady = false
                }
            }
        }
        // Registra el NetworkCallback para la conexión a Internet y la desconexión
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    //Funcion para el exoplayer de la musica de fondo de la app
    @OptIn(UnstableApi::class)
    private fun prepareExoPlayer(url: String) {
        // Detiene y libera el reproductor antes de prepararlo con la nueva URL
        if (this::exoPlayer.isInitialized) {
            exoPlayer.stop()
            exoPlayer.release()
        }

        // Crea un nuevo reproductor ExoPlayer
        exoPlayer = SimpleExoPlayer.Builder(this).build()

        // Asigna el reproductor al reproductor de la vista
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    //Funcion para mutear la musica
    private fun mutearAction() {
        if (isMuted) {
            //Si la musica esta silenciada, la reanuda
            exoPlayer.playWhenReady = true
            isMuted = false
        } else {
            //Si la musica esta reproduciendose, la pausa
            exoPlayer.playWhenReady = false
            isMuted = true
        }
    }

    // Función para manejar la acción del botón Siguiente
    private fun siguienteAction() {
        try {
            // Incrementa el índice de la URL actual
            currentUrlIndex = (currentUrlIndex + 1) % urlList.size

            // Prepara el reproductor con la nueva URL
            prepareExoPlayer(urlList[currentUrlIndex])

            // Cambia la imagen de ImageView
            val imageView = findViewById<ImageView>(R.id.imageView)
            val imageIndex = currentUrlIndex % imageList.size // Asegura que el índice de la imagen nunca exceda el tamaño de la lista
            imageView.setImageResource(imageList[imageIndex])

            // Comienza a reproducir la nueva URL
            exoPlayer.playWhenReady = true
        } catch (e: IndexOutOfBoundsException) {
            // Manejar la excepción aquí, por ejemplo, mostrando un mensaje de error al usuario
            Toast.makeText(this, "Error: La lista de URLs o imágenes está vacía.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // Manejar la excepción aquí, por ejemplo, mostrando un mensaje de error al usuario
            Toast.makeText(this, "Error: Problema al preparar el reproductor.", Toast.LENGTH_SHORT).show()
        } catch (e: NullPointerException) {
            // Manejar la excepción aquí, por ejemplo, mostrando un mensaje de error al usuario
            Toast.makeText(this, "Error: No se encontró la vista con el ID especificado.", Toast.LENGTH_SHORT).show()
        } catch (e: Resources.NotFoundException) {
            // Manejar la excepción aquí, por ejemplo, mostrando un mensaje de error al usuario
            Toast.makeText(this, "Error: No se encontró el recurso con el ID especificado.", Toast.LENGTH_SHORT).show()
        }
    }


    // Función llamada al presionar un botón de operación (+, -, x, /)
    fun operationAction(view: View) {
        try {
            // Verificar si el último carácter es un operador
            if (view is Button) {
                if (newOperation) {
                    textOperacion.text = lastResult
                    newOperation = false
                }
                // Verificar si se puede agregar un operador
                if (canAddOperator && textOperacion.text.isNotEmpty()) {
                    val lastChar = textOperacion.text.last()
                    // Verificar si el último carácter es un operador
                    if (lastChar == '/' || lastChar == 'x') {
                        // Mostrar un mensaje de error
                        Toast.makeText(
                            this,
                            "No se puede introducir más de una división o multiplicación seguidas",
                            Toast.LENGTH_SHORT
                        ).show()
                    // Verificar si el último carácter es un operador
                    } else {
                        // Calcular el resultado de la operación actual
                        val currentResult = calcularResultado()
                        // Limpiar el texto de la operación
                        textOperacion.text = ""
                        // Añadir el resultado de la operación actual
                        textOperacion.append(currentResult)
                        // Muestra el resultado en el textoResultado
                        textoResultado.text = currentResult
                        // Añadir el nuevo operador
                        textOperacion.append(view.text)
                        canAddOperator = false
                        canAddDecimal = true
                    }
                }
            }
        } catch (e: Exception) {
            // Manejar la excepción aquí, por ejemplo, mostrando un mensaje de error al usuario
            Toast.makeText(this, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    // Función para calcular una operación de división o multiplicación
    private fun calcDivOrTimes(passedList: MutableList<Any>, operation: Char): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var i = 0
        // Recorrer la lista de dígitos y operaciones y calcular la operación
        while (i < passedList.size) {
            // Verificar si el elemento actual es un operador
            if (passedList[i] is Char && i != passedList.lastIndex && passedList[i] == operation) {
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                val result = when (operation) {
                    '/' -> if (nextDigit == 0f) throw ArithmeticException("Error: División por cero") else prevDigit / nextDigit
                    'x' -> prevDigit * nextDigit
                    else -> throw IllegalArgumentException("Operación no válida: $operation")
                }
                newList[newList.lastIndex] = result
                i += 2
            } else {
                newList.add(passedList[i])
                i++
            }
        }
        return newList
    }


    // Función llamada al presionar el botón de igual (=)
    fun equalAction(view: View) {
        try {
            // Verificar si el último carácter es un operador
            if (textOperacion.text.isNotEmpty() && textOperacion.text.last() in listOf(
                    '+',
                    '-',
                    'x',
                    '/'
                )
            ) {
                Toast.makeText(this, "Error: Operación incompleta", Toast.LENGTH_SHORT).show()
                return
            }
            // Calcular la potencia si es necesario
            if (potenciaAction) {
                calculatePower()
            }
            val result = calcularResultado()
            textoResultado.text = result
            lastResult = result
            newOperation = true
        } catch (e: ArithmeticException) {
            textoResultado.text = "Error: División no válida"
        } catch (e: Exception) {
            textoResultado.text = "Error: Entrada inválida"
        }
    }




    // Función para calcular el resultado de la operación
    private fun calcularResultado(): String {
        val digitsOperations = digitsOperations()
        if (digitsOperations.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperations)
        if (timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)

        // Comprobar si se multiplicó por cero
        if (textOperacion.text.contains("*0")) {
            return "0"
        }

        // Comprobar si el último carácter es un operador
        val lastChar = textOperacion.text.last()
        if (lastChar == '/' || lastChar == 'x') {
            // Mostrar un mensaje de error
            Toast.makeText(
                this,
                "No se puede calcular el resultado sin completar la operación",
                Toast.LENGTH_SHORT
            ).show()
            return ""
        }

        return if (result % 1 == 0f) {
            result.toInt()
                .toString()  // Si el resultado es un número entero, convertir a Int antes de convertir a String
        } else {
            result.toString()  // Si el resultado es un número decimal, convertir directamente a String
        }
    }

    // Función para calcular las operaciones de suma y resta
    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float
        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i] as Char
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+') {
                    result += nextDigit
                }
                if (operator == '-') {
                    result -= nextDigit
                }
            }
        }
        return result
    }

    // Función para calcular las operaciones de multiplicación y división
    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('/')) {
            list = calcDivOrTimes(list, '/')
        }
        while (list.contains('x')) {
            list = calcDivOrTimes(list, 'x')
        }
        return list
    }


    // Función para obtener la lista de dígitos y operaciones
    private fun digitsOperations(): MutableList<Any> {
        var list = mutableListOf<Any>()
        var currentDigit = ""
        var index = 0
        while (index < textOperacion.text.length) {
            val character = textOperacion.text[index]
            if (character.isDigit() || character == '.') {
                currentDigit += character
                index++
            } else if (character == '^') {
                val base = currentDigit.toFloat()
                currentDigit = ""
                index++
                while (index < textOperacion.text.length && (textOperacion.text[index].isDigit() || textOperacion.text[index] == '.')) {
                    currentDigit += textOperacion.text[index]
                    index++
                }
                val exponent = currentDigit.toFloat()
                list.add(base.pow(exponent))
                currentDigit = ""
            } else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
                index++
            }
        }
        if (currentDigit != "") {
            list.add(currentDigit.toFloat())
        }
        return list
    }

    // Función llamada al presionar un botón de número
// Función llamada al presionar un botón de número
    fun numberAction(view: View) {
        if (view is Button) {
            if (newOperation) {
                textOperacion.text = ""
                newOperation = false
            }
            if (potenciaAction) {
                textOperacion.append(view.text)
                calculatePower() // Calcular la potencia si es necesario
                textOperacion.text = textoResultado.text // Mostrar el resultado de la operación de potencia
                canAddOperator = true
                potenciaAction = false
            } else if (canAddOperator) {
                textOperacion.append(view.text)
                canAddDecimal = false
            } else {
                textOperacion.append(view.text)
                canAddOperator = true
            }
        }
    }

    // Función para la acción de raíz
    fun raizAction(view: View) {
        if (textOperacion.text.isEmpty() && lastResult.isNotEmpty()) {
            textOperacion.text = lastResult
        }
        if (textOperacion.text.contains(Regex("[+\\-x/]"))) {
            equalAction(view)
            textOperacion.text = textoResultado.text
        }
        if (textOperacion.text.contains("^")) {
            potenciaAction(view)
            textOperacion.text = textoResultado.text
        }
        if (textOperacion.text.isEmpty()) {
            textoResultado.text = "Error: INSERT VALUE"
        } else {
            try {
                var number = textOperacion.text.toString().toFloat()
                if (number < 0) {
                    textoResultado.text = "Error: NEGATIVE VALUE"
                } else {
                    var previousResult: Float? = null
                    var result: Float = sqrt(number.toDouble()).toFloat()
                    while (abs(previousResult?.minus(result) ?: 0.0f) > 0.000001f) {
                        previousResult = result
                        result = sqrt(result.toDouble()).toFloat()
                    }
                    if (result.isNaN() || result.isInfinite()) {
                        textoResultado.text = "Error: INVALID OPERATION"
                    } else {
                        textoResultado.text = result.toString()
                        lastResult = result.toString() // Actualizar lastResult con el resultado de la operación
                        textOperacion.text = "" // Limpiar textOperacion después de la operación
                    }
                }
            } catch (e: Exception) {
                textoResultado.text = "Error: ${e.message}"
            }
        }
    }

    // Función para la acción de potencia
    fun potenciaAction(view: View) {
        if (textOperacion.text.isEmpty() && lastResult.isNotEmpty()) {
            textOperacion.text = lastResult
        }
        if (canAddOperator && textOperacion.text.isNotEmpty()) {
            try {
                // Verificar si hay una operación pendiente
                if (textOperacion.text.contains(Regex("[+\\-x/]"))) {
                    // Calcular la operación pendiente
                    equalAction(view)
                    // Usar el resultado de la operación pendiente para la operación de potencia
                    textOperacion.text = textoResultado.text
                }
                potenciaAction = true
                textOperacion.append("^")
                canAddOperator = false
                canAddDecimal = true
            } catch (e: Exception) {
                textoResultado.text = "Error: ${e.message}"
            }
        }
    }

    // Función para calcular la operación de potencia
    fun calculatePower() {
        val operationText = textOperacion.text.toString()
        val powerIndex = operationText.indexOf("^")

        if (powerIndex != -1 && powerIndex < operationText.length - 1) {
            val baseText = operationText.substring(0, powerIndex)
            val exponent = operationText.substring(powerIndex + 1).toFloat()

            val operatorIndex = baseText.indexOfAny(charArrayOf('+', '-', 'x', '/'))
            val base = if (operatorIndex != -1) {
                val operator = baseText[operatorIndex]
                val firstNumber = baseText.substring(0, operatorIndex).toFloat()
                val secondNumber = baseText.substring(operatorIndex + 1).toFloat()
                when (operator) {
                    '+' -> firstNumber + secondNumber
                    '-' -> firstNumber - secondNumber
                    'x' -> firstNumber * secondNumber
                    '/' -> firstNumber / secondNumber
                    else -> throw IllegalArgumentException("Operador no válido: $operator")
                }
            } else {
                baseText.toFloat()
            }

            val result = base.pow(exponent)

            // Comprobar si el resultado es un número entero
            if (result % 1 == 0f) {
                // Si el resultado es un número entero, convertir a Int antes de convertir a String
                textoResultado.text = result.toInt().toString()
                lastResult = result.toInt().toString()
            } else {
                // Si el resultado es un número decimal, convertir directamente a String
                textoResultado.text = result.toString()
                lastResult = result.toString()
            }

            potenciaAction = false
        } else {
            textoResultado.text = "Error: Formato de potencia incorrecto"
        }
    }

    // Función para la acción de borrar todo
    fun allClearAction(view: View) {
        // Restablecer todos los campos y variables a sus valores iniciales
        textOperacion.text = ""
        textoResultado.text = ""
        canAddOperator = false
        canAddDecimal = true
        newOperation = false
        potenciaAction = false
    }

    // Función para la acción de retroceso
    fun backSpaceAction(view: View) {
        // Si hay texto en textOperacion, eliminar el último carácter
        if (textOperacion.text.isNotEmpty()) {
            textOperacion.text = textOperacion.text.dropLast(1)
        }
    }

    // En la función numberActionWithSound, agrega el siguiente código:
    fun numberActionWithSound(view: View) {
        numberAction(view)
        contadorRandom2()

        // Genera un número aleatorio entre 0 y la longitud del array de colores
        val randomColorIndex = Random.nextInt(colors.size)

        // Obtiene el color correspondiente al índice generado
        val randomColor = ContextCompat.getColor(this, colors[randomColorIndex])

        // Cambia el color de fondo del botón
        view.setBackgroundColor(randomColor)
    }

    //Funcion para la accion de random de sonidos
    fun contadorRandom2() {
        val sounds = arrayOf(
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/game_over.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/command_ac_ts/nod_low_power.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/command_ac_ts/nod_transmission_in.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/better.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/back_2_work_y.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/bitchin.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/blow_it_x.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/clean-up.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/cry.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/hail.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/let_god.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/play.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/terminated.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/wasted.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/you_and_me.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/you_will_die.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/wants_some.wav",
            "https://www.wavsource.com/snds_2020-10-01_3728627494378403/video_games/duke/independence.wav"
        )

        val sound = sounds[(sounds.indices).random()]

        if (isNetworkConnected()) {
            Thread {
                try {
                    val player = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        setDataSource(sound)
                        prepare()
                        start()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    //Funcion para mejorar la fluidez de la app con los sonidos
    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }
}

