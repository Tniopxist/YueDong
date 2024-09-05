package com.example.app.model

data class PaginatedData(
    val list: List<ExerciseRecord>,
    val page: Int,
    val pageSize: Int,
    val total: Int
)

data class GetHealthStatusListResponse(
    val code: Int,
    val data: PaginatedData,
    val message: String
) {
    override fun toString(): String {
        return "GetHealthStatusListResponse(code=$code, data=$data, message='$message')"
    }
}
