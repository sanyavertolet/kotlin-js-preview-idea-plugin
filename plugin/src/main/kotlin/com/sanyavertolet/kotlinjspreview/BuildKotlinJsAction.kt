package com.sanyavertolet.kotlinjspreview

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.sanyavertolet.kotlinjspreview.builder.GradleBuilder
import com.sanyavertolet.kotlinjspreview.copier.RecursiveProjectCopier
import com.sanyavertolet.kotlinjspreview.substituror.AstSubstitutor
import org.jetbrains.kotlin.idea.util.application.runReadAction

/**
 * @JsPreview
 * val Welcome = FC { }
 */

class BuildKotlinJsAction(
    private val psiElement: PsiElement? = null,
) : AnAction() {
    private val projectCopier = RecursiveProjectCopier()
    private val builder = GradleBuilder()
    private val substitutor = AstSubstitutor()

    override fun actionPerformed(event: AnActionEvent) {
        psiElement ?: return
        val project = getEventProject(event) ?: return

        projectCopier.copy(project)
        substitutor.substitute(psiElement, project)
        builder.build(project)

        openBrowserWindow(project)
    }

    private fun openBrowserWindow(project: Project) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PreviewToolWindowFactory.ID)
        runReadAction { toolWindow?.activate(null) }
    }
}
