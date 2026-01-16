package com.example.ecotracker.utils

import com.example.ecotracker.ui.home.viewmodel.TreeStage

fun calculateTreeStage(level: Int): TreeStage{
    return when(level){
        0 -> TreeStage.SEED
        1 -> TreeStage.SMALL_TREE
        2 -> TreeStage.BIG_TREE
        else -> TreeStage.FULL_TREE
    }
}