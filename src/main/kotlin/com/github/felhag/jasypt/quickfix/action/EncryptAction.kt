package com.github.felhag.jasypt.quickfix.action

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.jasypt.encryption.StringEncryptor
import org.jetbrains.yaml.YAMLElementTypes.SCALAR_PLAIN_VALUE

class EncryptAction : AbstractJasyptAction("Encrypt")  {
    override fun isValidProperty(element: PsiElement): Boolean {
        return element.parent.elementType == SCALAR_PLAIN_VALUE && !element.text.startsWith(PREFIX)
    }

    override fun execute(encryptor: StringEncryptor, text: String): String {
        return "$PREFIX${encryptor.encrypt(text)}$SUFFIX"
    }
}
