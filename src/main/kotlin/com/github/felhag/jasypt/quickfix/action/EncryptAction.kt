package com.github.felhag.jasypt.quickfix.action

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import org.jasypt.encryption.StringEncryptor
import org.jetbrains.yaml.YAMLElementTypes.SCALAR_PLAIN_VALUE

class EncryptAction : AbstractJasyptAction("Encrypt")  {
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null) {
            return false
        }

        return isValue(this.findElement(file, editor))
    }

    private fun isValue(element: PsiElement?): Boolean {
        if (element == null) {
            return false;
        }

        return element.parent.elementType == SCALAR_PLAIN_VALUE && !element.text.startsWith(PREFIX)
    }

    override fun execute(encryptor: StringEncryptor, text: String): String {
        return "$PREFIX${encryptor.encrypt(text)}$SUFFIX"
    }
}
