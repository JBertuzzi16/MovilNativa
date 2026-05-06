package com.example.spin36.data.remote

const val PREMIO_TOP_TEN = 500
const val TAMANIO_TOP_TEN = 10

class TopTenRepository {

    suspend fun obtenerTopTen(): List<PuntuacionDto> {
        return try {
            RetrofitClient.api.obtenerTopTen()
                ?.values
                ?.sortedByDescending { it.puntuacion }
                ?.take(TAMANIO_TOP_TEN)
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun intentarEntrarEnTopTen(puntuacion: PuntuacionDto): Boolean {
        return try {
            val topActual = obtenerTopTen()
            val estaEnTop = topActual.any { it.uid == puntuacion.uid }
            val menosDeDiez = topActual.size < TAMANIO_TOP_TEN
            val superaMinimo = topActual.size >= TAMANIO_TOP_TEN &&
                    puntuacion.puntuacion > (topActual.minByOrNull { it.puntuacion }?.puntuacion ?: 0)

            if (estaEnTop || menosDeDiez || superaMinimo) {
                RetrofitClient.api.guardarPuntuacion(puntuacion.uid, puntuacion)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
