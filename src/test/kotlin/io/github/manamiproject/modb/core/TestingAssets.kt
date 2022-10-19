package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.*
import java.net.URI
import kotlin.test.fail

internal object MetaDataProviderTestConfig : MetaDataProviderConfig {
    override fun isTestContext(): Boolean = true
    override fun hostname(): Hostname = shouldNotBeInvoked()
    override fun buildAnimeLink(id: AnimeId): URI = shouldNotBeInvoked()
    override fun buildDataDownloadLink(id: String): URI = shouldNotBeInvoked()
    override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
}

internal object TestAnimeConverter : AnimeConverter {
    @Deprecated("Use coroutines",
        ReplaceWith("shouldNotBeInvoked()", "io.github.manamiproject.modb.test.shouldNotBeInvoked")
    )
    override fun convert(rawContent: String): Anime = shouldNotBeInvoked()
    override suspend fun convertSuspendable(rawContent: String) = shouldNotBeInvoked()
}


inline fun <reified T: Throwable> suspendableExpectingException(noinline func: suspend CoroutineScope.() -> Unit): Throwable? {
    var result: Throwable? = null

    val handler = CoroutineExceptionHandler { _, throwable ->
        result = throwable
    }

    runBlocking {
        CoroutineScope(Job() + CoroutineName("UnitTest") + handler).launch {
            func.invoke(this)
        }.join()
    }

    return when (result) {
        null -> fail("No exception has been thrown")
        !is T -> fail("Expected [${T::class}] to be thrown, but [${result!!::class}] was thrown.")
        else -> result
    }
}