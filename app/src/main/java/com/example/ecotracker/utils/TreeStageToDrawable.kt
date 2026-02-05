package com.example.ecotracker.utils

import androidx.annotation.DrawableRes
import com.example.ecotracker.R
import com.example.ecotracker.ui.home.viewmodel.TreeStage

@DrawableRes
fun treeStageToDrawable(stage: TreeStage): Int {
    return when(stage){
        TreeStage.SEED -> R.drawable.tree_stage1
        TreeStage.SMALL_TREE -> R.drawable.tree_stage2
        TreeStage.BIG_TREE -> R.drawable.tree_stage3
        TreeStage.FULL_TREE -> R.drawable.tree_stage4
    }
}