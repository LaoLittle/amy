package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.data.sendTo
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
                    else -> if (AmiyaFunction.RESPONSE in enabledFunction) Response(subject).broadcast()
                }
            }
        }
        GlobalEventChannel.subscribeAlways<NudgeEvent> {
            val group = subject
            if (group is Group && group.id in onEnabledGroups)
                if (AmiyaData.enabledFunction[group.id]?.contains(AmiyaFunction.NUDGE) == true) Nudge(group).broadcast()
        }

        GlobalEventChannel.subscribeAlways<SignIn> {
            group.sendMessage("Passed")
        }
    }

    private suspend fun Group.randomMessage() {
        when ((0..2).random()) {
            0 -> {
                sendMessage("1")
            }
            1 -> {
                if (imageFiles.isNotEmpty()) sendImage(imageFiles.random())
            }
            2 -> {
                if (audioFiles.isNotEmpty()) imageFiles.random().toExternalResource()
                    .use { uploadAudio(it).sendTo(this) }
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