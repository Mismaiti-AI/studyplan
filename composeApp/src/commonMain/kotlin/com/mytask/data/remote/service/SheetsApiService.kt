package com.mytask.data.remote.service

import com.mytask.core.config.SheetsApiConfig
import com.mytask.data.remote.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Service for communicating with Google Sheets via Apps Script.
 *
 * Note: No setup() method - sheet structure is created before
 * app generation by Mismaiti backend via OAuth flow.
 *
 * Dependencies injected via Koin:
 * - httpClient: Ktor HttpClient for API calls
 * - config: SheetsApiConfig singleton with script URL
 */
class SheetsApiService(
    private val httpClient: HttpClient,
    private val config: SheetsApiConfig
) {

    private val baseUrl: String
        get() = config.scriptUrl

    /**
     * Test connection to Apps Script
     */
    suspend fun ping(): Boolean {
        return try {
            val response: SheetsResponse<Unit> = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(SheetsRequest(action = "PING"))
            }.body()
            response.success
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get all rows from a sheet
     */
    suspend fun getAll(sheet: String, filters: Map<String, String>? = null): Result<List<Map<String, String>>> {
        return try {
            val response: List<Map<String, String>> = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(SheetsRequest(action = "GET", sheet = sheet, filters = filters))
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get single row by ID
     */
    suspend fun getById(sheet: String, id: String): Result<Map<String, String>?> {
        return try {
            val response: Map<String, String>? = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(SheetsRequest(action = "GET_BY_ID", sheet = sheet, id = id))
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Insert new row
     */
    suspend fun insert(sheet: String, data: Map<String, String>): Result<String> {
        return try {
            val response: InsertResponse = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(SheetsRequest(action = "INSERT", sheet = sheet, data = data))
            }.body()

            if (response.success && response.id != null) {
                Result.success(response.id)
            } else {
                Result.failure(Exception(response.error ?: "Insert failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update existing row
     */
    suspend fun update(sheet: String, id: String, data: Map<String, String>): Result<Unit> {
        return try {
            val response: SheetsResponse<Unit> = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(SheetsRequest(action = "UPDATE", sheet = sheet, id = id, data = data))
            }.body()

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete row
     */
    suspend fun delete(sheet: String, id: String): Result<Unit> {
        return try {
            val response: SheetsResponse<Unit> = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(SheetsRequest(action = "DELETE", sheet = sheet, id = id))
            }.body()

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Delete failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get sheet schema for validation
     */
    suspend fun getSchema(): Result<SchemaResponse> {
        return try {
            val response: SchemaResponse = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(SheetsRequest(action = "GET_SCHEMA"))
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}