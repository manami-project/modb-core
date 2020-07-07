package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.extensions.pickRandom

internal enum class Browser {
    Firefox,
    Chromium;

    companion object {
        fun random(): Browser {
            return values().toList().pickRandom()
        }
    }
}