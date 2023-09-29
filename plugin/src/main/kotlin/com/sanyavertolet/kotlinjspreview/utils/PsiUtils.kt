package com.sanyavertolet.kotlinjspreview.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.psi.KtProperty

fun PsiElement.isTopLevelPropertyAnnotatedWith(annotationShortName: String): Boolean {
    return this is KtProperty && isTopLevel && isAnnotatedWith(annotationShortName)
}

fun KtProperty.isAnnotatedWith(annotationShortName: String) = annotationEntries.any { it.shortName?.asString() == annotationShortName }

fun PsiElement.isParentTopLevelPropertyAnnotatedWith(
    annotationShortName: String,
): Boolean = parent?.isTopLevelPropertyAnnotatedWith(annotationShortName) == true

fun PsiElement.isValKeyword() = this is LeafPsiElement && text == "val"

fun PsiElement.isValOfPropertyAnnotatedWith(
    annotationShortName: String
) = isValKeyword() && isParentTopLevelPropertyAnnotatedWith(annotationShortName)

fun PsiElement.getIdentifier() = (this as KtProperty).kotlinFqName
