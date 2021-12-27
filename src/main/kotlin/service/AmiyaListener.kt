package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.laolittle.plugin.AmiyaBot.dataFolder
import org.laolittle.plugin.AmiyaData
import org.laolittle.plugin.AmiyaFunction
import org.laolittle.plugin.Service
import org.laolittle.plugin.onEnabledGroups
import java.io.File

object AmiyaListener : Service() {
    private val imageFiles: ArrayList<File>
    private val audioFiles: ArrayList<File>
    override suspend fun main() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            if (subject.id in onEnabledGroups) {
                val matchResult = Regex("(?i)(?:阿米娅|amiya)(.*)").find(message.content) ?: return@subscribeAlways
                val enabledFunction = AmiyaData.enabledFunction[subject.id] ?: return@subscribeAlways
                when (matchResult.groupValues[1]) {
                    "签到" -> if (AmiyaFunction.SIGN_IN in enabledFunction) SignIn(subject).broadcast()
                    else -> if (AmiyaFunction.RESPONSE in enabledFunction) Response(
                        subject,
                        sender,
                        matchResult.groupValues[1]
                    ).broadcast()
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
                TODO()
            }
        }
        GlobalEventChannel.subscribeAlways<SignIn> {
            TODO()
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
        imageFiles = readFiles("Image")
        audioFiles = readFiles("Audio")
    }
}