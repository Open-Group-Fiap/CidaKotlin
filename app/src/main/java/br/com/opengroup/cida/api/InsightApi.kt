package br.com.opengroup.cida.api

import retrofit2.http.GET
import retrofit2.http.Path

interface InsightApi {
    @GET("insight/email/{email}")
    suspend fun getInsight(@Path(value = "email") fullUrl: String)
}