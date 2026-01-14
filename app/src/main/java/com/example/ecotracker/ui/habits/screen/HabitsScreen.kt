package com.example.ecotracker.ui.habits.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.ecotracker.data.local.entity.Habit
import com.example.ecotracker.ui.habits.components.CounterPill
import com.example.ecotracker.ui.habits.components.FilterButton
import com.example.ecotracker.ui.habits.viewmodel.HabitFilter
import com.example.ecotracker.ui.habits.viewmodel.HabitOperationUiState
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModel
import kotlinx.coroutines.launch
import com.example.ecotracker.ui.habits.components.HabitItem
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory),
    onNavigateToCreateHabit: () -> Unit,
    onNavigateToEditHabit: (habitId: Long) -> Unit
) {
    // Coleta a lista de habitos do ViewModel
    val habits: LazyPagingItems<Habit> = viewModel.habits.collectAsLazyPagingItems()

    // Coleta os estados que a UI precisa observar
    val operationUiState by viewModel.uiState.collectAsState()
    val currentFilter by viewModel.habitFilter.collectAsState()

    // Gerencia o Snackbar para exibir feedbacks
    val snackbarHostState = remember { SnackbarHostState() }

    // Efeito para exibir o Snackbar quando houver mudança de estado
    LaunchedEffect(operationUiState) {
        when (val state = operationUiState) {
            is HabitOperationUiState.HabitAdded -> {
                snackbarHostState.showSnackbar("Hábito criado com sucesso!")
                viewModel.resetState()
            }
            is HabitOperationUiState.HabitUpdated -> {
                snackbarHostState.showSnackbar("Hábito atualizado com sucesso!")
                viewModel.resetState()
            }
            is HabitOperationUiState.HabitDeleted -> {
                snackbarHostState.showSnackbar("Hábito deletado com sucesso!")
                viewModel.resetState()
            }
            is HabitOperationUiState.Error -> {
                snackbarHostState.showSnackbar(state.msg, withDismissAction = true)
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Meus Hábitos") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateHabit) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Hábito")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Cabeçalho com filtros
            HeaderSection(viewModel = viewModel, currentFilter = currentFilter)

            // LazyColumn que exibir a lista de hábitos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Renderiza cada hábito da lista
                items(
                    count = habits.itemCount,
                    key = { index -> habits.peek(index)?.id ?: index }
                ) { index ->
                    val habit = habits[index]
                    if (habit != null) {
                        val completed by viewModel
                            .isHabitCompletedTodayFlow(habit.id)
                            .collectAsState(initial = false)

                        HabitItem(
                            habit = habit,
                            isCompleted = completed,
                            onToggleComplete = {
                                viewModel.toggleHabitCompletion(habit.id)
                            },
                            onEditClick = {
                                onNavigateToEditHabit(habit.id)
                            },
                            onDeleteClick = {
                                viewModel.deleteHabitById(habit.id)
                            }
                        )
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }
                }

                // Gerencia os estados de carregamento da paginação
                when {
                    // Carregamento inicial da tela inteira
                    habits.loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    // Carregando mais itens no final da lista
                    habits.loadState.append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    // Tratamento de Erros de carregamento
                    habits.loadState.refresh is LoadState.Error -> {
                        item {
                            val error = (habits.loadState.refresh as LoadState.Error).error
                            Text(
                                text = "Erro ao carregar hábitos: ${error.localizedMessage}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(viewModel: HabitViewModel, currentFilter: HabitFilter) {
    val totalCount by viewModel.totalHabitsCount.collectAsState()
    val completedCount by viewModel.completedHabitsCount.collectAsState()
    val pendingCount by viewModel.pendingHabitsCount.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Contadores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CounterPill("Totais: $totalCount")
            CounterPill("Concluídos: $completedCount")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seção de Filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            FilterButton(
                text = "Todos",
                isSelected = currentFilter == HabitFilter.ALL,
                onClick = { viewModel.setHabitFilter(HabitFilter.ALL) }
            )
            FilterButton(
                text = "Pendentes",
                isSelected = currentFilter == HabitFilter.PENDING,
                onClick = { viewModel.setHabitFilter(HabitFilter.PENDING) }
            )
            FilterButton(
                text = "Completos",
                isSelected = currentFilter == HabitFilter.COMPLETED,
                onClick = { viewModel.setHabitFilter(HabitFilter.COMPLETED) }
            )
        }
    }
}
