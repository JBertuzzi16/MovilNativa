package com.example.iskracode.data.model

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
        fun gestionRacha (haGanado:Boolean){
                if (haGanado){
                        rachaDeVictorias++
                        if (rachaDeVictorias==5){
                                println("¡Racha de 5 Victorias conseguida! + 100.0")
                                actualizarSaldo(100)
                            rachaDeVictorias=0

                        }
                }else {
                    rachaDeVictorias=0
                }

        }
}



