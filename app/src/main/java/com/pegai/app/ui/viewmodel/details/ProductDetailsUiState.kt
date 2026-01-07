package com.pegai.app.ui.viewmodel.details

import com.pegai.app.model.Product

data class ProductDetailsUiState(
    val isLoading: Boolean = true,
    val produto: Product? = null,

    // --- CAMPOS NOVOS NECESSÁRIOS PARA A TELA ---
    val imagensCarrossel: List<String> = emptyList(),
    val nomeDono: String = "",
    val fotoDono: String = "",
    val avaliacoesCount: Int = 0,

    val reviews: List<ReviewUI> = emptyList(),
    val isDonoDoAnuncio: Boolean = false,
    val erro: String? = null
)

// Classe auxiliar para os comentários
data class ReviewUI(
    val authorName: String,
    val comment: String,
    val rating: Int,
    val date: String
)