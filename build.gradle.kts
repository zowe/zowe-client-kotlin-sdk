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
 */

import net.researchgate.release.GitAdapter
import org.jetbrains.changelog.Changelog
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
  id("org.sonarqube") version "5.1.0.4882"
  id("org.jetbrains.kotlin.jvm") version "1.9.20"
  id("org.jetbrains.dokka") version "1.9.20"
  id("net.researchgate.release") version "3.0.2"
  id("jacoco")
  id("org.jetbrains.changelog") version "2.2.1"
}

apply(plugin = "java")
apply(plugin = "kotlin")
apply(from = "gradle/sonar.gradle")

group = properties("group").get()
version = properties("version").get()

val retrofit2Version = "2.11.0"
val gsonVersion = "2.11.0"
val javaKeytarVersion = "1.0.0"
val snakeYamlVersion = "2.3"
val junitJupiterVersion = "5.11.0"
val mockwebserverVersion = "4.12.0"

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
  implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
  implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
  implementation("com.squareup.retrofit2:converter-scalars:$retrofit2Version")
  implementation("com.google.code.gson:gson:$gsonVersion")
  implementation("com.starxg:java-keytar:$javaKeytarVersion")
  implementation("org.yaml:snakeyaml:$snakeYamlVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
  testImplementation("com.squareup.okhttp3:mockwebserver:$mockwebserverVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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

  test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
  }

  jacocoTestReport {
    dependsOn("test")
    reports {
      xml.required.set(true)
      xml.outputLocation.set(file("${project.layout.buildDirectory.get()}/reports/jacoco.xml"))
    }
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
