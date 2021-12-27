package org.laolittle.plugin.service

import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.laolittle.plugin.AmiyaBot.dataFolder
import org.laolittle.plugin.AmiyaData
import org.laolittle.plugin.AmiyaFunction
import org.laolittle.plugin.Service
import org.laolittle.plugin.database.DataManager
import org.laolittle.plugin.database.table.UserInformation
import org.laolittle.plugin.database.table.UserInformation.headHunting
import org.laolittle.plugin.api.molly.MollyApiService
import org.laolittle.plugin.onEnabledGroups
import org.laolittle.plugin.utils.KtorOkHttp
import java.io.File

object AmiyaListener : Service() {
    private val imageFiles: ArrayList<File>
    private val audioFiles: ArrayList<File>
    const val matchNumber = "[一二三四五六七八九十]"
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun main() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            if (subject.id in onEnabledGroups) {
                val matchResult =
                    Regex("(?i)(?:阿米娅|amiya)(签到|查看|公招|理智)(.*)").find(message.content)
                val enabledFunction = AmiyaData.enabledFunction[subject.id] ?: return@subscribeAlways
                if (matchResult== null){
                    if (AmiyaFunction.RESPONSE in enabledFunction) {
                        Response(
                            subject,
                            sender,
                            message.content
                        ).broadcast()
                    }
                    return@subscribeAlways
                }

                when (matchResult.groupValues[1]) {
                    "签到" -> if (AmiyaFunction.SIGN_IN in enabledFunction) SignIn(subject, sender).broadcast()
                    "查看" -> {
                        val matchQuery = Regex("(?i)(?:敌人|材料|物品)(.*)").find(matchResult.groupValues[2])

                        with(matchQuery?.let { it.groupValues[1] }) {
                            if (!this.isNullOrBlank()) {
                                when (this) {
                                    "敌人" -> if (AmiyaFunction.QUERY_ENEMY in enabledFunction) QueryEnemy(subject).broadcast()
                                    "材料", "物品" -> if (AmiyaFunction.QUERY_ITEM in enabledFunction) QueryItem(subject).broadcast()
                                }
                            }
                        }
                    }
                    "公招" -> if (AmiyaFunction.QUERY_OFFER in enabledFunction) QueryOffer(subject).broadcast()
                    "理智" -> {
                        TODO()
                    }
                }

            }
        }

        GlobalEventChannel.subscribeAlways<NudgeEvent> {
            val group = subject
            if (group is Group && group.id in onEnabledGroups)
                if (AmiyaData.enabledFunction[group.id]?.contains(AmiyaFunction.NUDGE) == true) Response(
                    group,
                    from as Member,
                    ""
                ).broadcast()
        }

        GlobalEventChannel.subscribeAlways<Response> {
            if (message.isEmpty()) group.randomMessage(sender)?.sendTo(group)
            else {
                val mollyReplyTempo = MollyApiService.request(
                    message = message,
                    userId = sender.id,
                    userName = sender.nameCardOrNick,
                    groupName = group.name,
                    groupId = group.id,
                    true
                )
                mollyReplyTempo.forEach { receive ->
                    when (receive.typed) {
                        1 -> {
                            val send = buildMessageChain {
                                add(receive.content)
                            }
                            val random = (100..3000).random().toLong()
                            delay(random)
                            group.sendMessage(send)
                        }

                        2 -> {
                            val url = "https://files.molicloud.com/" + receive.content
                            KtorOkHttp.get(url).use { group.sendImage(it) }
                        }

                        4 -> {
                            val receiver = group
                            val url = "https://files.molicloud.com/" + receive.content
                            KtorOkHttp.get(url)
                                .use { input -> input.toExternalResource().use { receiver.uploadAudio(it).sendTo(group) } }
                        }

                        else -> {
                            group.sendMessage("https://files.molicloud.com/${receive.content}")
                        }
                    }
                }
            }
        }

        GlobalEventChannel.subscribeAlways<SignIn> {
            val random = (1..10).random()
            group.sendMessage((DataManager.getUserInfo(sender) ?: return@subscribeAlways).toString())

            DataManager.suspendTransaction {
             val a = UserInformation.select { UserInformation.userId eq sender.id }.singleOrNull()?.get(headHunting) ?: 0
             UserInformation.update({ UserInformation.userId eq sender.id }) {
                 it[headHunting] = random + a
             }
            }
        }
    }

    private suspend fun Group.randomMessage(target: Member): Message? {
        return when ((0..3).random()) {
            0 -> {
                At(target) + PlainText("TODO")
            }
            1 -> {
                if (imageFiles.isNotEmpty()) imageFiles.random().toExternalResource()
                    .use { uploadImage(it) } else null
            }
            2 -> {
                target.nudge().sendTo(this)
                null
            }
            else -> {
                if (audioFiles.isNotEmpty()) imageFiles.random().toExternalResource()
                    .use { uploadAudio(it) } else null
            }
        }
    }

    private fun readFiles(path: String): ArrayList<File> {
        val files = arrayListOf<File>()
        val folder = dataFolder.resolve(path)
        if (!folder.exists()) folder.mkdir()
        folder.listFiles()?.forEach {
            if (!it.isDirectory) files.add(it)
        }
        return files
    }

    init {
        imageFiles = readFiles("Face")
        audioFiles = readFiles("Audio")
    }
}