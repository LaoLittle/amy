package org.laolittle.plugin.service

import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.laolittle.plugin.AmiyaFunction
import org.laolittle.plugin.AmiyaFunction.SIGN_IN

object AmiyaController {
    fun enableAmiya(type: AmiyaFunction): Listener<*> = when (type) {
        SIGN_IN -> GlobalEventChannel.subscribeAlways<GroupMessageEvent> { }
    }
}