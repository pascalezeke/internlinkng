package com.internlinkng.backend.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

object Users : UUIDTable() {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val isAdmin = bool("is_admin").default(false)
    val phoneNumber = varchar("phone_number", 32).nullable()
    val stateOfResidence = varchar("state_of_residence", 64).nullable()
    val profession = varchar("profession", 64).nullable()
    val profilePicture = text("profile_picture").nullable()
    val firstname = varchar("firstname", 64).nullable()
    val lastname = varchar("lastname", 64).nullable()
}

data class User(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val isAdmin: Boolean = false,
    val phoneNumber: String? = null,
    val stateOfResidence: String? = null,
    val profession: String? = null,
    val profilePicture: String? = null,
    val firstname: String? = null,
    val lastname: String? = null
)

fun ResultRow.toUser() = User(
    id = this[Users.id].value,
    email = this[Users.email],
    passwordHash = this[Users.passwordHash],
    isAdmin = this[Users.isAdmin],
    phoneNumber = this[Users.phoneNumber],
    stateOfResidence = this[Users.stateOfResidence],
    profession = this[Users.profession],
    profilePicture = this[Users.profilePicture],
    firstname = this[Users.firstname],
    lastname = this[Users.lastname]
) 