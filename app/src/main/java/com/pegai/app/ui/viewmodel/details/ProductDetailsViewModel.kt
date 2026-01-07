package com.pegai.app.ui.viewmodel.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.data.data.utils.formatarTempo
import com.pegai.app.model.Avaliacao
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class ProductDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    suspend fun carregarAvaliacoesDoProduto(produtoId: String): List<Avaliacao> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("avaliacao")
            .whereEqualTo("produtoId", produtoId)
            .get()
            .await()

        return snapshot.toObjects(Avaliacao::class.java)
    }



    suspend fun carregarReviewsUI(produtoId: String): List<ReviewUI> {
        val avaliacoes = carregarAvaliacoesDoProduto(produtoId)

        return avaliacoes.map { avaliacao ->
            ReviewUI(
                authorName = UserRepository.getNomeUsuario(avaliacao.usuarioId),
                comment = avaliacao.descricao,
                rating = avaliacao.nota,
                date = formatarTempo(avaliacao.data)
            )
        }
    }

    fun carregarDetalhes(productId: String?) {
        _uiState.update { it.copy(isLoading = true) }

        if (productId == null) {
            _uiState.update { it.copy(isLoading = false, erro = "Produto não encontrado") }
            return
        }

        viewModelScope.launch {
            val produto = ProductRepository.getProdutoPorId(productId)

            if (produto == null) {
                _uiState.update { it.copy(isLoading = false, erro = "Produto indisponível") }
                return@launch
            }

            val reviews = carregarReviewsUI(productId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    produto = produto,
                    imagensCarrossel = if (produto.imagens.isNotEmpty())
                        produto.imagens else listOf(produto.imageUrl),
                    nomeDono = produto.donoNome,
                    avaliacoesCount = reviews.size,
                    reviews = reviews,
                    isDonoDoAnuncio = produto.donoId == auth.currentUser?.uid
                )
            }
        }
    }


    private fun encontrarProdutoMock(id: String): Product? {
        val lista = listOf(
            Product(
                pid = "1",
                titulo = "Calculadora HP 12c",
                descricao = "Calculadora usada, perfeita para contabilidade.",
                preco = 15.0,
                categoria = "Calculadoras",
                imageUrl = "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                donoId = "user_mock_1",
                donoNome = "Maria",
                nota = 4.8,
                totalAvaliacoes = 12,
                imagens = listOf(
                    "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                    "https://http2.mlstatic.com/D_NQ_NP_787622-MLB48827768466_012022-O.webp"
                )
            ),
            Product(
                pid = "2",
                titulo = "Jaleco Quixadá",
                descricao = "Tamanho M. Pouco uso.",
                preco = 35.0,
                categoria = "Jalecos",
                imageUrl = "https://photos.enjoei.com.br/jaleco-branco-81336648/800x800/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy8xMzQ3Mzc3NC82MmY4Nzc0OGU2YTQwNzVkM2Q3OGNhMjFkZDZhY2NkNS5qcGc",
                donoId = "user_2",
                donoNome = "João",
                nota = 5.0,
                totalAvaliacoes = 3
            ),
            Product(
                pid = "3",
                titulo = "Kit Arduino",
                descricao = "Kit completo para iniciantes.",
                preco = 20.0,
                categoria = "Eletrônicos",
                imageUrl = "https://cdn.awsli.com.br/78/78150/produto/338952433/kit_arduino_uno_smd_starter_com_caixa_organizadora-3xak1vrhvm.png",
                donoId = "user_3",
                donoNome = "Pedro",
                nota = 4.5
            )
        )
        return lista.find { it.pid == id }
    }
}