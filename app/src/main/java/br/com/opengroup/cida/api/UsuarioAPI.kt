package br.com.opengroup.cida.api

import br.com.opengroup.cida.model.Login
import br.com.opengroup.cida.model.UsuarioRequest
import br.com.opengroup.cida.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface UsuarioAPI {
   @POST("/usuario")
   suspend fun cadastrarUsuario(@retrofit2.http.Body usuarioRequest: UsuarioRequest) : Response<UsuarioResponse>

   @GET("/usuario/email/{email}")
   suspend fun consultarUsuario(@retrofit2.http.Path(value="email") email: String) : Response<UsuarioResponse>

   @POST("/login") // Returns empty body
   suspend fun autenticarUsuario(@retrofit2.http.Body login: Login) : Response<Void>
}