package org.laolittle.plugin.database

import org.jetbrains.exposed.sql.Table

object UserInformation : Table(){
    val userName = varchar("name", 50)
}