package ru.tbank.education.school.lesson10.homework
//import ru.tbank.education.school.lesson10.homework.FullDocumentedClassTest.User
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.memberFunctions
object DocumentationGenerator {
    fun generateDoc(obj: Any): String {
        val userClass = obj::class
        if (userClass.hasAnnotation<InternalApi>()) {
            return "Документация скрыта (InternalApi)."
        }
        val classAnnotation = userClass.findAnnotation<DocClass>()
        if (classAnnotation==null) {
            return "Нет документации для класса."
        }
        var result = ""
        result += "=== Документация: ${userClass.simpleName} ===\n"

        if (classAnnotation != null) {
            result += "Описание: ${classAnnotation.description}\n"
            result += "Автор: ${classAnnotation.author}\n"
            result += "Версия: ${classAnnotation.version}\n"
        }
        val properties = userClass.memberProperties
        val docProperties = properties
            .filter { !it.hasAnnotation<InternalApi>() }
        if (docProperties.isNotEmpty()) {
            result += "--- Свойства ---\n"
            docProperties.forEach { property ->
                val annotation = property.findAnnotation<DocProperty>()
                val description = annotation?.description ?: "Нет описания"
                val example = annotation?.example ?: ""
                result += "-${property.name}\n"
                result += "  Описание: ${description}\n"
                if (example.isNotEmpty()) {
                    result += "  Пример: ${example}\n"
                }
            }
        }
        val methods = userClass.memberFunctions
        val objectMethods = setOf("equals", "hashCode", "toString", "clone", "finalize",
            "getClass", "notify", "notifyAll", "wait")
        val dataClassMethods = setOf("equals", "hashCode", "toString", "copy") +
                (1..10).map { "component$it" }
        val docMethods = methods
            .filter { !it.hasAnnotation<InternalApi>() }
            .filter { !objectMethods.contains(it.name) }
            .filter {
                if (userClass.isData) {
                    !dataClassMethods.contains(it.name)&& it.findAnnotation<DocMethod>() != null
                } else {
                    true
                }
            }
        if (docMethods.isNotEmpty()) {
            result += "\n--- Методы ---\n"
            docMethods.forEach { method ->
                var annotation = method.findAnnotation<DocMethod>()
                val description = annotation?.description ?: "Нет описания"
                val returns = annotation?.returns ?: "Нет описания"
                val params = method.parameters
                    .filter { it.name != "this" && it.name != null }
                    .map { param ->
                        val paramName = param.name ?: "unknown"
                        val typeName = when {
                            param.type.toString().contains("String") -> "String"
                            param.type.toString().contains("Int") -> "Int"
                            param.type.toString().contains("Boolean") -> "Boolean"
                            else -> param.type.toString().split(".").last()
                        }
                        "$paramName: $typeName"
                    }

                val paramsString = params.joinToString(", ")
                result += "- ${method.name}($paramsString)\n"
                result += "  Описание: ${description}\n"
                val paramsWithAnnotations = method.parameters
                    .filter { it.name != "this" && it.name != null }
                if (paramsWithAnnotations.isNotEmpty()) {
                    result += "  Параметры:\n"
                    paramsWithAnnotations.forEach { param ->
                        val paramAnnotation = param.findAnnotation<DocParam>()
                        val paramDescription = paramAnnotation?.description ?: "Нет описания"
                        result += "    ${param.name}: ${paramDescription}\n"
                        }
                    }
                    if (returns.isNotEmpty() ) {
                        result += "  Возвращает: ${returns}\n"
                }

            }
        }
        return result
    }
}
