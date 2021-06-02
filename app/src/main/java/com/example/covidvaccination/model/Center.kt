package com.example.covidvaccination.model

data class Center(
    val address: String,
    val district: String,
    val fees: String,
    val from: String,
    val name: String,
    val vaccine: String,
    val state: String,
    val to: String,
    val ageLimit: Int,
    val availability: Int
)