package br.com.opengroup.cida.model

data class Insight (
    val idInsight: Int,
    val idUsuario: Int,
    val idResumo: Int,
    val dataGeracao: String,
    val descricao: String
)