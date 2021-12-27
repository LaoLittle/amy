package org.laolittle.plugin

import com.alibaba.druid.pool.DruidDataSource
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.jvm.AbstractJvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.utils.info
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.laolittle.plugin.database.table.Enemies
import org.laolittle.plugin.service.AmiyaListener
import org.laolittle.plugin.service.AmiyaManager
import java.sql.Connection
import javax.sql.DataSource

object AmiyaBot : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.AmiyaBot",
        name = "AmiyaBot",
        version = "1.0-SNAPSHOT",
    ) {
        author("LaoLittle")
    }
) {
    private val dataSource = DruidDataSource()
    private val db: Database
    override fun onEnable() {
        GlobalEventChannel.subscribeAlways<BotOnlineEvent> {
            init()
            logger.info { "Bot(${bot.id}): Amiya-Bot 初始化完毕" }
        }


        /* GlobalEventChannel.subscribeGroupMessages {
            startsWith("test") {
                val num = it.toInt()
                val info = UserInformation(sender.id)
                transaction {
                    SchemaUtils.create(info)

                    info.insert { usr ->
                        usr[userNick] = sender.nameCardOrNick
                        usr[headHunting] = num
                    }
                }
            }
        }*/
    }
    private fun init() {
        AmiyaConfig.reload()
        AmiyaData.reload()
        AmiyaManager.start()
        AmiyaListener.start()
    }

    fun AbstractJvmPlugin.registerPermission(id: String, description: String): Permission {
        return PermissionService.INSTANCE.register(permissionId(id), description, this.parentPermission)
    }

    init {
        dataSource.url = "jdbc:sqlite:${dataFolder}/AmiyaDB.sqlite"
        dataSource.driverClassName = "org.sqlite.JDBC"
        db = Database.connect(dataSource as DataSource)
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Enemies)
        }
    }
}