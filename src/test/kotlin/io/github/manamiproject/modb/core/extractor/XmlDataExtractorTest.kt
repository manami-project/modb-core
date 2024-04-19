package io.github.manamiproject.modb.core.extractor

import io.github.manamiproject.modb.test.loadTestResource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class XmlDataExtractorTest {

    @Test
    fun `correctly extract data various cases`() {
        runBlocking {
            // when
            val result = XmlDataExtractor.extract(
                rawContent = loadTestResource("xml_data_extractor_tests/example.html"),
                selection = mapOf(
                    "title" to "//meta[@property='og:title']/@content",
                    "episodes" to "//td[contains(text(), 'Episodes')]/following-sibling::td/a/text()",
                    "source" to "//meta[@property='og:url']/@content",
                    "status" to "//td[contains(text(), 'Status')]/following-sibling::*/text()",
                    "tags" to "//span[@itemprop='genre']/text()",
                    "type" to "//td[contains(text(), 'Type')]/following-sibling::*/text()",
                    "duration" to "//td[contains(text(), 'Duration')]/following-sibling::*/text()",
                    "premiered" to "//td[contains(text(), 'Premiered')]/following-sibling::*/text()",
                    "aired" to "//td[contains(text(), 'Aired')]/following-sibling::*/text()",
                    "picture" to "//div[contains(@class, 'status-block')]/div[@itemprop='image']/@content",
                    "relatedAnime" to "//div[@id='related-manga']/table/tbody//a[contains(@href, 'https://myanimelist.net/anime/')]/@href"
                )
            )

            // then
            assertThat(result.string("title")).isEqualTo("Rurouni Kenshin: Meiji Kenkaku Romantan")
            assertThat(result.int("episodes")).isEqualTo(94)
            assertThat(result.string("source")).isEqualTo("https://myanimelist.net/anime/45/Rurouni_Kenshin__Meiji_Kenkaku_Romantan")
            assertThat(result.string("status")).isEqualTo("Finished Airing")
            assertThat(result.string("type")).isEqualTo("TV")
            assertThat(result.string("duration")).isEqualTo("25 min. per ep.")
            assertThat(result.string("premiered")).isEqualTo("Winter 1996")
            assertThat(result.string("aired")).isEqualTo("Jan 10, 1996 to Sep 8, 1998")
            assertThat(result.string("picture")).isEqualTo("https://cdn.myanimelist.net/images/anime/1346/119505.jpg")
            assertThat(result.listNotNull<String>("tags")).containsExactlyInAnyOrder(
                "Action",
                "Adventure",
                "Comedy",
                "Romance",
                "Historical",
                "Samurai",
                "Shounen"
            )
            assertThat(result.listNotNull<String>("relatedAnime")).containsExactlyInAnyOrder(
                "https://myanimelist.net/anime/401/Rurouni_Kenshin__Meiji_Kenkaku_Romantan_-_Seisou-hen",
                "https://myanimelist.net/anime/44/Rurouni_Kenshin__Meiji_Kenkaku_Romantan_-_Tsuioku-hen",
                "https://myanimelist.net/anime/6591/Rurouni_Kenshin_DVD-BOX_Special_Ending",
                "https://myanimelist.net/anime/11441/Rurouni_Kenshin__Meiji_Kenkaku_Romantan_-_Shin_Kyoto-hen",
                "https://myanimelist.net/anime/50613/Rurouni_Kenshin__Meiji_Kenkaku_Romantan_2023",
                "https://myanimelist.net/anime/46/Rurouni_Kenshin__Meiji_Kenkaku_Romantan_-_Ishinshishi_e_no_Chinkonka",
                "https://myanimelist.net/anime/4664/Rurouni_Kenshin__Special_Techniques",
                "https://myanimelist.net/anime/9716/Rurouni_Kenshin__Review_Special",
                "https://myanimelist.net/anime/12067/Rurouni_Kenshin_Special",
                "https://myanimelist.net/anime/10334/Rurouni_Kenshin_Recap",
                "https://myanimelist.net/anime/37537/Sobakasu",
                "https://myanimelist.net/anime/50613/Rurouni_Kenshin__Meiji_Kenkaku_Romantan_2023",
            )
        }
    }

    @Test
    fun `correctly extract textNodes, trim and filter for non-blank values if xPath ends with an element`() {
        runBlocking {
            // when
            val result = XmlDataExtractor.extract(
                rawContent = loadTestResource("xml_data_extractor_tests/textnodes_example.html"),
                selection = mapOf(
                    "synonyms" to "//h2[contains(text(), 'Information')]/following-sibling::*//tr[1]/td[2]",
                )
            )

            // then
            assertThat(result.listNotNull<String>("synonyms")).containsExactlyInAnyOrder(
                "Prétear",
                "Prétear: The New Legend of Snow White",
                "Shin Shirayuki-hime Densetsu Pretear",
                "新白雪姫伝説プリーティア",
            )
        }
    }

    @Test
    fun `return empty list if selector was not found`() {
        runBlocking {
            // when
            val result = XmlDataExtractor.extract(
                rawContent = loadTestResource("xml_data_extractor_tests/example.html"),
                selection = mapOf(
                    "result" to "//unknow",
                )
            )

            // then
            assertThat(result.notFound("result")).isTrue()
        }
    }
}