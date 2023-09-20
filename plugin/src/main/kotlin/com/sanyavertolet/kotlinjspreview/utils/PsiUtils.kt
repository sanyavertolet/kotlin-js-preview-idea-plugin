package com.sanyavertolet.kotlinjspreview.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.base.utils.fqname.getKotlinFqName
import org.jetbrains.kotlin.idea.util.hasAnnotationWithShortName
import org.jetbrains.kotlin.psi.KtProperty

fun PsiElement.isTopLevelPropertyAnnotatedWith(annotationShortName: String): Boolean {
    return this is KtProperty && isTopLevel && hasAnnotationWithShortName(annotationShortName)
}

fun PsiElement.isParentTopLevelPropertyAnnotatedWith(
    annotationShortName: String,
): Boolean = parent?.isTopLevelPropertyAnnotatedWith(annotationShortName) == true

fun PsiElement.isValKeyword() = this is LeafPsiElement && text == "val"

fun PsiElement.isValOfPropertyAnnotatedWith(
    annotationShortName: String
) = isValKeyword() && isParentTopLevelPropertyAnnotatedWith(annotationShortName)

fun PsiElement.getIdentifier() = (this as KtProperty).getKotlinFqName()
