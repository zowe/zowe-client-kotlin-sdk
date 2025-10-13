/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

import net.researchgate.release.GitAdapter
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher

fun properties(key: String) = providers.gradleProperty(key)
fun dateValue(pattern: String): String =
  LocalDate.now(ZoneId.of("Europe/Warsaw")).format(DateTimeFormatter.ofPattern(pattern))

plugins {
  base
  java
  `maven-publish`
  id("jacoco")
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.sonarqube)
  alias(libs.plugins.dokka)
  alias(libs.plugins.release)
  alias(libs.plugins.changelog)
  alias(libs.plugins.kover)
}

apply(plugin = "java")
apply(plugin = "kotlin")
apply(from = "gradle/sonar.gradle")

group = properties("group").get()
version = properties("version").get()

val releaseScope = if (project.hasProperty("release.scope")) project.property("release.scope") else "patch"
val mavenUser = properties("mavenUser").get()
val mavenPassword = properties("mavenPassword").get()

repositories {
  mavenCentral()
  maven {
    url = uri(properties("artifactoryMavenSnapshotRepo").get())
    credentials {
      username = mavenUser
      password = mavenPassword
    }
  }
  maven {
    url = uri(properties("artifactoryMavenRepo").get())
    credentials {
      username = mavenUser
      password = mavenPassword
    }
  }
  maven {
    url = uri("https://plugins.gradle.org/m2/")
  }
}

base {
  archivesName.set(properties("archivesName").get())
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
  withSourcesJar()
  withJavadocJar()
}

dependencies {
  api(libs.slf4j.api)
  implementation(libs.retrofit2)
  implementation(libs.retrofit2.converter.gson)
  implementation(libs.retrofit2.converter.scalars)
  // TODO: remove and use kotlinx.serialization.json since K2
  implementation(libs.gson)
  // TODO: remove and use kotlinx.serialization.json since K2
  implementation(libs.jackson.core.databind)
  implementation(libs.jackson.module.kotlin)
  // TODO: delete keytar lib after refactoring of interaction with Zowe Config
  implementation(libs.java.keytar)
  implementation(libs.snakeyaml)
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.cio)
  implementation(libs.ktor.client.content.negotiation)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.sshj)
  implementation(libs.json.schema.validator)
  // New way of testing
  testImplementation(libs.kotest.assertions.core)
  testImplementation(libs.kotest.runner.junit5)
  // Old way of testing
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.mockwebserver)
  testImplementation(libs.okhttp.tls)
  testImplementation(libs.sshd.core)
  testImplementation(libs.logback.classic)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
  version = properties("version")
  header.set(provider { "${version.get()} (${dateValue("yyyy-MM-dd")})" }.get())
  groups.set(listOf("Breaking changes", "Features", "Bugfixes", "Deprecations", "Security"))
  keepUnreleasedSection.set(false)
  itemPrefix.set("*")
  repositoryUrl = properties("repositoryUrl")
  sectionUrlBuilder.set { repositoryUrl, currentVersion, previousVersion, isUnreleased: Boolean ->
    repositoryUrl + when {
      isUnreleased -> when (previousVersion) {
        null -> "/commits"
        else -> "/compare/$previousVersion...HEAD"
      }

      previousVersion == null -> "/commits/$currentVersion"
      else -> "/compare/$previousVersion...$currentVersion"
    }
  }
}

kover {
  currentProject {
    instrumentation {
      /* exclude Gradle test tasks */
      disabledForTestTasks.addAll("intTest")
    }
  }
  reports {
    filters {
      includes {
        classes(
          providers.provider { "org.zowe.kotlinsdk.annotations.*" },
          providers.provider { "org.zowe.kotlinsdk.core.*" },
          providers.provider { "org.zowe.kotlinsdk.providers.*" },
        )
      }
    }
  }
}

