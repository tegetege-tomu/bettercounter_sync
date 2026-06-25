package org.kde.bettercounter.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class WebDavSyncer {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Returns file content, or null if the file does not exist (404). Throws on all other errors.
    suspend fun download(config: WebDavConfig): String? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(config.fileUrl())
            .header("Authorization", config.basicAuthHeader())
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            when {
                response.isSuccessful -> response.body?.string()
                response.code == 404 -> null
                response.code == 401 -> throw IOException("Authentication failed (401)")
                else -> throw IOException("Server returned ${response.code}")
            }
        }
    }

    suspend fun upload(config: WebDavConfig, content: String) = withContext(Dispatchers.IO) {
        val body = content.toRequestBody("text/plain; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(config.fileUrl())
            .header("Authorization", config.basicAuthHeader())
            .put(body)
            .build()

        client.newCall(request).execute().use { response ->
            // 200 OK or 201 Created are both success for PUT
            if (!response.isSuccessful && response.code != 201) {
                if (response.code == 401) throw IOException("Authentication failed (401)")
                throw IOException("Upload failed: server returned ${response.code}")
            }
        }
    }

    // Throws IOException describing the problem; returns normally on success (200 or 404 both count).
    suspend fun testConnection(config: WebDavConfig) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(config.fileUrl())
            .header("Authorization", config.basicAuthHeader())
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code == 401) throw IOException("Authentication failed (401)")
            if (!response.isSuccessful && response.code != 404) {
                throw IOException("Server returned ${response.code}")
            }
        }
    }
}
