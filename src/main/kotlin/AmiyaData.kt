package org.laolittle.plugin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object AmiyaData : AutoSavePluginData("AmiyaData") {
    @ValueDescription("群聊关闭的功能列表")
    val disabledFunction by value(mutableMapOf<Long, MutableSet<AmiyaFunction>>())
}