package org.kde.bettercounter.ui.settings

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kde.bettercounter.persistence.AverageMode
import org.kde.bettercounter.persistence.Repository
import org.kde.bettercounter.sync.WebDavSyncer

class SettingsViewModel(application: Application) {

    private val repo: Repository = Repository.create(application)
    private val syncer = WebDavSyncer()

    fun isAutoExportOnSaveEnabled(): Boolean = repo.isAutoExportOnSaveEnabled()
    fun setAutoExportOnSave(enabled: Boolean) = repo.setAutoExportOnSave(enabled)
    fun getAutoExportFileUri(): String? = repo.getAutoExportFileUri()
    fun setAutoExportFileUri(uriString: String) = repo.setAutoExportFileUri(uriString)
    fun getAverageCalculationMode(): AverageMode = repo.getAverageCalculationMode()
    fun setAverageCalculationMode(mode: AverageMode) = repo.setAverageCalculationMode(mode)

    fun isWebDavSyncEnabled(): Boolean = repo.isWebDavSyncEnabled()
    fun setWebDavSyncEnabled(enabled: Boolean) = repo.setWebDavSyncEnabled(enabled)
    fun getWebDavUrl(): String = repo.getWebDavUrl()
    fun setWebDavUrl(url: String) = repo.setWebDavUrl(url)
    fun getWebDavUsername(): String = repo.getWebDavUsername()
    fun setWebDavUsername(username: String) = repo.setWebDavUsername(username)
    fun getWebDavPassword(): String = repo.getWebDavPassword()
    fun setWebDavPassword(password: String) = repo.setWebDavPassword(password)
    fun getWebDavRemotePath(): String = repo.getWebDavRemotePath()
    fun setWebDavRemotePath(path: String) = repo.setWebDavRemotePath(path)

    fun testWebDavConnection(onResult: (success: Boolean, message: String) -> Unit) {
        val config = repo.getWebDavConfig() ?: run {
            onResult(false, "No URL configured")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncer.testConnection(config)
                withContext(Dispatchers.Main) { onResult(true, "") }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false, e.message ?: "Unknown error") }
            }
        }
    }
}