package com.internlinkng.data.model

data class SignupRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val phoneNumber: String,
    val stateOfResidence: String,
    val profession: String,
    val profilePicture: String? = null
) 