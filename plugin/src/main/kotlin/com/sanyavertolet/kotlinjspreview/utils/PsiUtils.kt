/**
 * File containing utils for `Psi`s
 */
package com.sanyavertolet.kotlinjspreview.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.psi.KtProperty

/**
 * @param annotationShortName short name of annotation
 * @return `true` if [this] is a top-level property, annotated with annotation with [annotationShortName], `false` otherwise
 */
fun PsiElement.isTopLevelPropertyAnnotatedWith(annotationShortName: String): Boolean {
    return this is KtProperty && isTopLevel && isAnnotatedWith(annotationShortName)
}

/**
 * @param annotationShortName short name of annotation
 * @return `true` if [this] [KtProperty] is annotated with annotation with [annotationShortName], `false` otherwise
 */
fun KtProperty.isAnnotatedWith(annotationShortName: String) = annotationEntries.any { it.shortName?.asString() == annotationShortName }

/**
 * @param annotationShortName short name of annotation
 * @return `true` if [this] is its parent is annotated with annotation with [annotationShortName], `false` otherwise
 */
fun PsiElement.isParentTopLevelPropertyAnnotatedWith(
    annotationShortName: String,
): Boolean = parent?.isTopLevelPropertyAnnotatedWith(annotationShortName) == true

/**
 * @return `true` if [this] is [LeafPsiElement] and its text is `val`, `false` otherwise
 */
fun PsiElement.isValKeyword() = this is LeafPsiElement && text == "val"

/**
 * @param annotationShortName short name of annotation
 * @return `true` if [this] is a `val` keyword, and it's parent is annotated with annotation with [annotationShortName], `false` otherwise
 */
fun PsiElement.isValOfPropertyAnnotatedWith(
    annotationShortName: String,
) = isValKeyword() && isParentTopLevelPropertyAnnotatedWith(annotationShortName)

/**
 * @return fully-qualified name of [this] as [KtProperty]
 */
fun PsiElement.getIdentifier() = (this as KtProperty).kotlinFqName
