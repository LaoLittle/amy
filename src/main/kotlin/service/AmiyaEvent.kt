package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.message.data.MessageChain

data class SignIn(
    val group: Group
) : AbstractEvent()

data class Response(
    val group: Group,
    val message: String
) : AbstractEvent()