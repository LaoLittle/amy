package org.laolittle.plugin.database.table

import org.jetbrains.exposed.sql.Table

object UserInformation : Table("user") {
    val userNick = varchar("name", 50)
    val userId = long("id").uniqueIndex()
    val headHunting = integer("headhunting")
}