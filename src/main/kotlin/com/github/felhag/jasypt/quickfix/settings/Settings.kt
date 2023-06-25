package com.github.felhag.jasypt.quickfix.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager

class Settings {
    fun get(): String? {
        return PasswordSafe.instance.getPassword(createCredentialAttributes("password"))
    }

    fun set(password: String) {
        PasswordSafe.instance.setPassword(createCredentialAttributes("password"), password)
    }

    companion object {
        fun get(): Settings = ApplicationManager.getApplication().getService(Settings::class.java)
    }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        return CredentialAttributes("jasypt-spring-boot-quickfix", key)
    }
}
