package org.laolittle.plugin.api.molly

import io.ktor.client.request.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.AmiyaBot
import org.laolittle.plugin.AmiyaConfig
import org.laolittle.plugin.utils.KtorOkHttp.post
import java.security.cert.X509Certificate
import javax.net.ssl.*


object MollyApiService {

    @ExperimentalSerializationApi
    suspend fun request(
        message: String,
        userId: Long,
        userName: String,
        groupName: String?,
        groupId: Long?,
        inGroup: Boolean
    ): MutableList<MollyReply> {
        val mollyUrl = "https://i.mly.app/reply"

        val jsonRequest = buildJsonObject {
            put("content", message)
            put("type", if (!inGroup) 1 else 2)
            put("from", userId)
            put("fromName", userName)
            put("to", groupId)
            put("toName", groupName)
        }

        useInsecureSSL() // 忽略SSL证书
        val json = jsonRequest.toString().post(mollyUrl){
            header("Api-Key", AmiyaConfig.api_key)
            header("Api-Secret", AmiyaConfig.api_secret)
        }
            AmiyaBot.logger.info { "服务器返回数据: $json" }
        return runCatching {
            val mollyData: MollyData = Json.decodeFromString(json)
            decode(mollyData.data)
        }.onFailure {
            val mollyError: MollyError = Json.decodeFromString(json)
            hasError(mollyError)
        }.getOrElse { throw Exception("解析错误! $json") }
    }

    @ExperimentalSerializationApi
    private fun hasError(mollyError: MollyError) {
        AmiyaBot.logger.error {
            """
            回复发生错误! 
            错误代码: ${mollyError.code}
            错误信息: ${mollyError.message}
        """.trimIndent()
        }
    }

    @ExperimentalSerializationApi
    private fun decode(msgData: JsonArray): MutableList<MollyReply> {
        val mollyReply = mutableListOf<MollyReply>()
        for (json in msgData) {
            mollyReply.add(Json.decodeFromJsonElement(json))
        }
        return mollyReply
    }

    private fun useInsecureSSL() {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        })

        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

        val allHostsValid = HostnameVerifier { _, _ -> true }

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
    }
}