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

    suspend fun String.post(url: String): String {
        return client.post {
            url(url)
            body = this@post
            header("Content-Type", "application/json;charset=utf-8")
        }
    }
}