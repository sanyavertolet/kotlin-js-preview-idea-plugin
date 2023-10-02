package com.sanyavertolet.kotlinjspreview.substituror

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

/**
 * Interface responsible for changing the source code of temp project so that the desired component is rendered
 */
interface Substitutor {
    /**
     * Substitute an argument of type `FC` in root wrapper invocation
     * Note that root wrapper should be marked with [com.sanyavertolet.kotlinjspreview.RootWrapper] annotation.
     *
     * @param psiElement element to be put to function arguments
     * @param project current [Project]
     */
    fun substitute(psiElement: PsiElement, project: Project)
}
