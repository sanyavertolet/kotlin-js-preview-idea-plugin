package com.sanyavertolet.kotlinjspreview

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.sanyavertolet.kotlinjspreview.utils.isValOfPropertyAnnotatedWith

class JsPreviewRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? = if (element.isValOfPropertyAnnotatedWith(PREVIEW_ANNOTATION)) {
        Info(AllIcons.Actions.Execute, { "Run preview" }, BuildKotlinJsAction(element.parent))
    } else {
        null
    }

    companion object {
        private const val PREVIEW_ANNOTATION = "JsPreview"
    }
}
