package io.github.manamiproject.modb.core.extractor

import io.github.manamiproject.modb.core.extensions.EMPTY
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import kotlin.reflect.full.isSubclassOf


public object XmlDataExtractor : DataExtractor {

    override suspend fun extract(rawContent: String, selection: Map<OutputKey, Selector>): ExtractionResult {
        val document = Jsoup.parse(rawContent)

        val result: Map<OutputKey, Any> = selection.map { transformXpathToTriple(it) }
            .map { elementSelection(document.selectXpath(it.second), it) }
            .associate { toOutput(it) }

        return ExtractionResult(result)
    }

    private fun toOutput(item: Pair<OutputKey, Any>) : Pair<OutputKey, Any> {
        if (item.second::class.isSubclassOf(Iterable::class)) {
            val castedList = (item.second as Iterable<*>).toList()

            return if (castedList.size == 1) {
                item.first to castedList.first()!!
            } else {
                item.first to castedList
            }
        } else {
            return item
        }
    }

    private fun transformXpathToTriple(entry: Map.Entry<OutputKey, Selector>): Triple<OutputKey, Selector, String> {
        val split = entry.value.split('/')
        val allExceptLast = split.subList(0, split.size-1).joinToString("/")

        return when {
            split.last().startsWith("@") -> Triple(entry.key, allExceptLast, split.last().trimStart('@'))
            split.last().endsWith("text()") -> Triple(entry.key, allExceptLast, split.last())
            else -> Triple(entry.key, entry.value, EMPTY)
        }
    }

    private fun elementSelection(jsoupElements: Elements, entry: Triple<OutputKey, Selector, String>): Pair<OutputKey, Any> {
        val value =  when {
            jsoupElements.isEmpty() -> NotFound
            entry.third == "text()" -> jsoupElements.eachText() ?: NotFound
            entry.third == EMPTY -> jsoupElements.textNodes().map { it.text().trim() }.filter { it.isNotBlank() }
            else -> jsoupElements.eachAttr(entry.third) ?: NotFound
        }

        return entry.first to value
    }
}