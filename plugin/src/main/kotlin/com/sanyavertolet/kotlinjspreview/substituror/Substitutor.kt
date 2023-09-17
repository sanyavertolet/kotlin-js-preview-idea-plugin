package com.sanyavertolet.kotlinjspreview.substituror

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

interface Substitutor {
    fun substitute(psiElement: PsiElement, project: Project)
}
