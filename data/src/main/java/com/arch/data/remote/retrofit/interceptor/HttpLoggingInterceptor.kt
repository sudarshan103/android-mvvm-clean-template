package com.arch.data.remote.retrofit.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * HTTP Logging Interceptor for debugging network requests and responses
 * Logs request headers, body, response headers, body, and response time
 * Should only be used in debuggable builds
 */
class HttpLoggingInterceptor(private val isDebug: Boolean = true) : Interceptor {

    companion object {
        private const val TAG = "HttpLogging"
        private const val MAX_BODY_LENGTH = 4096
        private val UTF8 = StandardCharsets.UTF_8
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isDebug) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val startTime = System.nanoTime()

        logRequest(request)

        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "Request failed with exception: ${e.message}", e)
            throw e
        }

        val elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
        logResponse(response, elapsedTime)

        return response
    }

    private fun logRequest(request: Request) {
        val requestBody = request.body
        val hasRequestBody = requestBody != null

        val startLine = "--> ${request.method} ${request.url}"
        Log.d(TAG, startLine)

        // Log request headers
        val headers = request.headers
        for (i in 0 until headers.size) {
            val name = headers.name(i)
            val value = if (isHeaderSensitive(name)) "***" else headers.value(i)
            Log.d(TAG, "$name: $value")
        }

        // Log request body
        if (hasRequestBody) {
            val contentType = requestBody.contentType()
            if (contentType != null) {
                Log.d(TAG, "Content-Type: $contentType")
            }

            if (isPlaintext(contentType)) {
                Log.d(TAG, requestBodyToString(request))
            } else {
                Log.d(TAG, "(binary ${requestBody.contentLength()}-byte body omitted)")
            }
        }

        Log.d(TAG, "--> END ${request.method}")
    }

    private fun logResponse(response: Response, elapsedTime: Long) {
        val startLine = "<-- ${response.code} ${response.message} (${elapsedTime}ms)"
        Log.d(TAG, startLine)

        // Log response headers
        val headers = response.headers
        for (i in 0 until headers.size) {
            val name = headers.name(i)
            val value = if (isHeaderSensitive(name)) "***" else headers.value(i)
            Log.d(TAG, "$name: $value")
        }

        // Log response body
        val contentTypeHeader = response.headers["content-type"]
        val contentLength = response.headers["content-length"]?.toLongOrNull() ?: 0

        if (hasResponseBody(response)) {
            val responseBody = response.peekBody(MAX_BODY_LENGTH.toLong())
            if (isPlaintextHeader(contentTypeHeader)) {
                val body = responseBody.string()
                val truncated = if (contentLength > MAX_BODY_LENGTH) " (truncated)" else ""
                Log.d(TAG, body + truncated)
            } else {
                Log.d(TAG, "(binary $contentLength-byte body omitted)")
            }
        }

        Log.d(TAG, "<-- END HTTP")
    }

    private fun requestBodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = okio.Buffer()
            copy.body?.writeTo(buffer)
            val charset = copy.body?.contentType()?.charset(UTF8) ?: UTF8
            val body = buffer.readString(charset)
            if (body.length > MAX_BODY_LENGTH) {
                body.substring(0, MAX_BODY_LENGTH) + "... (truncated)"
            } else {
                body
            }
        } catch (e: Exception) {
            "(error reading request body: ${e.message})"
        }
    }

    private fun isPlaintext(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        if (mediaType.type == "text") return true
        val subtype = mediaType.subtype
        return when {
            subtype.contains("json") -> true
            subtype.contains("xml") -> true
            subtype.contains("plain") -> true
            subtype.contains("x-www-form-urlencoded") -> true
            else -> false
        }
    }

    private fun isPlaintextHeader(contentType: String?): Boolean {
        if (contentType == null) return false
        return when {
            contentType.contains("text") -> true
            contentType.contains("json") -> true
            contentType.contains("xml") -> true
            contentType.contains("plain") -> true
            contentType.contains("x-www-form-urlencoded") -> true
            else -> false
        }
    }

    private fun hasResponseBody(response: Response): Boolean {
        // Responses with status codes 204 and 304 never have a body
        if (response.code == 204 || response.code == 304) {
            return false
        }

        // If the response headers don't contain a body, then the body is null
        if (response.request.method == "HEAD") {
            return false
        }

        val contentLength = response.headers["content-length"]
        val transferEncoding = response.headers["transfer-encoding"]

        return when {
            contentLength != null && contentLength != "0" -> true
            transferEncoding != null -> true
            response.headers["content-type"] != null -> true
            else -> false
        }
    }

    private fun isHeaderSensitive(name: String): Boolean {
        return name.equals("Authorization", ignoreCase = true) ||
               name.equals("Cookie", ignoreCase = true) ||
               name.equals("Set-Cookie", ignoreCase = true) ||
               name.equals("X-Auth-Token", ignoreCase = true) ||
               name.equals("X-API-Key", ignoreCase = true)
    }
}

