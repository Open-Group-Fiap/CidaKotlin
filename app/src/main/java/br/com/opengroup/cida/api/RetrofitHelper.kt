package br.com.opengroup.cida.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitHelper {
    companion object {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(300, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://cida-api.azurewebsites.net/")
            //.baseUrl("https://cad8-2804-14c-a0-8558-754e-3f36-878b-ebbe.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}