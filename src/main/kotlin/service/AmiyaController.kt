package org.laolittle.plugin.service

import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.MessageEvent

object AmiyaController {
    fun enableAmiya(): Listener<MessageEvent> = GlobalEventChannel.subscribeAlways { }

    fun disableAmiya(): Listener<MessageEvent> = GlobalEventChannel.subscribeAlways { }
}