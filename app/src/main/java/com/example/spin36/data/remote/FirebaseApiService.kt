package com.example.spin36.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface FirebaseApiService {

    @GET("top_ten.json")
    suspend fun obtenerTopTen(): Map<String, PuntuacionDto>?

    @PUT("top_ten/{uid}.json")
    suspend fun guardarPuntuacion(
        @Path("uid") uid: String,
        @Body puntuacion: PuntuacionDto
    ): PuntuacionDto
}
