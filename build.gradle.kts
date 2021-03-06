@file:Suppress("GradlePackageVersionRange")

plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val archivesBaseName = property("archives_base_name")
group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    mavenCentral()
    maven("https://maven.bymartrixx.me") {}
    maven("https://jitpack.io") {}
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")

    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")

    modImplementation("me.bymartrixx.player-events:api:${properties["player_events_api_version"]}")

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("com.github.saibotk:JMAW:0.3.1")?.let { shadow(it) }

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.junit.platform:junit-platform-runner:1.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks {

    processResources {
        inputs.property("version", project.version)
        filteringCharset = "UTF-8"
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    java {
        withSourcesJar()
    }

    named<Jar>("jar") {
        from("LICENSE") {
            rename {
                "${it}_${archivesBaseName}"
            }
        }
        from(compileJava)
        from(processResources)
    }

    shadowJar {
        configurations = listOf(project.configurations.shadow.get())
    }
    remapJar{
        inputFile.set(shadowJar.get().archiveFile)
    }
    prepareRemapJar{
        dependsOn(shadowJar)
    }

    named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

    create("copyJarToServer") {
        mustRunAfter("build")
        listOf(project.property("server1") as String).forEach {
            copyFile(it)
        }
    }

}

fun copyFile(path: String) {
    println("path: $path")
    copy {
        from(".\\build\\libs\\FK-1.0-SNAPSHOT.jar")
        into(path)
    }
}