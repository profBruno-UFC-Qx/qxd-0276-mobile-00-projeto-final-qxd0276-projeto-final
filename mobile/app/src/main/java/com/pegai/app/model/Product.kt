package com.pegai.app.model

/**
 * Representa um item ou material acadêmico disponível para aluguel no Pegaí.
 */
data class Product(
    val id: String,
    val titulo: String,
    val descricao: String,
    val preco: Double,

    // ID ou referência ao usuário proprietário do item
    val dono: String,

    /**
     * Avaliação média do item (escala de 0.0 a 5.0).
     * O valor padrão é 5.0 para novos itens.
     */
    val nota: Double = 5.0,
    val imageUrl: String
)