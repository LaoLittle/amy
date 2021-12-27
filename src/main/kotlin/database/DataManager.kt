package org.laolittle.plugin.database

import kotlinx.coroutines.Dispatchers
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.laolittle.plugin.database.table.UserInformation

object DataManager {
    suspend fun getUserInfo(member: Member): Int?{
        val info = UserInformation
        return suspendTransaction {
            SchemaUtils.create(info)
            val result = info.select { info.userId eq member.id }
            if (result.empty())
                UserInformation.insert { usr ->
                    usr[userNick] = member.nameCardOrNick
                    usr[userId] = member.id
                    usr[headHunting] = 0
                }
            result.singleOrNull()?.get(UserInformation.headHunting)
        }
    }


     suspend inline fun <T> suspendTransaction(crossinline block: suspend (Transaction) -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block(this) }

}