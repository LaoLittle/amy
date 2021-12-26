package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.AmiyaFunction
import org.laolittle.plugin.AmiyaFunction.NUDGE
import org.laolittle.plugin.AmiyaFunction.SIGN_IN

object AmiyaController {
    suspend fun Group.enableAmiya(function: AmiyaFunction): Listener<*> = when (function) {
        SIGN_IN -> subscribeAlways<GroupMessageEvent> {
            if (subject != this@enableAmiya) return@subscribeAlways
            if (message.content == "签到")
                sendMessage(message)
        }
        NUDGE -> subscribeAlways<NudgeEvent> {
            if (subject != this@enableAmiya) return@subscribeAlways
            if (target == bot) {
                sendMessage("yee")
            }
        }
    }

    private inline fun <reified E : Event> subscribeAlways(crossinline block: suspend E.() -> Unit): Listener<*> =
        GlobalEventChannel.subscribeAlways<E> {
            block()
        }
}