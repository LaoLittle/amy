package org.laolittle.plugin.service

import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.AmiyaBot
import org.laolittle.plugin.AmiyaBot.registerPermission
import org.laolittle.plugin.AmiyaData.enabledFunction
import org.laolittle.plugin.AmiyaFunction
import org.laolittle.plugin.Service
import org.laolittle.plugin.onEnabledGroups
import org.laolittle.plugin.service.AmiyaController.enableAmiya

object AmiyaManager : Service() {
    private val logger = AmiyaBot.logger
    override suspend fun main() {
        val amiyaAdministratorPerm = AmiyaBot.registerPermission(
            "amiya.operator",
            "Amiya-Bot 管理员权限"
        )
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val matchResult = Regex("(?i)(?:阿米娅|amiya)(.*)").find(message.content)
            if ((sender.isOperator() || sender.permitteeId.hasPermission(amiyaAdministratorPerm)) && (matchResult != null)) {
                val functionMatch = Regex("(打开|关闭)(.*)").find(matchResult.groupValues[1])
                logger.info { matchResult.groupValues.toString() }
                when (matchResult.groupValues[1]) {
                    "上班" -> {
                        val enabled = enabledFunction[subject.id] ?: mutableSetOf(*AmiyaFunction.values())
                        enabledFunction[subject.id] = enabled
                        if (onEnabledGroups[subject.id] != null) {
                            subject.sendMessage("已经在上班了")
                            return@subscribeAlways
                        }
                        val listeners = mutableMapOf<AmiyaFunction, Listener<*>>()
                        enabled.forEach {
                            listeners[it] = enableAmiya(it)
                        }
                        onEnabledGroups[subject.id] = listeners
                        subject.sendMessage("开始996")
                    }
                    "下班" -> {
                        val listeners = onEnabledGroups[subject.id] ?: mutableMapOf()
                        if (onEnabledGroups[subject.id] == null) return@subscribeAlways
                        val enabled = enabledFunction[subject.id] ?: mutableSetOf(*AmiyaFunction.values())
                        enabled.forEach {
                            listeners[it]?.complete()
                        }
                        onEnabledGroups.remove(subject.id)
                        subject.sendMessage("996结束")
                    }
                    "功能关闭列表" -> {
                        val msg = buildMessageChain {
                            AmiyaFunction.values().forEach {
                                if (enabledFunction[subject.id]?.contains(it) == false)
                                    add(it.toString())
                            }
                        }
                        if (msg.isEmpty()){
                            subject.sendMessage("没有关闭的功能")
                            return@subscribeAlways
                        }
                        subject.sendMessage(msg)
                    }
                }
                if (functionMatch != null) {
                    val function = when (functionMatch.groupValues[2]) {
                        "签到" -> AmiyaFunction.SIGN_IN
                        else -> {
                            subject.sendMessage("我不知道你要开启什么")
                            return@subscribeAlways
                        }
                    }
                    val enable = enabledFunction[subject.id] ?: mutableSetOf(*AmiyaFunction.values())

                    when (functionMatch.groupValues[1]) {
                        "打开" -> {
                            if (enable.contains(function)) {
                                subject.sendMessage("此功能已开启")
                                return@subscribeAlways
                            }
                            enable.add(function)
                            enabledFunction[subject.id] = enable
                        }
                        "关闭" -> {
                            if (!enable.contains(function)) {
                                subject.sendMessage("此功能已关闭")
                                return@subscribeAlways
                            }
                            enable.remove(function)
                            enabledFunction[subject.id] = enable
                        }
                    }
                }
            }
        }
    }
}