package br.com.opengroup.cida.model

import java.util.Date

data class Usuario(
    val email: String, val senha: String, val nome: String, val tipoDocumento: Int = 1, val dataCriacao: Date, val numDocumento: String, val telefone: String, val status: Int = 0
)