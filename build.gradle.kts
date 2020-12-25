plugins {
    kotlin("jvm") version "1.4.21"
    id("com.jfrog.bintray") version "1.8.5"
    `maven-publish`
    `java-library`
}

repositories {
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/manami-project/maven")
    }
}

group = "io.github.manamiproject"
version = project.findProperty("releaseVersion") as String? ?: ""
val projectName = "modb-core"

dependencies {
    api(kotlin("stdlib-jdk8"))
    api("org.slf4j:slf4j-api:1.7.30")

    implementation(platform(kotlin("bom")))
    implementation("com.beust:klaxon:5.4")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("io.github.manamiproject:modb-test:1.2.0")
}

kotlin {
    explicitApi()
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = Versions.JVM_TARGET
    freeCompilerArgs = listOf("-Xinline-classes")
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = Versions.JVM_TARGET
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports.html.isEnabled = false
    reports.junitXml.isEnabled = false
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

object Versions {
    const val JVM_TARGET = "11"
}

bintray {
    user = project.findProperty("bintrayUser") as String? ?: ""
    key = project.findProperty("bintrayApiKey") as String? ?: ""

    setPublications("maven")

    with(pkg) {
        repo = "maven"
        name = projectName
        with(version) {
            name = project.version.toString()
            vcsTag = project.version.toString()
        }
        setLicenses("AGPL-V3")
        vcsUrl = "https://github.com/manami-project/$projectName"
    }
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
                url.set("https://github.com/manami-project/$projectName")

                licenses {
                    license {
                        name.set("AGPL-V3")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.txt")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:manami-project/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com:manami-project/$projectName.git")
                    url.set("https://github.com/manami-project/$projectName")
                }
            }
        }
    }
}