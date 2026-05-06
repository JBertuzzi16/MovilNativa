package com.example.spin36.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PuntuacionDto(
    @Json(name = "nombre")    val nombre: String    = "",
    @Json(name = "puntuacion") val puntuacion: Int  = 0,
    @Json(name = "fecha")     val fecha: String     = "",
    @Json(name = "uid")       val uid: String       = ""
)
