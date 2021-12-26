package org.laolittle.plugin.service

import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.laolittle.plugin.AmiyaBot.dataFolder
import org.laolittle.plugin.AmiyaFunction
import org.laolittle.plugin.AmiyaFunction.*
import java.io.File

object AmiyaController {
    private val imageFiles: ArrayList<File>
    private val audioFiles: ArrayList<File>
    suspend fun Group.enableAmiya(function: AmiyaFunction): Listener<*> = when (function) {
        SIGN_IN -> subscribeAlways<GroupMessageEvent> {
            if (subject != this@enableAmiya) return@subscribeAlways
            if (message.content.contains(Regex("(?i)(?:阿米娅|amiya)签到"))) {
                TODO()
            }
        }
        RESPONSE -> {
            subscribeAlways<GroupMessageEvent> {
                if (subject != this@enableAmiya) return@subscribeAlways
                val matchResult = Regex("(?i)(?:阿米娅|amiya)(.*)").find(message.content)
                println(matchResult?.groupValues?.toString())
                when(matchResult?.let { it.groupValues[1] }){
                    "" -> { subject.randomMessage() }
                    "查看个人信息" -> { TODO() }
                }
            }
        }
        NUDGE -> subscribeAlways<NudgeEvent> {
            if (subject != this@enableAmiya) return@subscribeAlways
            if (target == bot) {
                (subject as Group).randomMessage()
            }
        }
    }

    private inline fun <reified E : Event> subscribeAlways(noinline block: suspend E.() -> Unit): Listener<*> =
        GlobalEventChannel.subscribeAlways<E> { block() }

    private suspend fun Group.randomMessage(){
        when((0..2).random()){
            0 -> { sendMessage("1") }
            1 -> { if (imageFiles.isNotEmpty()) sendImage(imageFiles.random()) }
            2 -> { if (audioFiles.isNotEmpty()) imageFiles.random().toExternalResource().use { uploadAudio(it).sendTo(this) } }
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