package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.AbstractEvent

data class SignIn(
    val group: Group
) : AbstractEvent()

data class Response(
    val group: Group,
    val sender: Member,
    val message: String
) : AbstractEvent()