package org.laolittle.plugin.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.laolittle.plugin.database.table.UserInformation

object DataManager {
    suspend fun getUserInfo(qq: Long) {
        val info = UserInformation(qq)
        suspendTransaction {
            SchemaUtils.create(info)
        }

    }


    private suspend inline fun <T> suspendTransaction(crossinline block: suspend (Transaction) -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block(this) }

}