tasks {
  wrapper {
    gradleVersion = properties("gradleVersion").get()
  }

  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_17.toString()
      languageVersion = LanguageVersion.LATEST_STABLE.versionString
    }
  }

  // Build native Rust secrets library
  register<Exec>("buildNativeSecrets") {
    group = "build"
    description = "Build Rust native secrets library"
    workingDir = file("src/secrets/scripts")

    commandLine = listOf("sh", "-c", "chmod +x build_secrets.sh && ./build_secrets.sh")

    // Rebuild only if files are changed
    inputs.files(
      fileTree("src/secrets/src"),
      file("src/secrets/Cargo.toml"),
      file("src/secrets/Cargo.lock")
    )

    outputs.dirs(
      "src/main/resources/native/linux/x86_64",
      "src/main/resources/native/linux/aarch64",
      "src/main/resources/native/macos/x86_64",
      "src/main/resources/native/macos/aarch64",
      "src/main/resources/native/windows/x86_64",
      "src/main/resources/native/windows/aarch64"
    )
  }

  // Make sure Rust secrets native lib is built before Kotlin
  named("compileKotlin") {
    dependsOn("buildNativeSecrets")
  }

  named("processResources") {
    dependsOn("buildNativeSecrets")
  }

  // Development: rebuild only Rust secrets native
  register<Exec>("rebuildNative") {
    group = "build"
    description = "Rebuild only the native library for current platform (faster for development)"
    workingDir = file("src/secrets")

    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()

    val target = when {
      osName.contains("linux") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
        "aarch64-unknown-linux-gnu"
      osName.contains("linux") ->
        "x86_64-unknown-linux-gnu"
      osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
        "aarch64-apple-darwin"
      osName.contains("mac") ->
        "x86_64-apple-darwin"
      osName.contains("win") && (osArch.contains("aarch64") || osArch.contains("arm64")) ->
        "aarch64-pc-windows-msvc"
      osName.contains("win") ->
        "x86_64-pc-windows-msvc"
      else -> throw GradleException("Unsupported platform: $osName / $osArch")
    }

    commandLine = listOf("cargo", "build", "--release", "--target", target)

    doLast {
      println("Built for target: $target")
      println("Run './gradlew processResources' to copy to resources")
    }
  }

  // Rust artifacts cleanup
  register<Delete>("cleanNative") {
    group = "build"
    description = "Clean Rust build artifacts"
    delete(
      file("src/secrets/target"),
      fileTree("src/native") {
        include("**/*.so", "**/*.dylib", "**/*.dll")
      }
    )
  }

  named("clean") {
    dependsOn("cleanNative")
  }

  named("sourcesJar") {
    dependsOn("buildNativeSecrets")
  }

  test {
    useJUnitPlatform()

    testLogging {
      events("passed", "skipped", "failed")
      // showStandardStreams = true
    }

    finalizedBy("jacocoTestReport")
    finalizedBy("koverHtmlReport")
    finalizedBy("koverXmlReport")

    afterSuite(
      KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
        if (desc.parent == null) { // will match the outermost suite
          val output =
            "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, " +
              "${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
          val fileName = "./build/reports/tests/${result.resultType}.txt"
          File(fileName).writeText(output)
        }
      })
    )
  }

  jacocoTestReport {
    dependsOn("test")
    reports {
      xml.required.set(true)
      xml.outputLocation.set(file("${project.layout.buildDirectory.get()}/reports/jacoco.xml"))
    }
  }

  koverHtmlReport {
    finalizedBy("koverXmlReport")
  }

  register<Test>("intTest") {
    description = "Run integration tests"
    group = "verification"
    testClassesDirs = sourceSets.getByName("intTest").output.classesDirs
    classpath = sourceSets.getByName("intTest").runtimeClasspath
    useJUnitPlatform {
      excludeTags("FirstTime")
    }
    testLogging {
      events("passed", "skipped", "failed")
    }
  }

  withType<DokkaTask> {
    val dokkaBaseConfiguration = """
        {
          "footerMessage": "(c) 2024 Zowe Community",
          "templatesDir": "${file("dokka/templates").absolutePath.replace('\\', '/')}",
          "customAssets": ["${file("dokka/assets/zowe-icon.svg").absolutePath.replace('\\', '/')}", "${file("dokka/assets/zowe-icon.png").absolutePath.replace('\\', '/')}"],
          "customStyleSheets": ["${file("dokka/assets/logo-styles.css").absolutePath.replace('\\', '/')}"]
        }
    """.trimIndent()
    pluginsMapConfiguration.set(
      // fully qualified plugin name to json configuration
      mapOf("org.jetbrains.dokka.base.DokkaBase" to dokkaBaseConfiguration)
    )
  }

  register("publishAllVersions") {
    group = "Zowe Publishing"
    description = "Publish JAR files to Zowe Artifactory"
    doLast {
      println("Published JARs")
    }
    dependsOn("publish")
  }

  afterReleaseBuild {
    dependsOn("publishAllVersions")
  }
}

