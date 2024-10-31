package br.com.opengroup.cida.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    companion object {
        val retrofit = Retrofit.Builder()
            //.baseUrl("https://cida-api.azurewebsites.net/")
            .baseUrl("https://cad8-2804-14c-a0-8558-754e-3f36-878b-ebbe.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}