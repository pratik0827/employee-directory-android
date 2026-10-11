package com.example.employeedetails.repository

import com.example.employeedetails.model.Employee
import com.example.employeedetails.network.ApiService
import com.example.employeedetails.network.NetworkUtils
import com.example.employeedetails.network.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.IOException

/**
 * Repository layer abstracting employee data retrieval from the network.
 */
class EmployeeRepository(
    private val apiService: ApiService = RetrofitClient.apiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Fetches employee data from the remote endpoint, gracefully parses the response,
     * and wraps the result in a Kotlin [Result].
     */
    suspend fun getEmployees(): Result<List<Employee>> {
        return NetworkUtils.safeApiCall(ioDispatcher) {
            val response = apiService.getEmployeeDetails()
            if (!response.isSuccessful) {
                throw IOException("Server returned HTTP error ${response.code()}: ${response.message()}")
            }

            val bodyString = response.body()?.string()
                ?: throw IOException("Empty response received from server.")

            NetworkUtils.parseEmployeesJson(bodyString)
        }
    }
}
