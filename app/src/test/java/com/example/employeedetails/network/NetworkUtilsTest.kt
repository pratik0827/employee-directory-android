package com.example.employeedetails.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying JSON parsing resilience for both root-array and wrapped-object payloads.
 */
class NetworkUtilsTest {

    @Test
    fun parseEmployeesJson_wrappedInEmployeesObject_parsesCorrectly() {
        val json = """
            {
              "employees": [
                { "name": "James", "age": 27, "salary": 17000 },
                { "name": "Emma", "age": 25, "salary": 45000 }
              ]
            }
        """.trimIndent()

        val result = NetworkUtils.parseEmployeesJson(json)
        assertEquals(2, result.size)
        assertEquals("James", result[0].name)
        assertEquals(27, result[0].age)
        assertEquals(17000, result[0].salary)
        assertEquals("Emma", result[1].name)
    }

    @Test
    fun parseEmployeesJson_directRootArray_parsesCorrectly() {
        val json = """
            [
              { "name": "Robert", "age": 30, "salary": 25000 },
              { "name": "Kumar", "age": 28, "salary": 14000 }
            ]
        """.trimIndent()

        val result = NetworkUtils.parseEmployeesJson(json)
        assertEquals(2, result.size)
        assertEquals("Robert", result[0].name)
        assertEquals(30, result[0].age)
        assertEquals(25000, result[0].salary)
    }

    @Test
    fun parseEmployeesJson_wrappedInDataObject_parsesCorrectly() {
        val json = """
            {
              "data": [
                { "name": "Ganesh", "age": 35, "salary": 35000 }
              ]
            }
        """.trimIndent()

        val result = NetworkUtils.parseEmployeesJson(json)
        assertEquals(1, result.size)
        assertEquals("Ganesh", result[0].name)
    }

    @Test
    fun parseEmployeesJson_emptyOrBlank_returnsEmptyList() {
        val emptyResult = NetworkUtils.parseEmployeesJson("")
        val blankResult = NetworkUtils.parseEmployeesJson("   ")

        assertTrue(emptyResult.isEmpty())
        assertTrue(blankResult.isEmpty())
    }
}
