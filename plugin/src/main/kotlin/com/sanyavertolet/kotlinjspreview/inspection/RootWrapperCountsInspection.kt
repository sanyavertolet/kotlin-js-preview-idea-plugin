package com.sanyavertolet.kotlinjspreview.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiFile
import com.sanyavertolet.kotlinjspreview.utils.getUsages
import org.jetbrains.kotlin.idea.codeinsight.api.classic.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class RootWrapperCountsInspection : AbstractKotlinInspection() {
    override fun checkFile(
        file: PsiFile,
        manager: InspectionManager,
        isOnTheFly: Boolean,
    ): Array<ProblemDescriptor> {
        val problemsHolder = ProblemsHolder(manager, file, isOnTheFly)
        file.collectDescendantsOfType<KtAnnotationEntry> { annotation ->
            annotation.shortName?.asString() == ROOT_WRAPPER_SHORT_NAME
        }
            .filter { it.getUsages(file.project).size > 1 }
            .map { annotation ->
                problemsHolder.registerProblem(
                    annotation,
                    "Several $ROOT_WRAPPER_SHORT_NAME annotation usages were found: there should be only one usage of $ROOT_WRAPPER_SHORT_NAME",
                    ProblemHighlightType.GENERIC_ERROR,
                )
            }
        return problemsHolder.resultsArray
    }

    companion object {
        private const val ROOT_WRAPPER_SHORT_NAME = "RootWrapper"
    }
}
