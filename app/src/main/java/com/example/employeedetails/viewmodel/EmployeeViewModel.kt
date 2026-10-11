package com.example.employeedetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeedetails.model.Employee
import com.example.employeedetails.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the UI state for the employee list screen.
 * Survives configuration changes and maintains a unidirectional data flow.
 */
class EmployeeViewModel(
    private val repository: EmployeeRepository = EmployeeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Employee>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Employee>>> = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    /**
     * Triggers asynchronous fetching of employees from the repository.
     */
    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getEmployees()
                .onSuccess { employeeList ->
                    _uiState.value = if (employeeList.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(employeeList)
                    }
                }
                .onFailure { throwable ->
                    val errorMessage = throwable.localizedMessage 
                        ?: "An unexpected network error occurred. Please try again."
                    _uiState.value = UiState.Error(errorMessage)
                }
        }
    }
}
