package com.sanyavertolet.kotlinjspreview.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.nj2k.types.typeFqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * [LocalInspectionTool] that checks that the first argument of method, annotated with [com.sanyavertolet.kotlinjspreview.RootWrapper]
 * has a `react.FC` as the first argument
 */
class RootWrapperFirstArgumentInspection : LocalInspectionTool() {
    @Suppress("UnsafeCallOnNullableType")
    override fun checkFile(
        file: PsiFile,
        manager: InspectionManager,
        isOnTheFly: Boolean,
    ): Array<ProblemDescriptor> {
        val problemsHolder = ProblemsHolder(manager, file, isOnTheFly)
        file.collectDescendantsOfType<KtNamedFunction> { function ->
            function.annotationEntries.any { it.shortName?.asString() == ROOT_WRAPPER_SHORT_NAME }
        }
            .filter { it.valueParameters.firstOrNull()?.typeFqName()?.asString() != FC_FQ_NAME }
            .map { function ->
                problemsHolder.registerProblem(
                    function.annotationEntries.find { it.shortName?.asString() == ROOT_WRAPPER_SHORT_NAME }!!,
                    "Incorrect root wrapper: first argument should have `$FC_FQ_NAME` type",
                    ProblemHighlightType.GENERIC_ERROR,
                )
            }
        return problemsHolder.resultsArray
    }

    companion object {
        private const val ROOT_WRAPPER_SHORT_NAME = "RootWrapper"
        private const val FC_FQ_NAME = "react.FC"
    }
}
