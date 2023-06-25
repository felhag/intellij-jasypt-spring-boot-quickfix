package com.github.felhag.jasypt.quickfix.action

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

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor == null || file == null) {
            return
        }

        val element = this.findElement(file, editor)
        if (element == null) {
            return
        }

        // Force to use intellijs classloader
        Thread.currentThread().contextClassLoader = this.javaClass.classLoader

        val properties = JasyptEncryptorConfigurationProperties()
        properties.password = Settings.get().get()
        properties.algorithm = "PBEWithMD5AndDES"
        properties.ivGeneratorClassname = "org.jasypt.iv.NoIvGenerator"

        val builder = StringEncryptorBuilder(
            properties,
            "jasypt.encryptor"
        ).build()

        val decrypt = execute(builder, element.text)
        editor.document.deleteString(element.startOffset, element.endOffset)
        editor.document.insertString(element.startOffset, decrypt)
    }

    abstract fun execute(encryptor: StringEncryptor, text: String): String;

    protected fun findElement(file: PsiFile, editor: Editor): PsiElement? {
        val offset: Int = editor.caretModel.offset
        return file.findElementAt(offset)
    }

}
