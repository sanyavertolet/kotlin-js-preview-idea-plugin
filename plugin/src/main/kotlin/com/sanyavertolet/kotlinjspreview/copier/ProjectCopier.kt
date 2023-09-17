package com.sanyavertolet.kotlinjspreview.copier

import com.intellij.openapi.project.Project

interface ProjectCopier {
    fun copy(project: Project)
}
