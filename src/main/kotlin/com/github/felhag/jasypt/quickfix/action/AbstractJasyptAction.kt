package com.github.felhag.jasypt.quickfix.action

import com.github.felhag.jasypt.quickfix.Environment
import com.github.felhag.jasypt.quickfix.settings.Settings
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.containers.stream
import com.ulisesbocchio.jasyptspringboot.configuration.StringEncryptorBuilder
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties
import org.apache.commons.lang.StringUtils
import org.jasypt.encryption.StringEncryptor
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.core.env.StandardEnvironment
import org.springframework.core.io.FileSystemResource
import java.io.File

const val PREFIX = "ENC("
const val SUFFIX = ")"
const val FILE_PREFIX = "application-"

abstract class AbstractJasyptAction(private val name: String) : BaseIntentionAction() {

    override fun getFamilyName(): String {
        return name
    }

    override fun getText(): String {
        return name
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (getEnvironment(file) == null) {
            return false
        }
        val element = findElement(file, editor)
        return element != null && isValidProperty(element)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val element = this.findElement(file, editor) ?: return

        // Force to use intellijs classloader
        val classLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = this.javaClass.classLoader

        val properties = buildProperties(file!!)
        val builder = StringEncryptorBuilder(properties, "jasypt.encryptor").build()
        val decrypt = try {
            execute(builder, element.text)
        } catch (e: Exception) {
            invokeLater { Messages.showErrorDialog("Failed to ${name.lowercase()} 😢", "Error!") }
            return
        }

        val document = editor!!.document
        document.deleteString(element.startOffset, element.endOffset)
        document.insertString(element.startOffset, decrypt)

        // Reset classloader
        Thread.currentThread().contextClassLoader = classLoader
    }

    private fun buildProperties(file: PsiFile): JasyptEncryptorConfigurationProperties {
        val virtualFile = file.virtualFile
        val path = StringUtils.removeEnd(virtualFile.path, virtualFile.name)
        val properties = mergeProperties(path, "application.yml", "application.yaml", virtualFile.name)
        val env = getEnvironment(file)!!
        properties.password = Settings.get().get(env)
        return properties
    }

    private fun mergeProperties(path: String, vararg files: String): JasyptEncryptorConfigurationProperties {
        val env = StandardEnvironment()
        files.stream()
            .map { "$path$it" }
            .map(::File)
            .filter(File::exists)
            .map(::FileSystemResource)
            .flatMap { YamlPropertySourceLoader().load("application-${env.propertySources.size()}", it).stream() }
            .forEach { env.propertySources.addLast(it) }
        return JasyptEncryptorConfigurationProperties.bindConfigProps(env)
    }

    private fun getEnvironment(file: PsiFile?): Environment? {
        if (file == null || file.virtualFile == null) {
            return null
        }
        val fileName = file.virtualFile.name
        if (!fileName.startsWith(FILE_PREFIX)) {
            return null
        }

        val envLetter = fileName.substring(FILE_PREFIX.length, FILE_PREFIX.length + 1)
        return Environment.values().find { it.name == envLetter.uppercase() }
    }

    abstract fun execute(encryptor: StringEncryptor, text: String): String

    abstract fun isValidProperty(element: PsiElement): Boolean

    private fun findElement(file: PsiFile?, editor: Editor?): PsiElement? {
        if (file == null || editor == null) {
            return null
        }
        val offset: Int = editor.caretModel.offset
        val element = file.findElementAt(offset)
        if ("\n" == element?.text) {
            return element.prevSibling.lastChild.lastChild
        }
        return element
    }
}
