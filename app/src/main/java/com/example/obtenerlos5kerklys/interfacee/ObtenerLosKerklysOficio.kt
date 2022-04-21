package com.example.obtenerlos5kerklys.interfacee

import com.example.obtenerlos5kerklys.clases.ModeloKerklys
import retrofit.http.FormUrlEncoded
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerLosKerklysOficio {
        @FormUrlEncoded
        @GET("obtenerCoordenadasParaSaberElKerklyMasCercano.php")
        open fun ObtenerC(@Query("oficio") oficio: String):
                Call<List<ModeloKerklys?>?>?

}