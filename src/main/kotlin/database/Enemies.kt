package org.laolittle.plugin.database

import org.ktorm.schema.Table
import org.ktorm.schema.varchar


object Enemies : Table<Nothing>("a_enemies") {
    val name = varchar("name")
    val aliasName = varchar("alias")

}