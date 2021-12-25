package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.laolittle.plugin.AmiyaFunction
import org.laolittle.plugin.AmiyaFunction.NUDGE
import org.laolittle.plugin.AmiyaFunction.SIGN_IN

object AmiyaController {
    fun Group.enableAmiya(type: AmiyaFunction): Listener<*> = when (type) {
        SIGN_IN -> subscribeAlways { }
        NUDGE -> subscribeAlways { }
    }

    private fun Group.subscribeAlways(block: GroupMessageEvent.() -> Unit): Listener<GroupMessageEvent> =
        GlobalEventChannel.subscribeAlways Here@{
            if (this@subscribeAlways != subject) return@Here
            block()
        }
}