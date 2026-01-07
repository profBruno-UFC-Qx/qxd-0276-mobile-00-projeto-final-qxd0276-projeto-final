package com.pegai.app.model

data class Product(
    val pid: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val preco: Double = 0.0,
    val categoria: String = "",

    // Mantemos imageUrl como a "Capa" para a Home não quebrar
    val imageUrl: String = "",

    // NOVA LISTA: Para o carrossel de detalhes
    val imagens: List<String> = emptyList(),

    // ID do dono (fundamental para abrir o perfil dele)
    val donoId: String = "",
    val donoNome: String = "", // Guardamos o nome aqui para facilitar (desnormalização)

    val nota: Double = 5.0,

    // Opcional: Lista de reviews simples embutida
    // (Para um app maior, isso seria uma sub-coleção no Firebase)
    val totalAvaliacoes: Int = 0
)