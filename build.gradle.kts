plugins {
    id("java")
    id("maven-publish")
}

group = "dev.morazzer"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

val implementToJar by configurations.creating {
    extendsFrom(configurations["runtimeClasspath"])
}

configurations {
    compileClasspath.extendsFrom(implementToJar)
}

dependencies {
    implementToJar("com.github.Morazzer:item-builder:f4f9d83863")
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    implementToJar.forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group as String?
            artifactId = project.name
            version = project.version as String?

            from(components["java"])
        }
    }
}
