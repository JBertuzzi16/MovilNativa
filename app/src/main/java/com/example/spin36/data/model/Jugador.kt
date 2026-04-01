package com.example.spin36.data.model

class Jugador (
        var id: Int,
        var nombre: String,
        saldoInicial : Int = 50,
        rachaInicial: Int = 0
)
{
      var saldoActual : Int = saldoInicial
              private set
        fun tieneSaldoSuficiente(monto: Int): Boolean{
                return saldoActual>=monto

        }

        fun actualizarSaldo(monto:Int){
                saldoActual +=monto
        }

        // Función racha de victorias
        var rachaDeVictorias:Int = rachaInicial
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
    fun reiniciarSesion (saldoInicial:Int = 50){
        saldoActual=saldoInicial
        rachaDeVictorias=0
    }
}




