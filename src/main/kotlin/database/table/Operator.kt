package org.laolittle.plugin.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object Operator : IntIdTable() {
    val name = varchar("name", 5).uniqueIndex()
    val description = text("description")
    val skill = varchar("skill", 5)
}