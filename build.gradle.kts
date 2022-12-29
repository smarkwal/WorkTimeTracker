plugins {
    java
    idea

    // Gradle Versions Plugin
    // https://github.com/ben-manes/gradle-versions-plugin
    id("com.github.ben-manes.versions") version "0.44.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.inject:guice:4.2.3") // TODO: upgrade to 5.1.0?
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

// special settings for IntelliJ IDEA
idea {

    project {
        jdkName = "11"
        languageLevel = org.gradle.plugins.ide.idea.model.IdeaLanguageLevel(JavaVersion.VERSION_11)
        vcs = "Git"
    }

}

tasks {

    processResources {

        // replace placeholders in resources
        // (see src/main/resources/version.properties)
        filesMatching("*.properties") {
            expand(
                "version" to project.version
            )
        }
    }

    jar {

        // set Main-Class in MANIFEST.MF
        manifest {
            attributes["Main-Class"] = "net.markwalder.tools.worktime.Main"
        }

        // include all compile time dependencies in JAR file
        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        exclude("META-INF/**")

    }

}
