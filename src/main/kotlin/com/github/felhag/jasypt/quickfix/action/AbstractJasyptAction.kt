package com.github.felhag.jasypt.quickfix.action

import com.github.felhag.jasypt.quickfix.Environment
import com.github.felhag.jasypt.quickfix.settings.Settings
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.ulisesbocchio.jasyptspringboot.configuration.StringEncryptorBuilder
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties
import org.jasypt.encryption.StringEncryptor

const val PREFIX = "ENC(";
const val SUFFIX = ")";
abstract class AbstractJasyptAction(private val name: String) : BaseIntentionAction() {

    override fun getFamilyName(): String {
        return name
    }

    override fun getText(): String {
        return name
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (getEnvironment(file) == null) {
            return false;
        }
        val element = findElement(file, editor)
        return element != null && isValidProperty(element)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val element = this.findElement(file, editor) ?: return

        // Force to use intellijs classloader
        Thread.currentThread().contextClassLoader = this.javaClass.classLoader

        val properties = JasyptEncryptorConfigurationProperties()
        val env = getEnvironment(file)!!
        properties.password = Settings.get().get(env)
        properties.algorithm = "PBEWithMD5AndDES"
        properties.ivGeneratorClassname = "org.jasypt.iv.NoIvGenerator"

        val builder = StringEncryptorBuilder(
            properties,
            "jasypt.encryptor"
        ).build()

        val decrypt = execute(builder, element.text)
        val document = editor!!.document
        document.deleteString(element.startOffset, element.endOffset)
        document.insertString(element.startOffset, decrypt)
    }

    private fun getEnvironment(file: PsiFile?): Environment? {
        val fileName = file!!.virtualFile.name
        val envLetter = fileName.substring("application-".length, "application-".length + 1)
        return Environment.values().find { it.name == envLetter.uppercase() }
    }

    abstract fun execute(encryptor: StringEncryptor, text: String): String

    abstract fun isValidProperty(element: PsiElement): Boolean

    private fun findElement(file: PsiFile?, editor: Editor?): PsiElement? {
        if (file == null || editor == null) {
            return null
        }
        val offset: Int = editor.caretModel.offset
        return file.findElementAt(offset)
    }
}
