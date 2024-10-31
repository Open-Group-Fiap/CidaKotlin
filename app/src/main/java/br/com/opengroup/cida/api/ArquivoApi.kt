package br.com.opengroup.cida.api

import br.com.opengroup.cida.model.InsightPage
import br.com.opengroup.cida.model.Upload
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ArquivoApi {
    @Multipart
    @POST("/arquivo/idUsuario/{id}/upload")
    suspend fun uploadArquivo(@Path(value="id") url: String,
                              @Part arquivosRequest: List<MultipartBody.Part>): Response<InsightPage>
}