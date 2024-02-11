![Build](https://github.com/manami-project/modb-core/actions/workflows/build.yml/badge.svg) [![Coverage Status](https://coveralls.io/repos/github/manami-project/modb-core/badge.svg)](https://coveralls.io/github/manami-project/modb-core) ![jdk21](https://img.shields.io/badge/jdk-21-informational)
# modb-core
_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

# What does this lib do?
This lib is the base for every specific meta data provider module. It contains the API for downloaders and converters, defines the anime model and provides basic functionality.

# Features
This lib contains the following features.

## SortedList
+ An implementation of `MutableList` whose elements are always sorted by a predefined `Comparator`

## Interfaces
* Interfaces for `MetaDataProviderConfig`, `Downloader`, `AnimeConverter`, `PathConverter`. `HttpClient` which define the standard API

## Converter
+ Default implementation for `PathConverter`. Takes an `AnimeConverter` as delegate and can convert files and directories

## HttpClient
+ Leightweight interface for creating HTTP calls including helpers for creating headers and setting user agents
+ Constants for the name of the property which can contain the path to a file providing possible user agents:
  + `FIREFOX_DESKTOP_USER_AGENTS_FILE_PROPERTY_NAME`
  + `FIREFOX_MOBILE_USER_AGENT_PROPERTY_NAME`
  + `CHROME_DESKTOP_USER_AGENT_PROPERTY_NAME`
  + `CHROME_MOBILE_USER_AGENT_PROPERTY_NAME`
+ Constants for the file name in the classpath providing possible user agents:
  + `FIREFOX_DESKTOP_USER_AGENT_RESOURCE_FILE`
  + `FIREFOX_MOBILE_USER_AGENT_RESOURCE_FILE`
  + `CHROME_DESKTOP_USER_AGENT_RESOURCE_FILE`
  + `CHROME_MOBILE_USER_AGENT_RESOURCE_FILE`

### DefaultHttpClient
+ Implementation of `HttpClient`
+ A client for HTTP requests based on [okhttp](https://github.com/square/okhttp)
+ Offers retry behavior which can be individualized

## Logger
+ Delegate for [SLF4J](https://github.com/qos-ch/slf4j) logger
```kotlin
companion object {
    private val log by LoggerDelegate()
}
```
+ Const `LOG_LEVEL_CONFIG_PROPERTY_NAME` for property name which lets you set the global log level

## Models
* For `Anime` and all underlying subtypes
    * Typealias for `Episodes`
    * Typealias for `Title`
    * Typealias for `Tag`
    * Enum `Anime.Type`
    * Enum `Anime.Status`
    * Type `AnimeSeason`
        * Typealias for `Year`
        * Type for `AnimeSeason.Season`
    * Type `Duration`
        * Typealias for `Seconds`
        * Enum `TimeUnit`
* Const `EMTPY` for empty `String`
* Const `LOCK_FILE_SUFFIX`  
* Const `YEAR_OF_THE_FIRST_ANIME` for year of the first anime
* Typealias for `RegularFile` as more specific version of `Path`
* Typealias for `Directory` as more specific version of `Path`
* Typealias for `FileSuffix`

## Json (de)serialization
+ Object class for serializing/deserializing objects to/from JSON

## Extension and utility functions

| function                                |description|
|-----------------------------------------|----|
| `loadResource`                          |Conveniently load a file from `src/main/resources`|
| `resourceFileExists`                    |Checks if a file exists in `src/main/resources`|
| `random`                                |Pick a a random number from a given interval|
| `excludeFromTestContext`                |Won't execute the code within during test execution|
| `Collection<T>.pickRandom()`            |Picks a random element of a `Collection`|
| `Int.toAnimeId`                         |Converts an `Int` to an `AnimeId`|
| `List<T>.createShuffledList()`          |Creates a new list from the given `List` and randomizes the order of elements|
| `List<T>.containsExactlyInTheSameOrder` |Checks if a list contains the same elements in the same order as another list|
| `OutputStream.write`                    |Writes a `String` to the `OutputStream` and also flushes the stream|
| `Path.changeSuffix`                     |Changes the file suffix|
| `Path.regularFileExists`                |Checks if a given `Path` exists and is a file|
| `Path.directoryExists`                  |Checks if a given `Path` exists and is a directory|
| `Path.readFile`                         |Read the content of a file into a `String`|
| `Path.copyTo`                           |Copy file to file, directory to directory or a file into a directory|
| `Path.fileName`                         |Filename as `String`|
| `Path.fileSuffix`                       |Returns the file suffix as `String`|
| `ByteArray.writeToFile`                 |Write `ByteArray` to file and optionally write a lock file as indications for other processes that the file is being written|
| `String.writeToFile`                    |Write `String` to file and optionally write a lock file as indications for other processes that the file is being written|
| `String.remove`                         |Remove sequence from a `String`|
| `String.normalizeWhitespaces`           |Replaces multiple consecutive whitespaces with a single one|