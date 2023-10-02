package com.sanyavertolet.kotlinjspreview

/**
 * Annotation that should mark top-level properties of type FC to be previewed
 *
 * Example of use:
 * ```Kotlin
 * @JsPreview
 * val Welcome = FC {
 *     var name by useState("sanyavertolet")
 *     div {
 *         +"Hello, $name"
 *     }
 *     input {
 *         type = InputType.text
 *         value = name
 *         onChange = { event ->
 *             name = event.target.value
 *         }
 *     }
 * }
 * ```
 */
@Suppress("unused")
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class JsPreview
