package com.sanyavertolet.kotlinjspreview.builder

import com.intellij.openapi.project.Project

/**
 * Interface responsible for project build
 */
interface Builder {
    /**
     * Build a project
     *
     * @param project [Project] to build
     */
    fun build(project: Project)
}
