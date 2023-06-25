package com.github.felhag.jasypt.quickfix.action

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jasypt.encryption.StringEncryptor

class DecryptAction : AbstractJasyptAction("Decrypt") {
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null) {
            return false
        }

        val element = this.findElement(file, editor)
        return element != null && element.text.startsWith("ENC(");
    }

    override fun execute(encryptor: StringEncryptor, text: String): String {
        return encryptor.decrypt(text.substring(PREFIX.length, text.length - SUFFIX.length))
    }
}
