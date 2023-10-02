package com.sanyavertolet.kotlinjspreview.copier

import com.intellij.openapi.project.Project

/**
 * Interface responsible for copying project to temp dir
 */
interface ProjectCopier {
    /**
     * Copy [project] to temp dir
     *
     * @param project a [Project] to copy
     */
    fun copy(project: Project)
}
