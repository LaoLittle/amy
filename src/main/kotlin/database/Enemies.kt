package org.laolittle.plugin.database

import org.jetbrains.exposed.sql.Table

object Enemies : Table() {
    val name = varchar("name", 50).uniqueIndex()
    val description = text("description")
}