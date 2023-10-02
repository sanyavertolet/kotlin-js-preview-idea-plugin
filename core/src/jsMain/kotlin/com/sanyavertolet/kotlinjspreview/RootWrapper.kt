package com.sanyavertolet.kotlinjspreview

/**
 * Annotation that should mark root wrappers.
 *
 * Example of root wrapper:
 * ```Kotlin
 * @RootWrapper
 * fun wrapper(fc: FC<*>) {
 *     val mainDiv = document.getElementById("wrapper")!!
 *     createRoot(mainDiv).render(
 *         fc.create(),
 *     )
 * }
 *
 * // Here is how you should use the wrapper
 * fun main() = wrapper(default)
 * ```
 */
@Suppress("unused")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class RootWrapper
