package org.laolittle.plugin.utils

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import java.io.InputStream

object KtorOkHttp {
    private val client = HttpClient(OkHttp)

    suspend fun get(url: String): InputStream {
        return client.get(url)
    }

    suspend fun String.post(url: String, block: HttpRequestBuilder.() -> Unit = {}): String {
        return client.post {
            url(url)
            body = this@post
            block()
            header("Content-Type", "application/json;charset=utf-8")
            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.62")
        }
    }
}