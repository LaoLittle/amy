package org.laolittle.plugin.service

import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.BotMuteEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.error
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
                val listeners: MutableMap<AmiyaFunction, Listener<*>> =
                    onEnabledGroups[subject.id]?.toMutableMap() ?: mutableMapOf()
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
                        enabled.forEach {
                            listeners[it] = subject.enableAmiya(it)
                        }
                        onEnabledGroups[subject.id] = listeners
                        subject.sendMessage("开始996")
                    }
                    "下班" -> {
                        if (onEnabledGroups[subject.id] == null) return@subscribeAlways
                        listeners.forEach {
                            it.value.complete()
                        }
                        onEnabledGroups.remove(subject.id)
                        subject.sendMessage("996结束")
                    }
                    "功能关闭列表" -> {
                        val msg = buildMessageChain {
                            AmiyaFunction.values().forEach {
                                if (enabledFunction[subject.id]?.contains(it) == false)
                                    add("$it ,")
                            }
                        }
                        if (msg.isEmpty()) {
                            subject.sendMessage("没有关闭的功能")
                            return@subscribeAlways
                        }
                        subject.sendMessage(msg)
                    }
                    else -> {
                        val function = when (functionMatch?.let { it.groupValues[2] }) {
                            "签到" -> AmiyaFunction.SIGN_IN
                            "戳一戳" -> AmiyaFunction.NUDGE
                            else -> {
                                subject.sendMessage("我不知道你要开启什么")
                                return@subscribeAlways
                            }
                        }
                        val enable = enabledFunction[subject.id] ?: mutableSetOf(*AmiyaFunction.values())

                        when (functionMatch.groupValues[1]) {
                            "打开" -> {
                                if (enable.add(function)) {
                                    subject.sendMessage("已开启")
                                    if (onEnabledGroups.contains(subject.id)) listeners[function] =
                                        subject.enableAmiya(function)
                                } else {
                                    subject.sendMessage("此功能已开启")
                                    return@subscribeAlways
                                }
                                enabledFunction[subject.id] = enable
                            }
                            "关闭" -> {
                                if (enable.remove(function)) {
                                    subject.sendMessage("已关闭")
                                    if (onEnabledGroups.contains(subject.id)) listeners[function]?.complete()
                                        ?: logger.error { "发生预料外的错误" }
                                } else {
                                    subject.sendMessage("此功能已关闭")
                                    return@subscribeAlways
                                }
                                enabledFunction[subject.id] = enable
                            }
                        }
                    }
                }
            }
        }

        GlobalEventChannel.subscribeAlways<BotMuteEvent> {
            val muteMessage = "被 ${group.name}($groupId) 的 ${operator.nameCardOrNick}(${operator.id}) 禁言"
            if (group.quit()) logger.info { "$muteMessage, 已自动退群" }
            else logger.error { "$muteMessage, 但已被踢出, 退群失败" }
        }
    }
}