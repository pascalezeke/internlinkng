package com.internlinkng.backend.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

object Applications : UUIDTable() {
    val userId = uuid("user_id")
    val hospitalId = uuid("hospital_id")
    val profession = varchar("profession", 100)
    val coverLetter = text("cover_letter").nullable()
    val createdAt = varchar("created_at", 50)
}

data class Application(
    val id: UUID,
    val userId: UUID,
    val hospitalId: UUID,
    val profession: String,
    val coverLetter: String?,
    val createdAt: String
)

fun ResultRow.toApplication() = Application(
    id = this[Applications.id].value,
    userId = this[Applications.userId],
    hospitalId = this[Applications.hospitalId],
    profession = this[Applications.profession],
    coverLetter = this[Applications.coverLetter],
    createdAt = this[Applications.createdAt]
) 