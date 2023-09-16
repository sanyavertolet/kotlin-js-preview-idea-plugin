package com.sanyavertolet.kotlinjspreview

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.search.searches.ReferencesSearch
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.jetbrains.kotlin.idea.configuration.GRADLE_SYSTEM_ID
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import java.io.File
import java.nio.file.NoSuchFileException
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.writeText


/**
 * @JsPreview
 * val Welcome = FC { }
 */

class BuildKotlinJsAction(private val psiElement: PsiElement? = null) : AnAction() {

    private val tempDirName = PROJECT_TEMP_DIR_NAME

    override fun actionPerformed(event: AnActionEvent) {
        psiElement ?: return
        val project = getEventProject(event) ?: return

        val (path, modifiedMainText) = replaceWrapper(project) ?: return
        runWriteAction { copyProjectToTempDir(project) }
        runWriteAction { saveToFile(getTempProjectPathByProjectPath(project, path), modifiedMainText) }
        runBuildTaskForTempProject(project)
        openBrowserWindow(project)
    }

    private fun openBrowserWindow(project: Project) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PreviewToolWindowFactory.ID)
        toolWindow?.activate(null)
    }

    private fun saveToFile(path: String, text: String) {
        Path(path).writeText(text)
    }

    private fun getTempProjectPathByProjectPath(project: Project, pathToFileInProject: String): String {
        val projectDir = project.guessProjectDir().orException { NO_PROJECT_DIR }
        return pathToFileInProject.replace(
            projectDir.path,
            "${projectDir.path}/$BUILD_DIR/$tempDirName",
        )
    }

    private fun replaceWrapper(project: Project): ModifiedFile? {
        psiElement ?: return null
        val usage = findWrapperUsage(project)?.resolve() ?: return null

        val previewComponentIdentifierString = getIdentifier() ?: return null

        val newParameter = JavaPsiFacade.getElementFactory(project).createIdentifier(
            previewComponentIdentifierString,
        )

        val sourceText = usage.parent.text

        usage.parent.findDescendantOfType<PsiElement> { it is PsiIdentifier }
            ?.replace(newParameter)

        return ModifiedFile(usage.containingFile.virtualFile.path, usage.parent.text, sourceText)
    }

    private fun copyProjectToTempDir(project: Project){
        val tempDir = createTempDir(project)
        val projectDir = project.guessProjectDir().orException { NO_PROJECT_DIR }
        copyFilesRecursively(projectDir, tempDir)
    }

    private fun copyFilesRecursively(source: VirtualFile, dist: VirtualFile) {
        val sourceFile = File(source.path)
        val distFile = File(dist.path)
        val dirFilter = FileFilterUtils.notFileFilter(
            FileFilterUtils.or(
                *copyIgnoreFileNames.map { FileFilterUtils.nameFileFilter(it) }.toTypedArray()
            )
        )
        runWriteAction { FileUtils.copyDirectory(sourceFile, distFile, dirFilter) }
    }

    private fun createTempDir(project: Project): VirtualFile {
        val projectBaseDir = project.guessProjectDir().orException { NO_PROJECT_DIR }

        val tempDirPath = "${projectBaseDir.path}/$BUILD_DIR/$tempDirName"
        val tempDir = LocalFileSystem.getInstance().refreshAndFindFileByPath(tempDirPath)

        return tempDir?.let { file ->
            if (file.exists()) {
                tempDir
            } else {
                projectBaseDir.createChildDirectoryIfNotCreated(BUILD_DIR).createChildDirectoryIfNotCreated(tempDirName)
            }
        } ?: throw NoSuchFileException("Could not create temp directory with path [${tempDirPath}]")
    }

    private fun getIdentifier() = (psiElement as KtProperty).name

    private fun findWrapperUsage(project: Project): PsiReference? {
        val wrapper = findWrapper(project) ?: return null
        val usages = ReferencesSearch.search(wrapper)
        return usages.findFirst()
    }

    private fun findWrapper(project: Project): PsiMethod? {
        val javaPsiFacade = JavaPsiFacade.getInstance(project)
        val annotationClass = javaPsiFacade.findClass(
            "com.sanyavertolet.kotlinjspreview.RootWrapper",
            GlobalSearchScope.allScope(project),
        ) ?: return null

        return AnnotatedElementsSearch.searchPsiMethods(
            annotationClass,
            GlobalSearchScope.allScope(project),
        ).findFirst()
    }

    private fun runBuildTaskForTempProject(project: Project) = ExternalSystemUtil.runTask(
        getSettings(project),
        DefaultRunExecutor.EXECUTOR_ID,
        project,
        GRADLE_SYSTEM_ID,
        JsPreviewNotifierCallback(project),
        ProgressExecutionMode.NO_PROGRESS_ASYNC,
        true,
    )

    private fun getSettings(project: Project) = ExternalSystemTaskExecutionSettings()
        .apply {
            val tempProjectPath = project.guessProjectDir()
                .orException { NO_PROJECT_DIR }
                .let { "${it.path}/$BUILD_DIR/$tempDirName" }
            externalProjectPath = tempProjectPath
            taskNames = Collections.singletonList("jsBrowserDistribution")
            externalSystemIdString = GRADLE_SYSTEM_ID.id
            vmOptions = ""
        }

    companion object {
        private val copyIgnoreFileNames = listOf(
            ".idea",
            ".gradle",
            ".run",
            "build",
            ".gitignore",
        )
    }
}

data class ModifiedFile(val path: String, val modifiedText: String, val sourceText: String)
