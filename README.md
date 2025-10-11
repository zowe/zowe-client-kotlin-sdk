# Zowe® Сlient Kotlin SDK

Zowe Client Kotlin SDK cover z/OSMF REST API with Kotlin object-oriented code using Retrofit2. It provides functionality to send HTTP requests to z/OSMF using Kotlin functions.

## Installation

To install this library in your project, use one of build tools like Maven, Gradle or Ant.

### Maven dependency

To add the SDK as a Maven dependency:

1. Declare a Zowe JFrog artifactory as a custom repository to fetch from:

```xml
<project>
...
  <repositories>
    <repository>
      <id>zowe-jfrog</id>
      <name>Zowe JFrog artifactory</name>
      <url>https://zowe.jfrog.io/zowe/libs-release</url>
    </repository>
  </repositories>
...
</project>
```

2. Add a new Maven dependency:

```xml
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>org.zowe.sdk</groupId>
      <artifactId>zowe-kotlin-sdk</artifactId>
      <!-- change the {version} to the actual version of the Zowe Client Kotlin SDK -->
      <version>{version}</version>
    </dependency>
  </dependencies>
</project>
```

### Gradle dependency

To add the SDK as a Gradle dependency:

1. Declare a Zowe JFrog artifactory as a custom repository to fetch from:

```kotlin
repositories {
  ...
  maven {
    url = uri("https://zowe.jfrog.io/zowe/libs-release")
  }
  ...
}
```

2. Add a new Gradle dependency:

```kotlin
// change the <version> to the actual version of the Zowe Client Kotlin SDK
val zoweKotlinSdkVersion = <version>

dependencies {
  ...
  implementation("org.zowe.sdk:zowe-kotlin-sdk:${zoweKotlinSdkVersion}")
  ...
}
```

### How to use the SDK in your project

To use the Zowe Client Kotlin SDK after it is added as a dependency in your project, simply import any of the needed packages:

```kotlin
...
import org.zowe.kotlinsdk
...
```

## Guide

In the Zowe Client Kotlin SDK, there are a number of different classes to access z/OSMF REST API, as well as the different methods of doing so to provide as much customizability as the user needs.

