package com.example.iskracode

import com.example.iskracode.data.model.Apuesta
import com.example.iskracode.data.model.Sesion
import com.example.iskracode.data.model.Jugador

fun main() {

    val jugador = Jugador(1,"Joaquin")
    println("=== RULETA ===")
    println ("Jugador: ${jugador.nombre.uppercase()}")
    println("Dinero inicial: ${jugador.saldoInicial}")

    var jugando = true

    while (jugando && jugador.saldoActual > 0) {
        println("=== NUEVA TIRADA ===")
        println("Tu saldo actual es: ${jugador.saldoActual}")
        print("Introduce la cantidad a apostar (o escribe 0 para salir): ")

        // Usamos toIntOrNull por si el usuario escribe texto por error que no explote
        val cantidad= readln().toIntOrNull() ?: 0

        if (cantidad == 0) {
            jugando = false
            continue
        }
        if (!jugador.tieneSaldoSuficiente(cantidad .toDouble())) {
            println("¡No tienes saldo suficiente para esa apuesta!")
            continue // Vuelve al inicio del bucle
        }
        jugador.actualizarSaldo(-cantidad.toDouble())
        println("Elige tipo de apuesta:")
        println("1. Pleno")
        println("2. Color")
        println("3. Par/Impar")
        println("4. Docena")
        print("Opción: ")
        val opcion = readln().toInt()

        val apuesta: Apuesta = when (opcion) {
            1 -> {
                print("Introduce el número (0-36): ")
                val numero = readln().toInt()
                Apuesta.Pleno(numero, cantidad )
            }

            2 -> {
                print("Introduce color (rojo/negro): ")
                val color = readln().lowercase()

                if (color != "rojo" && color != "negro") {
                    println("Color no válido")
                    continue
                }

                Apuesta.Color(color, cantidad)
            }

            3 -> {
                print("Introduce tipo (par/impar): ")
                val tipo = readln().lowercase()

                if (tipo != "par" && tipo != "impar") {
                    println("Tipo no válido")
                    continue
                }

                Apuesta.ParImpar(tipo, cantidad)
            }

            4 -> {
                print("Introduce la docena (1, 2 o 3): ")
                val docena = readln().toInt()

                if (docena !in 1..3) {
                    println("Docena no válida")
                   continue
                }

                Apuesta.Docena(docena, cantidad)
            }

            else -> {
                println("Opción no válida")
                continue
            }
        }

        val resultado = Sesion.girar()

        println("Ha salido el número: $resultado")

        val premio = apuesta.calcularPremio(resultado)

        if (premio > 0) {
            println("¡Has ganado!")
            println("Premio: $premio")
            jugador.actualizarSaldo(+premio .toDouble())
            jugador.gestionRacha(haGanado = true)
        } else {
            println("No hay premio.")
            jugador.actualizarSaldo(-cantidad .toDouble())
            jugador.gestionRacha(haGanado=false)
        }
        println("Racha de victorias actual: ${jugador.rachaDeVictorias} / 5")
        println("--------------------------------------------------")
    }
}