package ru.tbank.education.school.homework
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.InvalidPathException
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException
import java.nio.file.StandardOpenOption
/**
 * Интерфейс для подсчёта строк и слов в файле.
 */
interface FileAnalyzer {

    /**
     * Считает количество строк и слов в указанном входном файле и записывает результат в выходной файл.
     *
     * Словом считается последовательность символов, разделённая пробелами,
     * табуляциями или знаками перевода строки. Пустые части после разделения не считаются словами.
     *
     * @param inputFilePath путь к входному текстовому файлу
     * @param outputFilePath путь к выходному файлу, в который будет записан результат
     * @return true если операция успешна, иначе false
     */
    fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean
}


class IOFileAnalyzer : FileAnalyzer {
    override fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean {
        return try {
            val reader = BufferedReader(FileReader(inputFilePath))
            var lineCount = 0
            var wordCount = 0

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                lineCount++
                wordCount += countWordsInLine(line!!)
            }
            reader.close()
            val writer = FileWriter(outputFilePath)
            writer.write("Lines: $lineCount\nWords: $wordCount")
            writer.close()
            true
        } catch (e: InvalidPathException) {
            println("Ошибка: Некорректный путь к файлу - ${e.message}")
            false
        } catch (e: IOException) {
            println("Ошибка ввода-вывода: ${e.message}")
            false
        } catch (e: Exception) {
            println("Неожиданная ошибка: ${e.message}")
            false
        }
    }

    private fun countWordsInLine(line: String): Int {
        if (line.isBlank()) return 0
        return line.trim().split(Regex("\\s+")).count { it.isNotBlank() }
    }
}


class  NIOFileAnalyzer : FileAnalyzer {
    override fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean {
        return try {
            val lines = Files.readAllLines(Paths.get(inputFilePath))
            var lineCount = 0
            var wordCount = 0
            for (line in lines) {
                lineCount++
                wordCount += countWordsInLine(line)
            }
            val result = "Lines: $lineCount\nWords: $wordCount"
            Files.write(
                Paths.get(outputFilePath),
                result.toByteArray(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            )
            true
        } catch (e: InvalidPathException) {
            println("Ошибка: Некорректный путь к файлу - ${e.message}")
            false
        } catch (e: IOException) {
            println("Ошибка ввода-вывода: ${e.message}")
            false
        } catch (e: Exception) {
            println("Неожиданная ошибка: ${e.message}")
            false
        }
    }
        private fun countWordsInLine(line: String): Int {
            if (line.isBlank()) return 0
            return line.trim().split(Regex("\\s+")).count { it.isNotBlank() }
        }
}
fun main() {
        val ioAnalyzer = IOFileAnalyzer()
        val nioAnalyzer = NIOFileAnalyzer()

        val inputFile = "C:\\Users\\Admin\\OneDrive\\Документы\\буткемп\\ляля.txt"
        val outputFileIO = "C:\\Users\\Admin\\OneDrive\\Документы\\буткемп\\ляля1.txt"
        val outputFileNIO = "C:\\Users\\Admin\\OneDrive\\Документы\\буткемп\\ляля_nio.txt"

        println("Тестирование IO реализации:")
        val ioSuccess = ioAnalyzer.countLinesAndWordsInFile(inputFile, outputFileIO)

        if (ioSuccess) {
            println("IO анализ завершен успешно!")
        } else {
            println("IO анализ завершен с ошибкой!")
        }
        println("\nТестирование NIO реализации:")
        val nioSuccess = nioAnalyzer.countLinesAndWordsInFile(inputFile, outputFileNIO)

        if (nioSuccess) {
            println("NIO анализ завершен успешно!")
        } else {
            println("NIO анализ завершен с ошибкой!")
        }
}

