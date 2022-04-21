package com.example.obtenerlos5kerklys

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.obtenerlos5kerklys.clases.CalcularTiempoDistancia
import com.example.obtenerlos5kerklys.clases.ModeloKerklys
import com.example.obtenerlos5kerklys.clases.claseUrl
import com.example.obtenerlos5kerklys.interfacee.ObtenerLosKerklysOficio
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, CalcularTiempoDistancia.Geo {
    private lateinit var miSpinner: Spinner
    lateinit var context: Context
     var posicion = ""
    var u = claseUrl()
    var url = u.ROOT_URL
    var poslist: ArrayList<ModeloKerklys>? =null
    var latitudInicial: Double?= 0.0
    var longitudInicial: Double?= 0.0
    var latitudFinal: Double?= 0.0
    var longitudFinal: Double?= 0.0
    var i2: Int? =0

    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?;
        context = this
        locationStart()
        miSpinner = findViewById(R.id.Mi_spinner)

        val lista = ArrayList<String>()
        lista.add("Electricista");
        lista.add("Plomero");
        lista.add("Herrero");
        lista.add("Albañil");
        lista.add("Carpintero");
        lista.add("Niñera");
        lista.add("Trabajo");


        miSpinner.onItemSelectedListener
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        miSpinner.setAdapter(adapter)
        miSpinner.onItemSelectedListener = this

        buttonLos5MasCer.setOnClickListener{
           //Toast.makeText(this@MainActivity, "seleccionado $posicion", Toast.LENGTH_LONG).show()
            ObtenerCoordenas(posicion)
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
      //  posicion = p0!!.getItemIdAtPosition(p2).toInt()
         posicion = p0!!.selectedItem.toString()
       // Toast.makeText(this@MainActivity, "seleccionado $posicion", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun ObtenerCoordenas (oficio: String){
            val retrofit = Retrofit.Builder()
                .baseUrl(url+"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val get = retrofit.create(ObtenerLosKerklysOficio::class.java)
            val call = get.ObtenerC(oficio)

            call?.enqueue(object : Callback<List<ModeloKerklys?>?> {

                override fun onResponse(call: Call<List<ModeloKerklys?>?>, response: Response<List<ModeloKerklys?>?>) {

                    poslist = response.body() as ArrayList<ModeloKerklys>
                    Log.e("tamaño ", "${poslist!!.size}")
                    if (poslist!!.size >= 1){
                        for(i in 0 until poslist!!.size){
                            latitudFinal = poslist!!.get(i).getLatitud()
                            longitudFinal = poslist!!.get(i).getLongitud()
                            val url2 = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=$latitudInicial,$longitudInicial&destinations=$latitudFinal,$longitudFinal&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyAp-2jznuGLfRJ_en09y1sp6A-467zrXm0";
                            CalcularTiempoDistancia(context).execute(url2)


                        }
                    }else{
                        Toast.makeText(this@MainActivity, "No hay Coordenadas finales", Toast.LENGTH_LONG).show()
                        }

                   /* txtlatitud.setText("$latitudInicial")
                    txtLongitud.setText("$longitudInicial")

                    System.out.println("latitud: ${latitudInicial}")
                    System.out.println("longitud: ${longitudInicial}")*/
                }

                override fun onFailure(call: Call<List<ModeloKerklys?>?>, t: Throwable) {
                    //Toast.makeText(this, t.toString(), Toast.LENGTH_LONG).show()
                    System.out.println("el error es: ${t.toString()}")

                }

            })

        }

    @SuppressLint("MissingPermission")
    private fun locationStart() {
        val Local = Localizacion()
        Local.mainActivity= this
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        //En caso de que el Gps este desactivado, entrara en el if y nos mandara a la configuracion de nuestro Gps para activarlo
        //En caso de que el Gps este desactivado, entrara en el if y nos mandara a la configuracion de nuestro Gps para activarlo

        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, (Local as LocationListener)!!)
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, (Local as LocationListener)!!)
        // println("si entro aqui")


        btnRecorrerlista.setOnClickListener{
            recorrerLista()
        }
    }

    class Localizacion : LocationListener {
        var mainActivity: MainActivity? = null


        @SuppressLint("MissingPermission")
        override fun onLocationChanged(loc: Location) {
            // if(loc != null){
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.latitude
            loc.longitude
            val sLatitud = java.lang.String.valueOf(loc.latitude)
            val sLongitud = java.lang.String.valueOf(loc.longitude)
            mainActivity?.latitudInicial = loc.latitude
            mainActivity?.longitudInicial = loc.longitude
            //mainActivity?.txtlatitud?.setText(sLatitud)
          //  mainActivity?.txtLongitud?.setText(sLongitud)
            //  mainActivity?.setLocation(loc)
            mainActivity?.locationManager?.removeUpdates(this)

            //  }

            //mainActivityConsultaSinRegistro?.locationManager!!.removeUpdates((mainActivityConsultaSinRegistro?.locationManager as LocationListener?)!!)
        }

        override fun onProviderDisabled(provider: String) {
            //mainActivityConsultaSinRegistro?.txt.setText("GPS Desactivado")
            Toast.makeText(mainActivity, "GPS Desactivado", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {

            Toast.makeText(mainActivity, "GPS activado", Toast.LENGTH_SHORT).show()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            when (status) {
                LocationProvider.AVAILABLE -> Log.d("debug", "LocationProvider.AVAILABLE")
                LocationProvider.OUT_OF_SERVICE -> Log.d("debug", "LocationProvider.OUT_OF_SERVICE")
                LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE")
            }
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    override fun setDouble(min: String?) {
        val res = min!!.split(",").toTypedArray()
        val min = res[0].toDouble() / 60
        val dist = res[1].toInt() / 1000

      //  textView_result1.setText("Duracion= " + (min / 60).toInt() + " hr " + (min % 60).toInt() + " mins")
     //   textView_result2.setText("Distancia= $dist kilometros")
        i2 = i2!! +1
     /*   System.out.println("punto " + i2)
        System.out.println("Duracion= " + (min / 60).toInt() + " hr " + (min % 60).toInt() + " mins ")
        System.out.println("Distancia= $dist kilometros") */
        val e = i2!!-1


            System.out.println((min /60).toInt())
            poslist!![e!!].Hora = (min / 60).toInt()
            System.out.println((min %60).toInt())
            poslist!![e!!].minutos = (min % 60).toInt()


     //   val curp = poslist!!.get(e).getCurp()
     //   lista.add("$curp " + i2  +" tiempo"+ (min / 60).toInt() +  " hr " + (min % 60).toInt() + " mins")

       /* for(i in 0 until poslist!!.size){
            System.out.println(poslist!!.get(i).getCurp())
            System.out.println(poslist!!.get(i).Hora)
            System.out.println(poslist!!.get(i).minutos)
        }*/
   }


    fun recorrerLista (){
        for(i in 0 until poslist!!.size){
            System.out.println(poslist!!.get(i).getCurp())
            System.out.println("hora " + poslist!!.get(i).Hora + ":" + poslist!!.get(i).minutos)

        }



    }

}


