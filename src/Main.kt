

import org.jsoup.*
import com.google.gson.*
import java.io.File

data class ErrorMessage(val message: String)
data class Data(val links: List<String>, val text: List<String>)
data class Table(val headings: List<String>, val data: Data)
data class Logo(val src: String, val details: String = "")
data class Snapshot(val summary: String, val logo: Logo, val table: Table)

// top languages for devs from StackOverflow 2019 survey
val languages = listOf<String>(
        "JavaScript",
        "HTML",
        "CSS",
        "SQL",
        "Java",
        "Bash",
        "Shell",
        "Powershell",
        "C++",
        "C",
        "Python",
        "C#",
        "PHP",
        "TypeScript",
        "Ruby",
        "Go",
        "Swift",
        "Kotlin",
        "R",
        "VBA",
        "Objective-C",
        "Assembly",
        "Scala",
        "Rust",
        "Dart",
        "Elixir",
        "Clojure",
        "WebAssembly"
)

fun generateSnapshot(language: String): String {
    try {
        val snapshot = Jsoup
                .connect("https://en.wikipedia.org/wiki/$language _(programming_language)")
                .get()
                .select(".infobox.vevent")
                .map { el ->
                    val summary = el.getElementsByClass("summary").text()
                    val headings = el.getElementsByTag("tbody").select("tr th").eachText()
                    val td = el.getElementsByTag("tbody").select("tr td")
                    val links = td.select("a[href]").eachAttr("href")
                    val text = td.eachText()
                    val details = el.getElementsByTag("tbody").select("tr td")
                    val logoSrc = details
                            .select(".image")
                            .first()
                            .select("img")
                            .first()
                            .attr("src")
                    val logoDetails = details.select(".image").next().text()

                    Snapshot(summary, Logo(logoSrc, logoDetails), Table(headings, Data(links, text)))

                }
        return GsonBuilder().setPrettyPrinting().create().toJson(snapshot.first())
    } catch (e: HttpStatusException) {
        return GsonBuilder().create().toJson(ErrorMessage(e.message!!))
    }
    catch (e: NoSuchElementException) {
        return GsonBuilder().create().toJson(ErrorMessage(e.message!!))
    }
}

fun main() {
    for(language in languages) {
        File("./out/$language.json").writeText(generateSnapshot(language))
    }
}
