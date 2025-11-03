package com.example.spacexlaunches.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.spacexlaunches.data.api.RetrofitClient
import com.example.spacexlaunches.data.db.LaunchDatabase
import com.example.spacexlaunches.data.model.Launch
import com.example.spacexlaunches.data.repository.LaunchRepository
import kotlinx.coroutines.launch

class LaunchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LaunchRepository
    val launches: LiveData<List<Launch>>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        val launchDao = LaunchDatabase.getDatabase(application).launchDao()
        repository = LaunchRepository(launchDao, RetrofitClient.spaceXApi)
        launches = repository.allLaunches

        // Завантажуємо дані при створенні ViewModel
        loadLaunches()
    }

    fun loadLaunches() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.refreshLaunches()

            result.onSuccess {
                _errorMessage.value = null
            }.onFailure { exception ->
                // Перевіряємо, чи є дані в базі
                val hasData = repository.hasLaunches()
                if (hasData) {
                    _errorMessage.value = "Відображаються кешовані дані. Помилка оновлення: ${exception.message}"
                } else {
                    _errorMessage.value = "Помилка завантаження: ${exception.message}"
                }
            }

            _isLoading.value = false
        }
    }

    fun refreshLaunches() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null

            val result = repository.refreshLaunches()

            result.onSuccess {
                _errorMessage.value = null
            }.onFailure { exception ->
                _errorMessage.value = "Помилка оновлення: ${exception.message}"
            }

            _isRefreshing.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}