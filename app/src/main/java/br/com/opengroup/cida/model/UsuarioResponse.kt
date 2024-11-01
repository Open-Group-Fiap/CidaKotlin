package br.com.opengroup.cida.model



data class UsuarioResponse(
    val idUsuario: Int,
    val idAutenticacao: Int,
    val nome: String,
    val tipoDocumento: Int,
    val numDocumento: String,
    val telefone: String,
    val dataCriacao: String,
    val status: Int
)