package org.kde.bettercounter.sync

import android.util.Base64

data class WebDavConfig(
    val url: String,
    val username: String,
    val password: String,
    val remotePath: String,
) {
    fun fileUrl(): String = url.trimEnd('/') + "/" + remotePath.trimStart('/')

    fun basicAuthHeader(): String {
        val credentials = "$username:$password"
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }
}
