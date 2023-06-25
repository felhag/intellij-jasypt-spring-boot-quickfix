package com.github.felhag.jasypt.quickfix.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class SettingsConfigurer : Configurable {
    private var settingsComponent: SettingsComponent? = null

    override fun getDisplayName(): String {
        return "Jasypt Spring Boot Quickfix"
    }

    override fun createComponent(): JComponent? {
        settingsComponent = SettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        return settingsComponent!!.isModified()
    }

    override fun apply() {
        settingsComponent!!.apply()
    }

    override fun reset() {
        settingsComponent!!.reset()
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
