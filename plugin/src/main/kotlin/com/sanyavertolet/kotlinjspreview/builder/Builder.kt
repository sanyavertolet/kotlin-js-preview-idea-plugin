package com.sanyavertolet.kotlinjspreview.builder

import com.intellij.openapi.project.Project

interface Builder {
    fun build(project: Project)
}
