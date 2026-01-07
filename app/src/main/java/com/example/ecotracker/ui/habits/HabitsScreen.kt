package com.example.ecotracker.ui.habits

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.ecotracker.ui.addHabit.HabitViewModel

@Composable
fun HabitsScreen(viewModel: HabitViewModel){
    val habits = viewModel.habits.collectAsLazyPagingItems()

    Text( text = "Habitos Carregados: ${habits.itemCount}")
    LazyColumn{
        items(count = habits.itemCount) { index ->
            val habit = habits[index]
            habit?.let {
                Text(text = it.name)
            }
        }
        // Load Inicial
        if (habits.loadState.refresh is LoadState.Loading) {
            item {
                CircularProgressIndicator()
            }
        }
        // Load ao carregar mais páginas
        if (habits.loadState.append is LoadState.Loading) {
            item {
                CircularProgressIndicator()
            }
        }
    }
}