package com.sanyavertolet.kotlinjspreview.substituror

import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.search.searches.ReferencesSearch
import com.sanyavertolet.kotlinjspreview.utils.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.utils.getIdentifier
import com.sanyavertolet.kotlinjspreview.utils.getPathOrException
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import java.io.File

class AstSubstitutor: Substitutor {
    override fun substitute(psiElement: PsiElement, project: Project) = replaceWrapper(psiElement, project)

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

    private fun getPathToFileInTempProject(psiElement: PsiElement, project: Project): String {
        val pathToFile = psiElement.containingFile.virtualFile.path
        val pathToProject = project.getPathOrException().path
        val config = PluginConfig.getInstance()
        return pathToFile.replace(pathToProject, "$pathToProject/$BUILD_DIR/${config.tempProjectDirName}")
    }

    private fun replaceWrapper(psiElement: PsiElement, project: Project) {
        val usage = findWrapperUsage(project)?.element ?: return

        val pathToTempFile = getPathToFileInTempProject(usage, project)

        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(File(pathToTempFile)) ?: return
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return

        val previewComponentIdentifierString = psiElement.getIdentifier()?.shortName()?.asString() ?: return

        val newParameter = JavaPsiFacade.getElementFactory(project).createIdentifier(
            previewComponentIdentifierString,
        )

        PsiDocumentManager.getInstance(project).commitAllDocuments()

        runUndoTransparentWriteAction {
            psiFile.findDescendantOfType<KtFunction> { it.name == "main" }
                ?.findDescendantOfType<KtValueArgumentList>()
                ?.firstChild
                ?.nextSibling
                ?.replace(newParameter)
        }

        return
    }
}
