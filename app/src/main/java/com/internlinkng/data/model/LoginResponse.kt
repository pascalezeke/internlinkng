package com.internlinkng.data.model

data class LoginResponse(
    val token: String,
    val userId: String,
    val isAdmin: Boolean
) 