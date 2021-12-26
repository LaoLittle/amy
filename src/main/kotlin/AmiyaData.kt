package org.laolittle.plugin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.event.Listener

object AmiyaData : AutoSavePluginData("AmiyaData") {
    @ValueDescription("群聊开启的功能列表")
    val enabledFunction by value(mutableMapOf<Long, MutableSet<AmiyaFunction>>())

    @ValueDescription("别名")
    val aliasToGroup by value(mutableMapOf<Long, MutableMap<String, MutableSet<String>>>())
}

val onEnabledGroups: MutableMap<Long, Map<AmiyaFunction, Listener<*>>> = mutableMapOf()