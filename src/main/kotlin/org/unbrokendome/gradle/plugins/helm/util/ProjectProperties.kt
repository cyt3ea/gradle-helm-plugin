package org.unbrokendome.gradle.plugins.helm.util

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider


/**
 * Returns a [Provider] that provides the given project property if it is available, using `toString` to convert
 * it to a string.
 *
 * The project property is resolved as per [Project.property].
 *
 * @receiver the Gradle [Project]
 * @param propertyName the name of the property
 * @param evaluateGString if `true`, the string is evaluated as if it was a Groovy GString, with the [Project]
 *        as evaluation context
 * @return a [Provider] that returns the project property value if it exists, or is empty if the property does
 *         not exist
 */
internal fun Project.providerFromProjectProperty(propertyName: String, evaluateGString: Boolean = false): Provider<String> {
    val provider = provider<String> {
        project.findProperty(propertyName)?.toString()
    }
    return if (evaluateGString) {
        provider.asGString(this)
    } else {
        provider
    }
}


/**
 * Returns a [Provider] that provides the given project property if it is available, interpreting its value
 * as a boolean.
 *
 * The project property is resolved as per [Project.property].
 *
 * If the type of the property value is [Boolean], it is returned by the provider as-is; if it is a [String], the
 * provider returns `true` if it has the value `"true"` or `false` if it has any other value. The provider will throw
 * an exception if the property is not a `String` or `Boolean`.
 *
 * @receiver the Gradle [Project]
 * @param propertyName the name of the property
 * @return a [Provider] that returns the project property value if it exists, or is empty if the property does
 *         not exist
 */
fun Project.booleanProviderFromProjectProperty(propertyName: String): Provider<Boolean> =
        provider {
            project.findProperty(propertyName)?.let { value ->
                when (value) {
                    is Boolean -> value
                    is String -> value.toBoolean()
                    else -> throw IllegalArgumentException("Value cannot be converted to a Boolean: $value")
                }
            }
        }


/**
 * Returns a [Provider] that provides the given project property if it is available, interpreting as the path of
 * a directory.
 *
 * The project property is resolved as per [Project.property].
 *
 * If the property contains a relative path, it is resolved from the project directory.
 *
 * @receiver the Gradle [Project]
 * @param propertyName the name of the property
 * @param evaluateGString if `true`, the string is evaluated as if it was a Groovy GString, with the [Project]
 *        as evaluation context
 * @return a [Provider] that returns the project property value as a [Directory] if it exists, or is empty if the
 *         property does not exist
 */
fun Project.dirProviderFromProjectProperty(propertyName: String,
                                           evaluateGString: Boolean = false): Provider<Directory> =
        providerFromProjectProperty(propertyName, evaluateGString)
                .let { pathProvider ->
                    project.layout.projectDirectory.dir(pathProvider)
                }


/**
 * Returns a [Provider] that provides the given project property if it is available, interpreting as the path of
 * a file.
 *
 * The project property is resolved as per [Project.property].
 *
 * If the property contains a relative path, it is resolved from the project directory.
 *
 * @receiver the Gradle [Project]
 * @param propertyName the name of the property
 * @param evaluateGString if `true`, the string is evaluated as if it was a Groovy GString, with the [Project]
 *        as evaluation context
 * @return a [Provider] that returns the project property value as a [RegularFile] if it exists, or is empty if the
 *         property does not exist
 */
fun Project.fileProviderFromProjectProperty(propertyName: String,
                                            evaluateGString: Boolean = false): Provider<RegularFile> =
        providerFromProjectProperty(propertyName, evaluateGString)
                .let { pathProvider ->
                    project.layout.projectDirectory.file(pathProvider)
                }
