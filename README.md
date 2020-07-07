![Build](https://github.com/manami-project/modb-core/workflows/Build/badge.svg)
# modb-core
_modb_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

# What does this lib do?
This lib is the base for every specific meta data provider module. It contains the API for downloaders and converters, defines the anime model and provides basic functionality.
 
# Usage
Add repository and dependency
```kotlin
repositories {
    maven {
        url = uri("https://dl.bintray.com/manami-project/maven")
    }
}

dependencies {
    testImplementation("io.github.manamiproject:modb-core:$version")
}
```

# Features
Apart from the interfaces defining the API and the objects defining an anime this lib contains the following features.

## Converter
+ Default implementation for `PathConverter` which extends an `AnimeConverter` to convert files and directories

## HttpClient
+ A client for HTTP requests based on okhttp4
+ Can use individual retries if a request fails

### Retries
+ Offers `Retryable`s inspired by [resilience4j](https://github.com/resilience4j/resilience4j)
+ `Retryable`s are individualized by `RetryBehavior`s
+ `RetryBehavior`s can be stored in a `RetryableRegistry` to be able to simply reuse specific `RetrybleBehavior`

## Logger
+ Delegate for SLF4J logger
```kotlin
companion object {
    private val log by LoggerDelegate()
}
```
## Json (de)serialization
+ Object class for serializing/deserializing objects to/from JSON

## Extensions and utilities
+ Various extension and utility functions