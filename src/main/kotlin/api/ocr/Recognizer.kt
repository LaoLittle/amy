package org.laolittle.plugin.api.ocr

import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.laolittle.plugin.AmiyaData
import org.laolittle.plugin.utils.KtorOkHttp.post
import java.net.URL
import java.util.*

object Recognizer {

    suspend fun getToken() {
        val json = "".post("https://web.baimiaoapp.com/api/user/login/anonymous") {
            header("x-auth-uuid", getUUID())
        }
    }

    suspend fun recognizeCharacter(image: Image) {
        val base64 = withContext(Dispatchers.IO) {
            val bytes = Base64.getEncoder().encode(URL(image.queryUrl()).openStream().readBytes())
            String(bytes)
        }
        val payload = buildJsonObject {
            put("batchId", "")
            put("dataUrl", "data:image/jpeg;base64,$base64")
        }
        payload.toString().post("") {
            header("x-auth-uuid", getUUID())

        }
    }

    private fun getUUID(doReset: Boolean = false): String {
        if (AmiyaData.uuid.isEmpty() || doReset)
            AmiyaData.uuid = UUID.randomUUID().toString()
        return AmiyaData.uuid
    }
}