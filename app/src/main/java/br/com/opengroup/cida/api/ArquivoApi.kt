package br.com.opengroup.cida.api

import br.com.opengroup.cida.model.Upload
import retrofit2.http.POST
import retrofit2.http.Path

interface ArquivoApi {
    @POST("/arquivo/idUsuario/{id}/upload")
    suspend fun uploadArquivo(@Path(value="id") url: String, @retrofit2.http.Body upload: Upload)
}