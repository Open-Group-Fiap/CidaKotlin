package br.com.opengroup.cida.model

data class InsightPage(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val insights: Array<Insight>,
)

