package br.com.opengroup.cida.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    companion object {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://cida-api.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}