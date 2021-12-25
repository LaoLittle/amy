package org.laolittle.plugin.service

import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import org.laolittle.plugin.AmiyaBot
import org.laolittle.plugin.AmiyaBot.registerPermission
import org.laolittle.plugin.Service

object AmiyaManager : Service() {
    override suspend fun main() {
        val amiyaPerm = AmiyaBot.registerPermission(
            "amiya.operator",
            "Amiya-Bot 管理员权限"
        )
        GlobalEventChannel.subscribeGroupMessages {
            finding(Regex("(?i)(阿米娅|amiya)(休息|下班)")){
                TODO("暂时做到这")
            }
        }
    }
}