package com.mytask.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SheetsRequest(
    val action: String,
    val sheet: String? = null,
    val data: Map<String, String>? = null,
    val filters: Map<String, String>? = null,
    val id: String? = null,
    val models: List<ModelDefinition>? = null
)

@Serializable
data class ModelDefinition(
    val name: String,
    val icon: String? = null,
    val fields: List<FieldDefinition>
)

@Serializable
data class FieldDefinition(
    val name: String,
    val label: String? = null,
    val type: String? = null,  // "status", "string", etc.
    val width: Int? = null,
    val hidden: Boolean = false
)

@Serializable
data class SheetsResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val error: String? = null
)

@Serializable
data class InsertResponse(
    val success: Boolean,
    val id: String? = null,
    val error: String? = null
)

@Serializable
data class SchemaResponse(
    val sheets: List<SheetSchema>
)

@Serializable
data class SheetSchema(
    val name: String,
    val columns: List<String>
)