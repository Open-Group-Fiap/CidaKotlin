package br.com.opengroup.cida.api

import br.com.opengroup.cida.model.InsightPage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface InsightApi {
    @GET("insight/email/{email}")
    suspend fun getInsight(@Path(value = "email") fullUrl: String): Response<InsightPage>
}