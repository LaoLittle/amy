package org.laolittle.plugin

import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.jvm.AbstractJvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.service.AmiyaManager

object AmiyaBot : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.AmiyaBot",
        name = "AmiyaBot",
        version = "1.0-SNAPSHOT",
    ) {
        author("LaoLittle")
    }
) {
    override fun onEnable() {


        init()
        logger.info { "Amiya-Bot 初始化完毕" }
    }

    fun AbstractJvmPlugin.registerPermission(id: String, description: String): Permission {
        return PermissionService.INSTANCE.register(permissionId(id), description, this.parentPermission)
    }

    private fun init() {
        AmiyaConfig.reload()
        AmiyaData.reload()
        AmiyaManager.start()
    }
}