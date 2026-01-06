package com.marcos.myspentapp.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.myspentapp.ui.cash_api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CashViewModel : ViewModel() {

    private val _selectedCurrency = MutableStateFlow("BRL")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates = _rates.asStateFlow()

    init {
        fetchRates()
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    private fun fetchRates() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRates("USD")
                _rates.value = response.conversion_rates
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun convertValue(originalBRL: Double, targetCurrency: String, rates: Map<String, Double>): Double {

        // 1) Pega taxa USD → BRL
        val usdToBrl = rates["BRL"] ?: return originalBRL

        // 2) Converte BRL → USD
        val valueInUsd = originalBRL / usdToBrl

        // 3) USD → Moeda desejada
        val usdToTarget = rates[targetCurrency] ?: return originalBRL

        return valueInUsd * usdToTarget
    }

    @SuppressLint("DefaultLocale")
    fun formatCurrency(value: Double): String {
        return String.format("%.2f", value)
    }

}

