package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.AbstractEvent

data class Response(
    val group: Group,
    val sender: Member,
    val message: String
) : AbstractEvent()

data class SignIn(
    val group: Group
) : AbstractEvent()

data class Gacha(
    val group: Group
) : AbstractEvent()

data class QueryOperator(
    val group: Group
) : AbstractEvent()

data class QueryEnemy(
    val group: Group
) : AbstractEvent()

data class QueryItem(
    val group: Group
) : AbstractEvent()

data class QueryOffer(
    val group: Group
) : AbstractEvent()

data class Calc(
    val group: Group
) : AbstractEvent()

data class Weibo(
    val group: Group
) : AbstractEvent()