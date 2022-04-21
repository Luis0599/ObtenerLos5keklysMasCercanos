package com.example.obtenerlos5kerklys.clases

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ModeloKerklys {
    @SerializedName("Curp")
    @Expose
    private var Curp: String = ""
    @SerializedName("latitud")
    @Expose
    private var latitud =0.0
    @SerializedName("longitud")
    @Expose
    private var longitud =0.0

    var Hora =0

    var minutos = 0

    fun getCurp(): String { return Curp}
    fun getLatitud(): Double { return latitud}
    fun getLongitud(): Double { return longitud}
}