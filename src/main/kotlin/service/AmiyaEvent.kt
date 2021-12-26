package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.AbstractEvent

data class Nudge(
    val group: Group
) : AbstractEvent()

data class SignIn(
    val group: Group
) : AbstractEvent()

data class Response(
    val group: Group
) : AbstractEvent()