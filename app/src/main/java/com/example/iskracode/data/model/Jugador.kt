package com.example.iskracode.data.model

class Jugador (
        var id: Int,
        var nombre: String,
        val saldoInicial: Double = 50.0,// 50 fichas inicial despues se puede cambiar
)
{
      var saldoActual: Double=50.00
              private set
        fun tieneSaldoSuficiente(saldoInicial: Double): Boolean{
                return saldoActual>=saldoInicial

        }

        fun actualizarSaldo(saldoInicial: Double){
                saldoActual +=saldoInicial
        }

        // Función racha de victorias
        var rachaDeVictorias:Int = 0
                private set
        fun gestionRacha (haGanado:Boolean){
                if (haGanado){
                        rachaDeVictorias++
                        if (rachaDeVictorias==5){
                                println("¡Racha de 5 Victorias conseguida! + 100.0")
                                actualizarSaldo(100.0)
                                rachaDeVictorias=0
                        }else {
                                rachaDeVictorias=0
                        }
                }

        }
}



