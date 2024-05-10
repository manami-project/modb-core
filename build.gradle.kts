plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    `java-library`
    jacoco
    alias(libs.plugins.coveralls.jacoco)
}

group = "io.github.manamiproject"
version = project.findProperty("release.version") as String? ?: ""

val projectName = "modb-core"
val githubUsername = "manami-project"

repositories {
    mavenCentral()
    maven {
        name = "modb-test"
        url = uri("https://maven.pkg.github.com/$githubUsername/modb-test")
        credentials {
            username = parameter("GH_USERNAME", githubUsername)
            password = parameter("GH_PACKAGES_READ_TOKEN")
        }
    }
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core.jvm)
    api(libs.okhttp)

    implementation(libs.slf4j.api)
    implementation(libs.moshi)
    implementation(libs.jsonpathkt)
    implementation(libs.jsoup)

    testImplementation(libs.logback.classic)
    testImplementation(libs.modb.test)
}

kotlin {
    explicitApi()
    jvmToolchain(JavaVersion.VERSION_21.toString().toInt())
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports.html.required.set(false)
    reports.junitXml.required.set(true)
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    jacoco {
        exclude(
            "io/github/manamiproject/modb/core/converter/AnimeConverter.class",
            "io/github/manamiproject/modb/core/converter/PathAnimeConverter.class",
            "io/github/manamiproject/modb/core/downloader/Downloader.class",
            "io/github/manamiproject/modb/core/extractor/DataExtractor.class",
            "io/github/manamiproject/modb/core/extractor/PathDataExtractor.class",
            "io/github/manamiproject/modb/core/httpclient/HeaderCreator.class",
            "io/github/manamiproject/modb/core/httpclient/HttpClient.class",
            "io/github/manamiproject/modb/core/logging/Logger.class",
            "io/github/manamiproject/modb/core/logging/LogLevelRetriever.class",
        )
    }
}

val fileTreeConfig: (ConfigurableFileTree) -> Unit = {
    it.exclude(
        "io/github/manamiproject/modb/core/converter/AnimeConverter.class",
        "io/github/manamiproject/modb/core/converter/PathAnimeConverter.class",
        "io/github/manamiproject/modb/core/downloader/Downloader.class",
        "io/github/manamiproject/modb/core/extractor/DataExtractor.class",
        "io/github/manamiproject/modb/core/extractor/PathDataExtractor.class",
        "io/github/manamiproject/modb/core/httpclient/HeaderCreator.class",
        "io/github/manamiproject/modb/core/httpclient/HttpClient.class",
        "io/github/manamiproject/modb/core/logging/Logger.class",
        "io/github/manamiproject/modb/core/logging/LogLevelRetriever.class",
    )
}

tasks.jacocoTestReport {
    dependsOn(allprojects.map { it.tasks.named<Test>("test") })
    reports {
        html.required.set(false)
        xml.required.set(true)
        xml.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/test/jacocoFullReport.xml"))
    }
    afterEvaluate {
        classDirectories = files(classDirectories.files.map {
            fileTree(it to fileTreeConfig)
        })
    }
}

coverallsJacoco {
    reportPath = "${layout.buildDirectory}/reports/jacoco/test/jacocoFullReport.xml"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javaDoc by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = projectName
            url = uri("https://maven.pkg.github.com/$githubUsername/$projectName")
            credentials {
                username = parameter("GH_USERNAME", githubUsername)
                password = parameter("GH_PACKAGES_RELEASE_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = projectName
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javaDoc.get())

            pom {
                packaging = "jar"
                name.set(projectName)
                description.set("This lib is the base for every specific meta data provider module. It contains the API for downloaders and converters, defines the anime model and provides basic functionality.")
                url.set("https://github.com/$githubUsername/$projectName")

                licenses {
                    license {
                        name.set("AGPL-V3")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.txt")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:$githubUsername/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com:$githubUsername/$projectName.git")
                    url.set("https://github.com/$githubUsername/$projectName")
                }
            }
        }
    }
}

fun parameter(name: String, default: String = ""): String {
    val env = System.getenv(name) ?: ""
    if (env.isNotBlank()) {
        return env
    }

    val property = project.findProperty(name) as String? ?: ""
    if (property.isNotEmpty()) {
        return property
    }

    return default
}