Brief description of the packages in the SDK:
- **[org.zowe.kotlinsdk](#orgzowekotlinsdk)** - the root package with raw APIs for various purposes
- **[org.zowe.kotlinsdk.annotations](#orgzowekotlinsdkannotations)** - the package to provide the annotations to mark the functionalities availability for a specific version of z/OS
- **[org.zowe.kotlinsdk.zowe.client.sdk](#orgzowekotlinsdkzoweclientsdk)** - the default implementation of raw APIs usage to provide the default handling of the requests and responses to z/OSMF REST API
- **[org.zowe.kotlinsdk.zowe.config](#orgzowekotlinsdkzoweconfig)** - the functionality to work with Zowe Team Config v2
- **[org.zowe.kotlinsdk.zowe.examlples](#orgzowekotlinsdkzoweexamples)** - the examples of how to use Zowe Team Config v2 functionality with Zowe Client Kotlin SDK

Refer to the specific package description below to know more the purpose and how to use each of them.

### [org.zowe.kotlinsdk](./src/main/kotlin/org/zowe/kotlinsdk/)

The package includes such APIs as:
- **[Data API](./src/main/kotlin/org/zowe/kotlinsdk/DataAPI.kt)** - the functionality to work with z/OS datasets and USS files
- **[Console API](./src/main/kotlin/org/zowe/kotlinsdk/ConsoleAPI.kt)** - the functionality to work with z/OS console services
- **[Info API](./src/main/kotlin/org/zowe/kotlinsdk/InfoAPI.kt)** - the functionality to work with z/OSMF /info endpoint requests (e.g. to get initial system info)
- **[JES API](./src/main/kotlin/org/zowe/kotlinsdk/JESApi.kt)** - the functionality to work with z/OS JES
- **[Service API](./src/main/kotlin/org/zowe/kotlinsdk/ServiceAPI.kt)** - the functionality to work with z/OS /service endpoint requests (e.g. to change user's password)
- **[Systems API](./src/main/kotlin/org/zowe/kotlinsdk/SystemsApi.kt)** - the functionality to work with z/OS /resttopology endpoint requests (e.g. to find out the available systems under the specified address)
- **[TSO API](./src/main/kotlin/org/zowe/kotlinsdk/TsoApi.kt)** - the functionality to work with TSO/E address space services

The raw APIs provide descriptions with headers and body parameters to issue a request. To use them, you need to declare a functionality to trigger the specific request together with the response handling. There are default request and response handlers under [org.zowe.kotlinsdk.zowe.client.sdk](#orgzowekotlinsdkzoweclientsdk) package, so you don't need to specify the functionality by yourself.

> [!NOTE]
> There is also an **API wrapper** functionality builder declared under **[org/zowe/kotlinsdk/api.kt](./src/main/kotlin/org/zowe/kotlinsdk/api.kt)** to simplify the process of building the API wrapper.

To customize the request triggering and response handling:
1. Import the necessary raw API to cover, as well as **Retrofit2 API builder**:

```kotlin
...
// Import DataAPI and API wrapper
import org.zowe.kotlinsdk.DataAPI
<<<
import org.zowe.kotlinsdk.buildApi
--- or ---
import org.zowe.kotlinsdk.buildApiWithBytesConverter
>>>
...
```

2. Wrap the API with the **Retrofit2 API builder**

```kotlin
...
// Build DataAPI wrapper
val datasetsApi = buildApi(<zosmf-url>, <okhttp-client>, DataAPI::class.java)
...
```

3. Use the API wrapper and process the response

```kotlin
...
// Declare the request
val listDataSetsRequest = datasetsApi
  .listDataSets(
    xIBMAttr = XIBMAttr(XIBMAttr.Type.BASE, true),
    authorizationToken = Credentials.basic("<username>", "<password>"),
    dsLevel = "USERHLQ.*"
  )
  
// Trigger the request
val listDataSetsResponse = listDataSetsRequest.execute()

// Process the response
if (listDataSetsResponse.isSuccessful){
  val members = response.body();
}
...
```

### [org.zowe.kotlinsdk.annotations](./src/main/kotlin/org/zowe/kotlinsdk/annotations/)

The annotations package provides an annotation class to mark the functionality of z/OS available since some version of the z/OS itself. E.g. there is a **[ServiceAPI](./src/main/kotlin/org/zowe/kotlinsdk/ServiceAPI.kt)** where the usage of the "change password" functionality is introduced. The **"/zosmf/services/authenticate"** endpoint is available since z/OS 2.5 only.

The "availability" classes:
1. **ZVersion** - the enum class to indicate the version of z/OS
2. **AvailableSince** - the annotation class to mark the functionality with the availability since a specific version (including the version)

### [org.zowe.kotlinsdk.zowe.client.sdk](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/)

This package provides the default API isage implementation that is available to use in your code without defining z/OSMF REST API calls handler. There are subpackages available to separate functionalities of different z/OS components:
1. **[core](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/core/)** - provides some core features to work with z/OSMF REST API. For now, there is a **[ZOSConnection](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/core/ZOSConnection.kt)** data class only. This class represents connection instances to carry all the necessary information to send a z/OSMF REST API request
2. **[zosconsole](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/zosconsole/)** - provides functionality to work with z/OS console services
3. **[zosfiles](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/zosfiles/)** - provides functionality to work with z/OS datasets
3. **[zosjobs](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/zosjobs/)** - provides functionality to work with z/OS JES
3. **[zostso](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/zostso/)** - provides functionality to work with TSO/E address space services
3. **[zosuss](./src/main/kotlin/org/zowe/kotlinsdk/zowe/client/sdk/zosuss/)** - provides functionality to work with USS files

As the example of how to use the default implementation:
1. Import **ZOSConnection** and other classes with the functionality needed:

```kotlin
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.ZosDsn
```

2. Create a **ZOSConnection** instance:

```kotlin
val zosConnection = ZOSConnection("<host>", "<port>", "<username>", "<password>", "http(s)")
```

3. Create the functionality class instance and trigger the function needed:

```kotlin
val result = ZosDsn(zosConnection).createDsn("<dsn>", <CreateDataset cls instance>)
```

### [org.zowe.kotlinsdk.zowe.config](./src/main/kotlin/org/zowe/kotlinsdk/zowe/config/)

This package provides the functionality to work with **[Zowe Team Config v2](https://docs.zowe.org/stable/user-guide/cli-using-using-team-profiles/)**. The **[ZoweConfig](./src/main/kotlin/org/zowe/kotlinsdk/zowe/config/ZoweConfig.kt)** class provides the functions to parse and manipulate the **zowe.config.json** file. See the **[org.zowe.kotlinsdk.zowe.examples](#orgzowekotlinsdkzoweexamples)** package for examples.

### [org.zowe.kotlinsdk.zowe.examples](./src/main/kotlin/org/zowe/kotlinsdk/zowe/examples/)

This package provides the examples of how to work with **[Zowe Team Config v2](https://docs.zowe.org/stable/user-guide/cli-using-using-team-profiles/)** in Kotlin.

## Contributing

For the general contributing rules, refer to [CONTRIBUTING.md](https://github.com/zowe/zowe-explorer-intellij/blob/main/CONTRIBUTING.md) under **Zowe Explorer plug-in for IntelliJ IDEA repo**

### To run unit tests

```shell
./gradlew test
```

### To run integration tests

> [!NOTE]
> Integration tests are running under the real environment setup. To run them, you need either to have the environment setup and provide all the necessary env variables or to stub the functionality of the z/OSMF REST API responses.

Before running integration tests, you need three variables to be set up:
- **``ZOSMF_TEST_URL``** - an URL of the real mainframe with z/OSMF REST API to run the tests
- **``ZOSMF_TEST_USERNAME``** - a username with appropriate permissions to run the tests
- **``ZOSMF_TEST_PASSWORD``** - a user password to run the tests

To run the integration tests:

```shell
./gradlew intTest
```

### To generate source code documentation

We use **[Dokka](https://github.com/Kotlin/dokka)** to generate the SDK documentation.

To generate the docs, run:

```shell
./gradlew dokkaHtml
```

The generated docs will be generated under **build/dokka/html**

## Release process

The rules and commands below describe how to publish artifacts for the Zowe Client Kotlin SDK in Zowe JFrog artifactory.

> [!NOTE]
> Prefer to use **[GitHub Release Workflow](./.github/workflows/release-new-version.yml)** to release a new version of the Zowe Client Kotlin SDK

### Rules to publish

1. The version of the built SDK will be in a **SemVer** format. To provide a build that is not yet ready to be publicly used, add the **-rc.n** at the end of the version, where **n** is **the next release candidate**.
2. Provide **`zowe.deploy.username`** and **`zowe.deploy.password`** as the parameters of the **`release`** command to specify the credentials to the [Zowe Artifactory](https://zowe.jfrog.io/), as well as the **`artifactory_user`** and the **`artifactory_password`** (generally, the credentials might be the same, but the variables are different for the different tasks during the release process).
Example:

```shell
./gradlew release -Pzowe.deploy.username=$ARTIFACTORY_USERNAME -Pzowe.deploy.password=$ARTIFACTORY_PASSWORD -Partifactory_user=$ARTIFACTORY_USERNAME -Partifactory_password=$ARTIFACTORY_USERNAME
```

> [!NOTE]
> You can set properties in two ways:
>
> - on the command-line: `-Pzowe.deploy.username=$USERNAME -Pzowe.deploy.password=$PASSWORD`
> - in `~/.gradle/gradle.properties`

> [!WARNING]
> Do not commit exact properties to the GitHub repository. This is a confidential information. Properties used during the **GitHub Release Workflow** are stored securely under **GitHub Secrets**.

### Release SNAPSHOT artifacts

```shell
./gradlew publishAllVersions
```

### Release final artifacts

```shell
./gradlew release -Prelease.useAutomaticVersion=true # new patch
./gradlew release -Prelease.useAutomaticVersion=true -Prelease.scope=patch # new patch
./gradlew release -Prelease.useAutomaticVersion=true -Prelease.scope=minor # new minor
./gradlew release -Prelease.useAutomaticVersion=true -Prelease.scope=major # new major
```

### Release artifacts with a custom version

```shell
./gradlew release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=0.0.0 -Prelease.newVersion=1.1.0-SNAPSHOT
```

## Build from sources

TODO: complete
Install Rust and Java
then run:
```shell
./gradlew build
```
