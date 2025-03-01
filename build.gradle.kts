/*
 * Copyright 2022 Stephan Markwalder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    java
    idea
    jacoco

    // Gradle Versions Plugin
    // https://github.com/ben-manes/gradle-versions-plugin
    id("com.github.ben-manes.versions") version "0.52.0"

    // create report with all open-source licenses
    id("com.github.jk1.dependency-license-report") version "2.9"

    // run Sonar analysis
    id("org.sonarqube") version "6.0.1.5171"

    // get current Git branch name
    id("org.ajoberstar.grgit") version "5.3.0"

    // JarHC Gradle plugin
    id("org.jarhc") version "1.1.1"
}

buildscript {
    dependencies {
        // fix CVE-2023-3635 in Okio < 3.4.0
        // (indirect dependency of Gradle Versions Plugin 0.51.0)
        classpath("com.squareup.okio:okio:3.10.2")
        classpath("com.squareup.okio:okio-jvm:3.10.2")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("commons-io:commons-io:2.18.0")
    implementation("com.google.inject:guice:5.1.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.15.2")

    // fix vulnerabilities in transitive dependencies
    // fix CVE-2018-10237 and CVE-2020-8908
    implementation("com.google.guava:guava:33.4.0-jre")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// special settings for IntelliJ IDEA
idea {

    project {
        jdkName = "17"
        languageLevel = org.gradle.plugins.ide.idea.model.IdeaLanguageLevel(JavaVersion.VERSION_17)
        vcs = "Git"
    }

    module {
        excludeDirs = setOf(file(".jarhc"))
    }

}

licenseReport {
    // create CSV report with all open-source licenses
    renderers = arrayOf(
        com.github.jk1.license.render.CsvReportRenderer("licenses.csv")
    )
}

jacoco {
    toolVersion = "0.8.10"
}

sonar {
    // documentation: https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/

    properties {

        // connection to SonarCloud
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "smarkwal")
        property("sonar.projectKey", "smarkwal_WorkTimeTracker")

        // Git branch
        property("sonar.branch.name", getGitBranchName())

        // paths to test sources and test classes
        property("sonar.tests", "${projectDir}/src/test/java")
        property("sonar.java.test.binaries", "${layout.buildDirectory.get()}/classes/java/test")

        // include test results
        property("sonar.junit.reportPaths", "${layout.buildDirectory.get()}/test-results/test")

        // include test coverage results
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
    }
}

tasks {

    compileJava {
        // report usage of deprecated APIs
        options.compilerArgs.add("-Xlint:deprecation")
    }

    processResources {

        // replace placeholders in resources
        // (see src/main/resources/version.properties)
        filesMatching("*.properties") {
            expand(
                "version" to project.version
            )
        }
    }

    test {

        // enable headless mode for tests
        systemProperty("java.awt.headless", "true")

        // set timezone for tests
        systemProperty("user.timezone", "Europe/Zurich")

        // check if test resources have to be regenerated
        if (project.hasProperty("generate.test.resources")) {
            systemProperty("generate.test.resources", "true")
        }

    }

    jar {

        // make sure that license report has been generated
        dependsOn("generateLicenseReport")

        // set Main-Class in MANIFEST.MF
        manifest {
            attributes["Main-Class"] = "net.markwalder.tools.worktime.Main"
        }

        // add LICENSE to JAR file
        from("../LICENSE")

        // include all runtime dependencies in JAR file
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

        // include license report in JAR file
        from("${layout.buildDirectory.get()}/reports/dependency-license") {
            into("META-INF/licenses")
        }

        exclude(

            // exclude module-info files
            "**/module-info.class",

            // exclude license files
            "META-INF/LICENSE", "META-INF/LICENSE.txt",
            "META-INF/NOTICE", "META-INF/NOTICE.txt",
            "META-INF/DEPENDENCIES",

            // exclude signature files
            "META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA"
        )

    }

    jacocoTestReport {

        // run all tests first
        dependsOn(test)

        reports {

            // generate XML report (required for Sonar)
            xml.required.set(true)

            // generate HTML report
            html.required.set(true)
        }
    }

    jarhcReport {
        dependsOn(jar)
        classpath.setFrom(
            //jar.get().archiveFile,
            configurations.runtimeClasspath
        )
        reportFiles.setFrom(
            file("${rootDir}/docs/jarhc-report.html"),
            file("${rootDir}/docs/jarhc-report.txt")
        )
    }

    build {
        dependsOn(jarhcReport)
    }

    dependencyUpdates {
        gradleReleaseChannel = "current"
        rejectVersionIf {
            isNonStable(candidate.version)
                    || candidate.group == "com.google.inject" && candidate.module == "guice" && candidate.version >= "6.0"
        }
    }

}

tasks.sonar {
    // run all tests and generate JaCoCo XML report
    dependsOn(tasks.test, tasks.jacocoTestReport)
}

// helper functions ------------------------------------------------------------

fun getGitBranchName(): String {
    return grgit.branch.current().name
}

fun isNonStable(version: String): Boolean {
    if (version.contains("-SNAPSHOT")) return true
    if (version.contains("-alpha")) return true
    if (version.contains("-beta")) return true
    if (version.contains("-rc")) return true
    return false
}
