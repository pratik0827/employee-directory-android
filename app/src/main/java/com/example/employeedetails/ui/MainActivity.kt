package com.example.employeedetails.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.employeedetails.adapter.EmployeeAdapter
import com.example.employeedetails.databinding.ActivityMainBinding
import com.example.employeedetails.model.Employee
import com.example.employeedetails.viewmodel.EmployeeViewModel
import com.example.employeedetails.viewmodel.UiState
import kotlinx.coroutines.launch

/**
 * Main launcher Activity displaying a list of employees fetched asynchronously.
 * Observes ViewModel's UiState lifecycle-safely and handles navigation to detail screen.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: EmployeeViewModel by viewModels()
    private lateinit var employeeAdapter: EmployeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        employeeAdapter = EmployeeAdapter { selectedEmployee ->
            navigateToDetail(selectedEmployee)
        }

        binding.recyclerViewEmployees.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = employeeAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadEmployees()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.loadEmployees()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderUiState(state)
                }
            }
        }
    }

    private fun renderUiState(state: UiState<List<Employee>>) {
        when (state) {
            is UiState.Loading -> {
                if (!binding.swipeRefreshLayout.isRefreshing) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                binding.layoutError.visibility = View.GONE
                binding.layoutEmpty.visibility = View.GONE
            }

            is UiState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                binding.layoutError.visibility = View.GONE
                binding.layoutEmpty.visibility = View.GONE
                binding.recyclerViewEmployees.visibility = View.VISIBLE

                employeeAdapter.submitList(state.data)
            }

            is UiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                binding.recyclerViewEmployees.visibility = View.GONE
                binding.layoutEmpty.visibility = View.GONE
                binding.layoutError.visibility = View.VISIBLE

                binding.tvErrorMessage.text = state.message
            }

            is UiState.Empty -> {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                binding.recyclerViewEmployees.visibility = View.GONE
                binding.layoutError.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToDetail(employee: Employee) {
        val intent = Intent(this, EmployeeDetailActivity::class.java).apply {
            putExtra(EmployeeDetailActivity.EXTRA_EMPLOYEE, employee)
        }
        startActivity(intent)
    }
}
