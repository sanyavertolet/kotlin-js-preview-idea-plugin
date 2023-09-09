package com.sanyavertolet.kotlinjspreview

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.util.hasAnnotationWithShortName
import org.jetbrains.kotlin.psi.KtProperty

class JsPreviewRunLineMarkerContributor : RunLineMarkerContributor() {
    private fun PsiElement.isAnnotatedWith(annotationShortName: String) = this is KtProperty &&
        isTopLevel && hasAnnotationWithShortName(annotationShortName)

    override fun getInfo(element: PsiElement): Info? = if (element.isAnnotatedWith("JsPreview")) {
        Info(AllIcons.Actions.Execute, { "Run preview" }, BuildKotlinJsAction(element))
    } else {
        null
    }
}
