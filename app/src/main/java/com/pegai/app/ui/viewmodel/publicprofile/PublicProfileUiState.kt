package com.pegai.app.ui.viewmodel.publicprofile

import com.pegai.app.model.Product
import com.pegai.app.model.User

data class PublicProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val produtos: List<Product> = emptyList(),
    val reviews: List<ReviewMock> = emptyList(),
    val produtosSugeridos: List<ProdutoMock> = emptyList(),
    val nota: Number = 0.0,
    val totalAvaliacao: Number = 0.0,

    val erro: String? = null
)

// Classes movidas da tela para cá
data class ReviewMock(val nome: String, val comentario: String, val nota: Int, val data: String)
data class ProdutoMock(val nome: String, val preco: String, val imagem: String)