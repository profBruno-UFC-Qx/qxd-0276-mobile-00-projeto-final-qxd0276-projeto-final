package com.example.ecotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.data.local.database.DatabaseProvider
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.ui.addHabit.HabitViewModel
import com.example.ecotracker.ui.habits.HabitViewModelFactory
import com.example.ecotracker.ui.habits.AddHabitScreen
import com.example.ecotracker.ui.theme.EcoTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ Cria o banco
        val database = DatabaseProvider.getDatabase(this)

        // 2️⃣ Obtém o DAO
        val habitDao = database.habitDao()

        // 3️⃣ Cria o Repository
        val habitRepository = HabitRepository(habitDao)

        setContent {
            EcoTrackerTheme {

                // 4️⃣ Cria o ViewModel com Factory
                val viewModel: HabitViewModel = viewModel(
                    factory = HabitViewModelFactory(habitRepository)
                )

                // 5️⃣ Chama a tela
                AddHabitScreen(viewModel = viewModel)
            }
        }
    }
}