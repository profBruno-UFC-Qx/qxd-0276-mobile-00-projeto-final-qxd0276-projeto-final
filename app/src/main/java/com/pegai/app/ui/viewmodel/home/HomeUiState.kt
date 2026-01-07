package com.pegai.app.ui.viewmodel.home

import com.pegai.app.model.Product
import com.pegai.app.model.User

data class HomeUiState(
    // Dados da UI
    val produtos: List<Product> = emptyList(),
    val produtosPopulares: List<Product> = emptyList(),
    val categorias: List<String> = listOf("Todos", "Livros", "Calculadoras", "Jalecos", "Eletrônicos", "Outros"),

    // Estados de Seleção/Filtro
    val categoriaSelecionada: String = "Todos",
    val textoPesquisa: String = "",

    // Estados de Hardware/Sistema
    val localizacaoAtual: String = "Localização desconhecida",
    val isLoading: Boolean = false,
    val erro: String? = null,
)