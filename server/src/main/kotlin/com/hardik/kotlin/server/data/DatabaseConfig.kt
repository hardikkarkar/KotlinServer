package com.hardik.kotlin.server.data

data class DatabaseConfig(
    val name: String,
    val driverClassName: String,
    val jdbcURL: String
)