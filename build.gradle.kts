import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm") version "1.6.10"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("org.jetbrains.dokka") version "1.6.10"
    application
}

group = "ru.itmo.sd.nebash"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")

    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("ru.itmo.sd.nebash.MainKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false // Activates all, even unstable rules
    config = files("$projectDir/config/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}
