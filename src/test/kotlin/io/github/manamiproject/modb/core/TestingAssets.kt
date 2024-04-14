package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.ExtractionResult
import io.github.manamiproject.modb.core.extractor.OutputKey
import io.github.manamiproject.modb.core.extractor.Selector
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import java.net.URI

internal object MetaDataProviderTestConfig : MetaDataProviderConfig {
    override fun isTestContext(): Boolean = true
    override fun hostname(): Hostname = shouldNotBeInvoked()
    override fun buildAnimeLink(id: AnimeId): URI = shouldNotBeInvoked()
    override fun buildDataDownloadLink(id: String): URI = shouldNotBeInvoked()
    override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
}

internal object TestAnimeConverter : AnimeConverter {
    override suspend fun convert(rawContent: String) = shouldNotBeInvoked()
}

internal object TestDataExtractor : DataExtractor {
    override suspend fun extract(rawContent: String, selection: Map<OutputKey, Selector>): ExtractionResult = shouldNotBeInvoked()
}