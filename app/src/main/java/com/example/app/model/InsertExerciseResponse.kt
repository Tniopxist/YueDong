package com.example.app.model

class InsertExerciseResponse (val code: Int, val data: String, val message: String) {
    override fun toString(): String {
        return "InsertExerciseResponse(code=$code, data='$data', message='$message')"
    }
}