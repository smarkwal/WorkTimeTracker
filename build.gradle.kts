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
    id("com.github.ben-manes.versions") version "0.46.0"

    // create report with all open-source licenses
    id("com.github.jk1.dependency-license-report") version "2.4"

    // run Sonar analysis
    id("org.sonarqube") version "4.2.0.3129"

    // get current Git branch name
    id("org.ajoberstar.grgit") version "5.2.0"

    // JarHC Gradle plugin
    id("org.jarhc") version "1.0.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.12.0")
    implementation("com.google.inject:guice:5.1.0")
    implementation("org.slf4j:slf4j-api:2.0.7")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.7")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
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
    toolVersion = "0.8.8"
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
        property("sonar.java.test.binaries", "${buildDir}/classes/java/test")

        // include test results
        property("sonar.junit.reportPaths", "${buildDir}/test-results/test")

        // include test coverage results
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
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
        from("${buildDir}/reports/dependency-license") {
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

}

tasks.sonar {
    // run all tests and generate JaCoCo XML report
    dependsOn(tasks.test, tasks.jacocoTestReport)
}

// helper functions ------------------------------------------------------------

fun getGitBranchName(): String {
    return grgit.branch.current().name
}
