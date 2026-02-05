package com.example.ecotracker.ui.home.viewmodel

data class HomeUiState(
    val motivationalPhrase: String = "",
    val points: Int = 0,
    val level: Int = 1,
    val treeStage: TreeStage = TreeStage.SEED,
    val isLoading: Boolean = true,
    val error: String? = null
)
