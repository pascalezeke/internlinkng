package com.internlinkng.data.model

import com.google.gson.annotations.SerializedName

data class Hospital(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("professions")
    val professions: List<String>,
    @SerializedName("salaryRange")
    val salaryRange: String,
    @SerializedName("deadline")
    val deadline: String,
    @SerializedName("created")
    val created: String, // Added created date
    @SerializedName("onlineApplication")
    val onlineApplication: Boolean,
    @SerializedName("applicationUrl")
    val applicationUrl: String?,
    @SerializedName("physicalAddress")
    val physicalAddress: String?,
    @SerializedName("professionSalaries")
    val professionSalaries: Map<String, String>? = null
) 