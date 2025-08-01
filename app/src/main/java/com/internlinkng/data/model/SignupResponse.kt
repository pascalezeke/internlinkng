package com.internlinkng.data.model

data class SignupResponse(
    val token: String,
    val userId: String,
    val isAdmin: Boolean
) 