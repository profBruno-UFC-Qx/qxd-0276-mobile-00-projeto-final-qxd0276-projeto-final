package com.pegai.app.ui.viewmodel.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class HomeViewModel : ViewModel() {

    // Estado Único da Tela
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Lista "mestra" para filtros locais (evita requery no banco)
    private var todosProdutosCache: List<Product> = emptyList()

    init {
        carregarDadosIniciais()
    }

    // --- AÇÕES DE UI (Intent) ---

    fun selecionarCategoria(categoria: String) {
        _uiState.update { it.copy(categoriaSelecionada = categoria) }
        aplicarFiltros()
    }

    fun atualizarPesquisa(texto: String) {
        _uiState.update { it.copy(textoPesquisa = texto) }
        aplicarFiltros()
    }

    // --- LÓGICA DE DADOS ---

    fun carregarDadosIniciais() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val dadosCarregados = carregarProdutosCompletos()
                todosProdutosCache = dadosCarregados
                ProductRepository.salvarNoCache(dadosCarregados)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        produtos = dadosCarregados,
                        produtosPopulares = dadosCarregados.filter { p -> p.nota >= 4.5 }
                    )
                }
                println(dadosCarregados)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = "Erro ao carregar produtos"

                    )
                }
                Log.e("Firestore", "Erro ao carregar produtos", e)

            }
        }
    }


    private fun aplicarFiltros() {
        val estadoAtual = _uiState.value
        val termo = estadoAtual.textoPesquisa.lowercase()
        val cat = estadoAtual.categoriaSelecionada

        val listaFiltrada = todosProdutosCache.filter { produto ->
            // Filtro de Categoria
            val matchCategoria = if (cat == "Todos") {
                true
            } else {
                produto.categoria.equals(cat, ignoreCase = true)
            }

            // Filtro de Texto (Título ou Descrição)
            val matchTexto = produto.titulo.lowercase().contains(termo) ||
                    produto.descricao.lowercase().contains(termo)

            // Só passa se atender aos DOIS critérios
            matchCategoria && matchTexto
        }

        _uiState.update { it.copy(produtos = listaFiltrada) }
    }

    // --- LÓGICA DE LOCALIZAÇÃO  ---

    @SuppressLint("MissingPermission")
    fun obterLocalizacaoAtual(context: Context) {
        _uiState.update { it.copy(localizacaoAtual = "Buscando...") }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModelScope.launch {
                        converterCoordenadas(context, location.latitude, location.longitude)
                    }
                } else {
                    _uiState.update { it.copy(localizacaoAtual = "GPS indisponível") }
                }
            }.addOnFailureListener {
                _uiState.update { it.copy(localizacaoAtual = "Erro GPS") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(localizacaoAtual = "Sem permissão") }
        }
    }

    private fun converterCoordenadas(context: Context, lat: Double, long: Double) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, long, 1)

            if (!addresses.isNullOrEmpty()) {
                val end = addresses[0]
                val texto = if (end.subLocality != null)
                    "${end.thoroughfare}, ${end.subLocality}"
                else
                    end.thoroughfare ?: "Localização detectada"

                _uiState.update { it.copy(localizacaoAtual = texto) }
            }
        } catch (e: Exception) {
            // Ignora erro de geocoder de forma sileciosa
        }
    }

    // --- MOCK DATA ---
    private fun gerarDadosFalsos(): List<Product> {
        return listOf(
            // PRODUTO 1
            Product(
                pid = "1",
                titulo = "Calculadora HP 12c",
                descricao = "Calculadora usada, perfeita para contabilidade.",
                preco = 15.0,
                categoria = "Calculadoras",
                imageUrl = "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                // Novos campos nomeados:
                donoId = "user_mock_1",
                donoNome = "Maria",
                nota = 4.8,
                totalAvaliacoes = 12,
                imagens = listOf(
                    "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                    "https://http2.mlstatic.com/D_NQ_NP_787622-MLB48827768466_012022-O.webp"
                )
            ),

            // PRODUTO 2 (O que estava dando erro)
            Product(
                pid = "2",
                titulo = "Jaleco Quixadá",
                descricao = "Tamanho M. Pouco uso.",
                preco = 35.0,
                categoria = "Jalecos",
                imageUrl = "https://photos.enjoei.com.br/jaleco-branco-81336648/800x800/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy8xMzQ3Mzc3NC82MmY4Nzc0OGU2YTQwNzVkM2Q3OGNhMjFkZDZhY2NkNS5qcGc",
                // Corrigido aqui:
                donoId = "user_2",
                donoNome = "João",
                nota = 5.0,
                totalAvaliacoes = 3
            ),

            // PRODUTO 3
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
    }

    suspend fun carregarProdutosCompletos(): List<Product> {
        val produtosBase = carregarProdutosBase()

        return produtosBase.map { produto ->

            val donoNome = carregarNomeDono(produto.donoId)
            val (notaMedia, totalAvaliacoes) = calcularAvaliacoes(produto.pid)

            produto.copy(
                donoNome = donoNome,
                nota = notaMedia,
                totalAvaliacoes = totalAvaliacoes
            )
        }
    }

    suspend fun carregarProdutosBase(): List<Product> {
        val snapshot = db.collection("products").get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<Product>()?.copy(pid = doc.id)
        }
    }

    suspend fun carregarNomeDono(donoId: String): String {
        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(donoId)
            .get()
            .await()

        return doc.getString("nome") ?: "Usuário"
    }

    suspend fun calcularAvaliacoes(produtoId: String): Pair<Double, Int> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("avaliacao")
            .whereEqualTo("produtoId", produtoId)
            .get()
            .await()

        if (snapshot.isEmpty) return Pair(5.0, 0)

        val notas = snapshot.documents.mapNotNull {
            it.getDouble("nota")
        }

        val media = notas.average()
        val total = notas.size

        return Pair(media, total)
    }

}