package com.sanyavertolet.kotlinjspreview.substituror

import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem

import com.intellij.psi.search.searches.ReferencesSearch
import com.sanyavertolet.kotlinjspreview.utils.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.utils.getIdentifier
import com.sanyavertolet.kotlinjspreview.utils.getPathOrException
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import java.io.File
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiDocumentManager
import com.sanyavertolet.kotlinjspreview.utils.JsPreviewException
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.nio.file.NoSuchFileException

class AstSubstitutor: Substitutor {
    private val fileSystem = LocalFileSystem.getInstance()
    override fun substitute(psiElement: PsiElement, project: Project) = replaceWrapper(psiElement, project)

    private fun findWrapper(module: Module, project: Project): KtNamedFunction? = KotlinAnnotationsIndex.get(
            "RootWrapper",
            project,
            module.moduleWithLibrariesScope,
        )
            .first()
            .context
            ?.parent
            ?.takeIf { it is KtNamedFunction } as KtNamedFunction?

    private fun findWrapperUsage(module: Module, project: Project): PsiReference? {
        val wrapper = findWrapper(module, project) ?: throw JsPreviewException(
            "Could not find wrapper. Make sure it is annotated with @com.sanyavertolet.kotlinjspreview.RootWrapper annotation."
        )
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
        val module = psiElement.module ?: error("Could not get module of psiElement [$psiElement]")
        val usage = findWrapperUsage(module, project)?.element ?: throw JsPreviewException(
            "Could not find wrapper usages. Make sure it is annotated with @com.sanyavertolet.kotlinjspreview.RootWrapper annotation."
        )

        val pathToTempFile = getPathToFileInTempProject(usage, project)

        val virtualFile = fileSystem.refreshAndFindFileByIoFile(File(pathToTempFile))
            ?: throw NoSuchFileException("Could not find virtual file with path [$pathToTempFile].")
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            ?: throw NoSuchFileException("Could not find file by virtual file [$virtualFile].")

        val previewComponentIdentifierString = psiElement.getIdentifier()
            ?.shortName()
            ?.asString()
            ?: throw JsPreviewException("Could not get preview component identifier.")

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
