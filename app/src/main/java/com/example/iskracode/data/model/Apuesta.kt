package com.example.iskracode.data.model

sealed class Apuesta(protected val cantidad: Int) {
     public abstract val multiplicador: Int
     public abstract fun esGanadora(resultado: Int): Boolean

    fun calcularPremio(resultado: Int): Int {
        return if (esGanadora(resultado)) cantidad * multiplicador else 0
    }

    class Pleno(
        private val numero: Int,
        cantidad: Int
    ) : Apuesta(cantidad) {
        override val multiplicador: Int = 36

        override fun esGanadora(resultado: Int): Boolean {
            return resultado == numero
        }
    }

    class Color(
        private val color: String,
        cantidad: Int
    ) : Apuesta(cantidad) {
        override val multiplicador: Int = 2

        override fun esGanadora(resultado: Int): Boolean {
            return when (color.lowercase()) {
                "rojo" -> resultado in Sesion.colorRojo
                "negro" -> resultado in Sesion.colorNegro
                else -> false
            }
        }
    }

    class Docena(
        private val docena: Int,
        cantidad: Int
    ) : Apuesta(cantidad) {
        override val multiplicador: Int = 3

        override fun esGanadora(resultado: Int): Boolean {
            return when (docena) {
                1 -> resultado in 1..12
                2 -> resultado in 13..24
                3 -> resultado in 25..36
                else -> false
            }
        }
    }

    class ParImpar(
        private val tipo: String,
        cantidad: Int
    ) : Apuesta(cantidad) {
        override val multiplicador: Int = 2

        override fun esGanadora(resultado: Int): Boolean {
            if (resultado == 0) return false

            return when (tipo.lowercase()) {
                "par" -> resultado % 2 == 0
                "impar" -> resultado % 2 != 0
                else -> false
            }
        }
    }
}
