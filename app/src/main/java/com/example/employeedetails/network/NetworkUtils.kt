package com.example.employeedetails.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.employeedetails.model.Employee
import com.example.employeedetails.model.EmployeeListResponse
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility functions for networking, connectivity verification, safe coroutine execution,
 * and robust JSON deserialization accommodating dynamic root schemas.
 */
object NetworkUtils {

    private val gson by lazy { Gson() }

    /**
     * Inspects active network capabilities to determine if internet connectivity is available.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Safely executes a suspend network block on [dispatcher], translating known network
     * and HTTP exceptions into structured Kotlin [Result] objects.
     */
    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(dispatcher) {
            try {
                Result.success(apiCall())
            } catch (throwable: Throwable) {
                val readableException = when (throwable) {
                    is UnknownHostException -> {
                        IOException("No internet connection. Please verify your network and retry.", throwable)
                    }
                    is SocketTimeoutException -> {
                        IOException("Connection timed out. Server took too long to respond.", throwable)
                    }
                    is HttpException -> {
                        val code = throwable.code()
                        IOException("Server returned error ($code): ${throwable.message()}", throwable)
                    }
                    is IOException -> {
                        IOException(throwable.message ?: "Network communication error occurred.", throwable)
                    }
                    else -> {
                        Exception(throwable.message ?: "An unexpected error occurred.", throwable)
                    }
                }
                Result.failure(readableException)
            }
        }
    }

    /**
     * Robustly parses the employee JSON payload, gracefully handling both:
     * 1. Direct root JSON array: `[ { "name": "James", ... } ]`
     * 2. Wrapped root JSON object: `{ "employees": [ ... ] }` or `{ "data": [ ... ] }`
     */
    fun parseEmployeesJson(jsonString: String): List<Employee> {
        if (jsonString.isBlank()) return emptyList()

        val jsonElement: JsonElement = JsonParser.parseString(jsonString)

        return when {
            // Case 1: Payload is already a root JSON Array
            jsonElement.isJsonArray -> {
                val listType = object : TypeToken<List<Employee>>() {}.type
                gson.fromJson<List<Employee>>(jsonElement, listType) ?: emptyList()
            }

            // Case 2: Payload is an enclosing JSON Object
            jsonElement.isJsonObject -> {
                val jsonObject = jsonElement.asJsonObject

                // Check for standard "employees" key
                if (jsonObject.has("employees") && jsonObject.get("employees").isJsonArray) {
                    val listType = object : TypeToken<List<Employee>>() {}.type
                    gson.fromJson<List<Employee>>(jsonObject.get("employees"), listType) ?: emptyList()
                }
                // Check for fallback "data" key
                else if (jsonObject.has("data") && jsonObject.get("data").isJsonArray) {
                    val listType = object : TypeToken<List<Employee>>() {}.type
                    gson.fromJson<List<Employee>>(jsonObject.get("data"), listType) ?: emptyList()
                }
                // Fallback: search for the first available JSON array inside the root object
                else {
                    val firstArrayEntry = jsonObject.entrySet().firstOrNull { it.value.isJsonArray }
                    if (firstArrayEntry != null) {
                        val listType = object : TypeToken<List<Employee>>() {}.type
                        gson.fromJson<List<Employee>>(firstArrayEntry.value, listType) ?: emptyList()
                    } else {
                        // Attempt fallback mapping through EmployeeListResponse
                        val wrapper = gson.fromJson(jsonString, EmployeeListResponse::class.java)
                        wrapper.employees ?: wrapper.data ?: emptyList()
                    }
                }
            }

            else -> emptyList()
        }
    }
}
