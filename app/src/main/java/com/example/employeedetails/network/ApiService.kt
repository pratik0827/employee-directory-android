package com.example.employeedetails.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit API interface defining endpoints for employee data retrieval.
 */
interface ApiService {

    /**
     * Fetches raw JSON response from the public dummy endpoint.
     * Returning [ResponseBody] enables flexible handling of both root JSON arrays
     * and root JSON objects (e.g. {"employees": [...]}).
     */
    @GET("dummy/EmployeeDetails.json")
    suspend fun getEmployeeDetails(): Response<ResponseBody>
}
