package com.sanyavertolet.kotlinjspreview

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.util.hasAnnotationWithShortName
import org.jetbrains.kotlin.psi.KtProperty

class JsPreviewLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return if (element is KtProperty &&
            element.isTopLevel && element.hasAnnotationWithShortName("JsPreview")
        ) {
            LineMarkerInfo(
                element,
                element.textRange,
                AllIcons.Actions.Execute,
                { "Run preview" },
                null,
                GutterIconRenderer.Alignment.CENTER,
                { "run-preview" },
            )
        } else {
            null
        }
    }
}
