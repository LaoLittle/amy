package org.laolittle.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object AmiyaConfig : AutoSavePluginConfig("AmiyaConfig"){
    @ValueDescription("茉莉云Api-Key")
    val api_key: String by value("")

    @ValueDescription("茉莉云Api-Secret")
    val api_secret: String by value("")
}