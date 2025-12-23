package com.pegai.app.ui.viewmodel.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel : ViewModel() {

    // Estado Único da Tela
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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

    private fun carregarDadosIniciais() {
        _uiState.update { it.copy(isLoading = true) }

        // Simulação de busca no Backend
        val dadosCarregados = gerarDadosFalsos()
        todosProdutosCache = dadosCarregados

        // Atualiza o estado
        _uiState.update {
            it.copy(
                isLoading = false,
                produtos = dadosCarregados, // Inicialmente mostra tudo
                produtosPopulares = dadosCarregados.filter { p -> p.nota >= 4.7 }
            )
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
            Product("1", "Calculadora HP 12c", "Usada, em bom estado", 5.0, "Eletrônicos", "Maria", nota = 5.0, "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw"),
            Product("2", "Jaleco Quixadá", "Tamanho M, bordado UFC", 15.0, "Jalecos", "João", 4.5,"https://photos.enjoei.com.br/jaleco-branco-81336648/800x800/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy8xMzQ3Mzc3NC82MmY4Nzc0OGU2YTQwNzVkM2Q3OGNhMjFkZDZhY2NkNS5qcGc"),
            Product("3", "Kit Arduino", "Completo com sensores", 20.0, "Eletrônicos", "Toinha", 3.9,"https://cdn.awsli.com.br/78/78150/produto/338952433/kit_arduino_uno_smd_starter_com_caixa_organizadora-3xak1vrhvm.png"),
            Product("4", "Livro Cormen", "A bíblia da computação", 10.0, "Livros", "Adrian", 4.6,"https://img.olx.com.br/images/87/874568196905386.jpg"),
            Product("5", "Câmera Canon Antiga", "Para amantes de fotografia", 35.0, "Eletrônicos", "Helder", 3.5,"https://d1o6h00a1h5k7q.cloudfront.net/imagens/img_m/28309/13751831.jpg")
        )
    }
}