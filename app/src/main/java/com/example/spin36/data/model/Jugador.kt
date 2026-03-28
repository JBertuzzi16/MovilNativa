package com.example.spin36.data.model

class Jugador (
        var id: Int,
        var nombre: String
)
{
      var saldoActual = 50
              private set
        fun tieneSaldoSuficiente(monto: Int): Boolean{
                return saldoActual>=monto

        }

        fun actualizarSaldo(monto:Int){
                saldoActual +=monto
        }

        // Función racha de victorias
        var rachaDeVictorias:Int = 0
                private set
    fun gestionRacha(haGanado: Boolean): Boolean {
        if (haGanado) {
            rachaDeVictorias++
            if (rachaDeVictorias == 5) {
                actualizarSaldo(100)
                rachaDeVictorias = 0
                return true
            }
        } else {
            rachaDeVictorias = 0
        }
        return false
    }

}




