plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "ru.itmo.sd.nebash"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<JavaExec>().all {
    mainClass.set("ru.itmo.sd.nebash.MainKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
