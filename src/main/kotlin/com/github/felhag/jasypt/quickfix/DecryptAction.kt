package com.github.felhag.jasypt.quickfix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.ulisesbocchio.jasyptspringboot.configuration.StringEncryptorBuilder
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties
import org.jetbrains.annotations.NotNull
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.core.env.StandardEnvironment
import org.springframework.core.io.UrlResource

class DecryptAction : BaseIntentionAction() {
    override fun getFamilyName(): String {
        return "Decrypt";
    }

    @NotNull
    override fun getText(): String {
        return "Decrypt"
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null) {
            return false
        }

        val element = this.findElement(file, editor)
        return element != null && element.text.startsWith("ENC(");
    }

    fun findElement(file: PsiFile, editor: Editor): PsiElement? {
        val offset: Int = editor.caretModel.offset
        return file.findElementAt(offset)
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
        properties.password = ""
        properties.algorithm = "PBEWithMD5AndDES"
        properties.ivGeneratorClassname = "org.jasypt.iv.NoIvGenerator"

        val encrypt = StringEncryptorBuilder(
            properties,
            "jasypt.encryptor"
        ).build()

        val decrypt = encrypt.decrypt(element.text.substring("ENC(".length, element.text.length - 1))
        editor.document.deleteString(element.startOffset, element.endOffset)
        editor.document.insertString(element.startOffset, decrypt)
    }
}
