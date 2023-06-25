package com.github.felhag.jasypt.quickfix.action

import com.intellij.psi.PsiElement
import org.jasypt.encryption.StringEncryptor

class DecryptAction : AbstractJasyptAction("Decrypt") {
    override fun isValidProperty(element: PsiElement): Boolean {
        return element.text.startsWith(PREFIX)
    }

    override fun execute(encryptor: StringEncryptor, text: String): String {
        return encryptor.decrypt(text.substring(PREFIX.length, text.length - SUFFIX.length))
    }
}
