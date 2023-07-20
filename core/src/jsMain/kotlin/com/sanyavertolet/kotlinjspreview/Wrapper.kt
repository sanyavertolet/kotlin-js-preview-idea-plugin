package com.sanyavertolet.kotlinjspreview

import react.FC
import react.create
import react.dom.client.createRoot
import web.dom.document
import web.html.HTMLDivElement

fun wrapper(fc: FC<*>) {
    // Might need some external dependencies e.g. bootstrap and so on

    val mainDiv = document.getElementById("wrapper") as HTMLDivElement
    createRoot(mainDiv).render(fc.create())
}