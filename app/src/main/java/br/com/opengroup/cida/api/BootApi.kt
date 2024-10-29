package br.com.opengroup.cida.api

import retrofit2.Response
import retrofit2.http.GET

interface BootApi {
    @GET("/boot")
    suspend fun boot(): Response<Void>
}