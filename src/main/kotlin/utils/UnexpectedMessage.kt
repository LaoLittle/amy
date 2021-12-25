package org.laolittle.plugin.utils

import net.mamoe.mirai.contact.Contact

object UnexpectedMessage {
    suspend fun Contact.sendDefault(){
        sendMessage("怎么了？")
    }
}