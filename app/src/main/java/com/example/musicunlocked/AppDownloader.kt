package com.example.musicunlocked

import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import java.io.IOException

class AppDownloader(private val client: OkHttpClient) : Downloader() {
    @Throws(IOException::class)
    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val data = request.dataToSend()

        val requestBuilder = okhttp3.Request.Builder()
            .url(url)
            .method(httpMethod, data?.toRequestBody())

        for ((key, value) in headers) {
            requestBuilder.addHeader(key, value.joinToString(","))
        }

        client.newCall(requestBuilder.build()).execute().use { okHttpResponse ->
            val responseCode = okHttpResponse.code
            val responseMessage = okHttpResponse.message
            val body = okHttpResponse.body?.string()
            val responseHeaders = okHttpResponse.headers.toMultimap()
            val latestUrl = okHttpResponse.request.url.toString()

            return Response(responseCode, responseMessage, responseHeaders, body, latestUrl)
        }
    }
}
