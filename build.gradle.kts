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

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<JavaExec>().all {
    mainClass.set("ru.itmo.sd.nebash.MainKt")
}

tasks.withType<Test> {
    dependsOn("detekt")
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
