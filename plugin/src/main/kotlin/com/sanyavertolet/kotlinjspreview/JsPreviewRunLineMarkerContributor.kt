package com.sanyavertolet.kotlinjspreview

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.util.hasAnnotationWithShortName
import org.jetbrains.kotlin.psi.KtProperty

class JsPreviewRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? = if (element is KtProperty &&
        element.isTopLevel && element.hasAnnotationWithShortName("JsPreview")
    ) {
        Info(
            AllIcons.Actions.Execute,
            null,
            BuildKotlinJsAction(),
        )
    } else {
        null
    }
}
