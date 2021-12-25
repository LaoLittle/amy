package org.laolittle.plugin.service

import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.AmiyaBot
import org.laolittle.plugin.AmiyaBot.registerPermission
import org.laolittle.plugin.Service

object AmiyaManager : Service() {
    private val logger = AmiyaBot.logger
    override suspend fun main() {
        val amiyaAdministratorPerm = AmiyaBot.registerPermission(
            "amiya.operator",
            "Amiya-Bot 管理员权限"
        )
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val matchResult = Regex("(?i)(?:阿米娅|amiya)(.*)").find(message.content)
            if ((sender.isOperator() || sender.permitteeId.hasPermission(amiyaAdministratorPerm)) && (matchResult != null)){
                logger.info { matchResult.groupValues.toString() }
                when (matchResult.groupValues[1]) {
                    "上班" -> {
                        TODO()
                    }
                    "下班" -> {
                        TODO()
                    }
                    "功能关闭列表" -> {
                        TODO()
                    }
                }
            }
        }
    }
}