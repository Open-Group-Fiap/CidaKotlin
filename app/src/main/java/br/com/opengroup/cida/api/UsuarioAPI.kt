package br.com.opengroup.cida.api

import br.com.opengroup.cida.model.Login
import br.com.opengroup.cida.model.UsuarioRequest
import br.com.opengroup.cida.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UsuarioAPI {
    @POST("/usuario")
    suspend fun cadastrarUsuario(@retrofit2.http.Body usuarioRequest: UsuarioRequest): Response<UsuarioResponse>

    @GET("/usuario/email/{email}")
    suspend fun consultarUsuario(@retrofit2.http.Path(value = "email") email: String): Response<UsuarioResponse>

    @PUT("/usuario/{id}")
    suspend fun atualizarUsuario(
        @retrofit2.http.Path(value = "id") id: String,
        @retrofit2.http.Body usuarioRequest: UsuarioRequest
    ): Response<UsuarioResponse>

    @DELETE("/usuario/{id}")
    suspend fun deletarUsuario(@retrofit2.http.Path(value = "id") id: String): Response<Void>

    @POST("/login") // Returns empty body
    suspend fun autenticarUsuario(@retrofit2.http.Body login: Login): Response<Void>
}