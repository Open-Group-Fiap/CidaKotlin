package br.com.opengroup.cida.api

import br.com.opengroup.cida.model.Login
import br.com.opengroup.cida.model.Usuario
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface UsuarioAPI {
   @POST("usuario")
   suspend fun cadastrarUsuario(@retrofit2.http.Body usuario: Usuario) : Response<Usuario>

   @POST("/login") // Returns empty body
   suspend fun autenticarUsuario(@retrofit2.http.Body login: Login) : Response<Void>
}