//-----------Integration tests configuration start
sourceSets {
  create("intTest") {
    java {
      compileClasspath += main.get().output
      runtimeClasspath += main.get().output
      srcDir("src/intTest/kotlin")
    }
    resources {
      srcDir("src/intTest/resources")
    }
  }
}

configurations {
  named("intTestImplementation") {
    extendsFrom(configurations.getByName("testImplementation"))
  }
  named("intTestRuntimeOnly") {
    extendsFrom(configurations.getByName("testRuntimeOnly"))
  }
}
//-----------Integration tests configuration end

//-----------Release and publish part start
publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      groupId = "org.zowe.sdk"
      artifactId = "zowe-kotlin-sdk"
      version = properties("version").get()

      from(components["java"])

      versionMapping {
        usage("java-api") {
          fromResolutionOf("runtimeClasspath")
        }
        usage("java-runtime") {
          fromResolutionResult()
        }
      }

      pom {
        // add all items necessary for maven central publication
        name = "Zowe-Client-Kotlin-SDK"
        description = "Library that allows to perform http requests to IBM z/OSMF REST API with the help of Retorfit2."
        url = "https://github.com/zowe/zowe-client-kotlin-sdk"

        issueManagement {
          system = "GitHub"
          url = "https://github.com/zowe/zowe-client-kotlin-sdk/issues"
        }
        licenses {
          license {
            name = "Eclipse Public License 2.0"
            url = "https://github.com/zowe/zowe-client-kotlin-sdk/blob/main/LICENSE"
            distribution = "repo"
          }
        }
        scm {
          url = "https://github.com/zowe/zowe-client-kotlin-sdk"
          connection = "scm:git:git://github.com/zowe/zowe-client-kotlin-sdk.git"
          developerConnection = "scm:git:git@github.com:zowe/zowe-client-kotlin-sdk.git"
        }
        developers {
          developer {
            id = "zowe-ijmp"
            name = "Zowe IntelliJ Plugin team"
            email = "zowe.robot@gmail.com"
          }
        }
      }
    }
  }
  repositories {
    maven {
      credentials {
        username = if (project.hasProperty("zowe.deploy.username")) project.property("zowe.deploy.username") as String else ""
        password = if (project.hasProperty("zowe.deploy.password")) project.property("zowe.deploy.password") as String else ""
      }
      url = if (properties("version").get().endsWith("-SNAPSHOT"))
        uri(properties("artifactoryPublishingMavenSnapshotRepo").get())
      else
        uri(properties("artifactoryPublishingMavenRepo").get())
    }
  }
}

release {
  failOnCommitNeeded = true
  failOnPublishNeeded = true
  failOnSnapshotDependencies = true
  failOnUnversionedFiles = false
  failOnUpdateNeeded = true
  revertOnFail = true
  preCommitText = "[Gradle Release plugin]"
  preTagCommitMessage = "[skip ci] Before tag commit"
  tagCommitMessage = "Release:"
  tagTemplate = "$version"
  newVersionCommitMessage = "Create new version:"
  versionPropertyFile = "gradle.properties"

  when (releaseScope) {
    "minor" -> {
      versionPatterns = mapOf(
        "[.]*\\.(\\d+)\\.(\\d+)[.]*"
                to KotlinClosure2<Matcher, Project, String>({ m: Matcher, _ ->
          m.replaceAll(".${m.group(0)[1] + 1}.0")
        })
      )
    }

    "major" -> {
      versionPatterns = mapOf(
        "(\\d+)\\.(\\d+)\\.(\\d+)[.]*"
                to KotlinClosure2<Matcher, Project, String>({ m: Matcher, _ ->
          m.replaceAll("${m.group(0)[1] + 1}.0.0")
        })
      )
    }

    else -> {
      versionPatterns = mapOf(
        "(\\d+)([^\\d]*$)"
                to KotlinClosure2<Matcher, Project, String>({ m: Matcher, _ ->
          m.replaceAll("${m.group(1).toInt() + 1}${m.group(2)}")
        })
      )
    }
  }

  scmAdapters = listOf(GitAdapter::class.java)

  git {
    requireBranch.set("")
    pushToRemote.set("origin")
    pushToBranchPrefix.set("")
    commitVersionFileOnly.set(false)
    signTag.set(false)
  }
}
//-----------Release and publish part end
