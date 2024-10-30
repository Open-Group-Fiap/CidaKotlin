package br.com.opengroup.cida.model

data class UsuarioResponse (
    val idUsuario: Int, val email: String, val senha: String, val nome: String, val tipoDocumento: Int = 1, val dataCriacao: String, val numDocumento: String, val telefone: String, val status: Int = 0

)