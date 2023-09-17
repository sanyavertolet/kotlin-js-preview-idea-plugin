package com.sanyavertolet.kotlinjspreview.substituror

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.search.searches.ReferencesSearch
import com.sanyavertolet.kotlinjspreview.getIdentifier
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

class AstSubstitutor: Substitutor {
    override fun substitute(psiElement: PsiElement, project: Project) = runWriteAction {
        replaceWrapper(psiElement, project)
    }

    private fun findWrapper(project: Project): PsiMethod? {
        val javaPsiFacade = JavaPsiFacade.getInstance(project)
        val annotationClass = javaPsiFacade.findClass(
            "com.sanyavertolet.kotlinjspreview.RootWrapper",
            GlobalSearchScope.allScope(project),
        ) ?: return null

        return AnnotatedElementsSearch.searchPsiMethods(
            annotationClass,
            GlobalSearchScope.allScope(project),
        ).findFirst()
    }

    private fun findWrapperUsage(project: Project): PsiReference? {
        val wrapper = findWrapper(project) ?: return null
        val usages = ReferencesSearch.search(wrapper)
        return usages.findFirst()
    }

    // TODO: fix replace so that it would replace only in temp project
    private fun replaceWrapper(psiElement: PsiElement, project: Project) {
        val usage = findWrapperUsage(project)?.element ?: return

        val previewComponentIdentifierString = psiElement.getIdentifier()?.asString() ?: return

        val newParameter = JavaPsiFacade.getElementFactory(project).createIdentifier(
            previewComponentIdentifierString,
        )

        usage.parent.findDescendantOfType<KtValueArgumentList>()?.firstChild?.nextSibling
            ?.replace(newParameter)

        return
    }
}
