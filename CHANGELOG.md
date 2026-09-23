# Zowe Client Kotlin SDK Changelog

All notable changes to the Zowe Client Kotlin SDK will be documented in this file.

## [Unreleased]

### Features

* Feature: Gradle is updated from 8.7 to 8.14.5 ([0ac6d86d](https://github.com/zowe/zowe-client-kotlin-sdk/commit/0ac6d86d))

### Security

* Security: Gradle wrapper JAR is committed and pinned with "distributionSha256Sum" instead of being fetched from the mutable "master" branch ([0ac6d86d](https://github.com/zowe/zowe-client-kotlin-sdk/commit/0ac6d86d))
* Security: Example programs are moved to a separate "samples" source set, excluded from the published artifact, and do not print the credentials ([45994412](https://github.com/zowe/zowe-client-kotlin-sdk/commit/45994412))
* Security: z/OS password is not rendered anymore by "toString" of "ZOSConnection", "ZoweConnection" and "ZoweConfigProfile" ([720fbdc8](https://github.com/zowe/zowe-client-kotlin-sdk/commit/720fbdc8))
* Security: TLS certificate and host name validation is enabled by default. "UnsafeOkHttpClient" is deprecated ([34a9e590](https://github.com/zowe/zowe-client-kotlin-sdk/commit/34a9e590))
* Security: "gradle/verification-metadata.xml" is added, so that Gradle verifies the SHA-256 checksum of every resolved dependency ([89edb135](https://github.com/zowe/zowe-client-kotlin-sdk/commit/89edb135))

### Bugfixes

* Bugfix: "rejectUnauthorized" defaults to true and "protocol" defaults to "https" when they are absent in the parsed YAML config ([722f7bd0](https://github.com/zowe/zowe-client-kotlin-sdk/commit/722f7bd0))

## [0.5.2] (2025-09-24)

### Bugfixes

* Bugfix: Fixed zowe.config.json for not yet defined credentials ([c60dd736](https://github.com/zowe/zowe-client-kotlin-sdk/commit/c60dd736))

## [0.5.1] (2024-11-15)

### Bugfixes

* Bugfix: Fixed work with encrypted/plaintext credentials in the zowe config ([3f1e48fb](https://github.com/zowe/zowe-client-kotlin-sdk/commit/3f1e48fb))

## [0.5.0] (2024-09-23)

### Features

* Feature: Added BYTES, KILOBYTES and MEGABYTES to Dataset class ([7f0d087a](https://github.com/zowe/zowe-client-kotlin-sdk/commit/7f0d087a))
* Feature: Added "failOnPrompt" parameter to the "issueTsoCommand" function ([b6903eb5](https://github.com/zowe/zowe-client-kotlin-sdk/commit/b6903eb5))
* Feature: Added responses validator ([c659e038](https://github.com/zowe/zowe-client-kotlin-sdk/commit/c659e038))
* Feature: Migrated "build.gradle" to "build.gradle.kts" ([f9eefc83](https://github.com/zowe/zowe-client-kotlin-sdk/commit/f9eefc83))
* Feature: Added "saveNewSecureProperties" function ([7c81c2a6](https://github.com/zowe/zowe-client-kotlin-sdk/commit/7c81c2a6))
* Feature: Added "executeTsoCommand" function to TsoApi ([c7fcb97b](https://github.com/zowe/zowe-client-kotlin-sdk/commit/c7fcb97b))
* Feature: Added parsing of nested profiles of Zowe Team Config v2 ([3ce259b8](https://github.com/zowe/zowe-client-kotlin-sdk/commit/3ce259b8))

### Bugfixes

* Bugfix: Fix for Windows credentials store ([4db02f88](https://github.com/zowe/zowe-client-kotlin-sdk/commit/4db02f88))

## [0.4.0] (2023-03-07)

### Features

* Feature: GitHub issue #3: Zowe Kotlin SDK: Java-like Kotlin API ([56788c1a](https://github.com/zowe/zowe-client-kotlin-sdk/commit/56788c1a))
* Feature: Implement ZosUssFile (copying methods - from uss to dsn and to uss folder) ([2bb0b257](https://github.com/zowe/zowe-client-kotlin-sdk/commit/2bb0b257))

### Bugfixes

* Bugfix: GitHub issue #9: Error Creating Connection ([bbc16d72](https://github.com/zowe/zowe-client-kotlin-sdk/commit/bbc16d72))

[0.5.2]: https://github.com/zowe/zowe-client-kotlin-sdk/compare/0.5.1...0.5.2
[0.5.1]: https://github.com/zowe/zowe-client-kotlin-sdk/compare/0.5.0...0.5.1
[0.5.0]: https://github.com/zowe/zowe-client-kotlin-sdk/compare/0.4.0...0.5.0
[0.4.0]: https://github.com/zowe/zowe-client-kotlin-sdk/commits/0.4.0
