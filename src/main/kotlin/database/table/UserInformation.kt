package org.laolittle.plugin.database.table

import org.jetbrains.exposed.sql.Table

class UserInformation(qq: Long) : Table("info_$qq") {
    val userNick = varchar("name", 50)
    val headHunting = integer("headhunting")
}