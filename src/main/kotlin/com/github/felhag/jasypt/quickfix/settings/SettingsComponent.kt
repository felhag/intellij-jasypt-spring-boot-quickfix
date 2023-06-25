package com.github.felhag.jasypt.quickfix.settings

import com.github.felhag.jasypt.quickfix.Environment
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class SettingsComponent {
    var panel: JPanel? = null

    private val passwords = Environment.values().associateBy({ it }, { JBPasswordField() })

    init {
        val builder = FormBuilder.createFormBuilder()
        passwords.forEach { builder.addLabeledComponent("Jasypt password ${it.key.name}", it.value) }
        panel = builder.addComponentFillVertically(JPanel(), 0).panel
    }

    fun isModified(): Boolean {
        val settings = Settings.get()
        return !settings.getAll().all { passwords[it.key]!!.password.equals(it.value) }
    }

    fun apply() {
        passwords.forEach { Settings.get().set(it.key, String(it.value.password)) }
    }

    fun reset() {
        passwords.forEach { it.value.text = Settings.get().get(it.key) }
    }
}
