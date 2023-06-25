package com.github.felhag.jasypt.quickfix.settings

import com.github.felhag.jasypt.quickfix.Environment
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager

class Settings {

    fun get(env: Environment): String? {
        return PasswordSafe.instance.getPassword(createCredentialAttributes(env))
    }

    fun getAll(): Map<Environment, String?> {
        return Environment.values().associateBy({ it }, { get(it) })
    }

    fun set(env: Environment, password: String) {
        PasswordSafe.instance.setPassword(createCredentialAttributes(env), password)
    }

    companion object {
        fun get(): Settings = ApplicationManager.getApplication().getService(Settings::class.java)
    }

    private fun createCredentialAttributes(env: Environment): CredentialAttributes {
        // Apparently saving multiple "users" with the same service name isn't possible yet...
        // https://youtrack.jetbrains.com/issue/IDEA-203412/PasswordSafe-overwrites-secrets-of-different-users
        val key = "jasypt-spring-boot-quickfix " + env.name + "-password"
        return CredentialAttributes(key, null)
    }
}
