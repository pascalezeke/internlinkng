package com.internlinkng.data.model

data class ApplicationRequest(
    val userId: String,
    val hospitalId: String,
    val profession: String,
    val coverLetter: String? = null
) 