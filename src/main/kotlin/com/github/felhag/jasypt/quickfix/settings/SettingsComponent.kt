package com.github.felhag.jasypt.quickfix.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel


class SettingsComponent {
    var panel: JPanel? = null

    private val password = JBPasswordField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Jasypt password", password)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun isModified(): Boolean {
        val current = Settings.get().get()
        val equal = String(password.password).equals(current);
        return !equal
    }

    fun apply() {
        Settings.get().set(String(password.password));
    }

    fun reset() {
        password.text = Settings.get().get()
    }
}
