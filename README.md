## Zowe client Kotlin SDK
This SDK covert zOSMF Rest API with Kotlin object oriented code using Retrofit. Zowe client Kotlin SDK will allow you to send http requests to your zOSMF.

## Installation
To install this library in your project use one of build tools like Maven, Gradle or Ant. Use the link below to get necessary artifacts.
https://zowe.jfrog.io/artifactory/libs-release/org/zowe/sdk/zowe-kotlin-sdk/

To set up the SDK as a dependency:
```xml
<dependency>
  <groupId>org.zowe.sdk</groupId>
  <artifactId>zowe-kotlin-sdk</artifactId>
  <version>{version}</version>
</dependency>
```

## Guide
In Zowe client Kotlin SDK you can find API classes. They can be used to send requests to zOSMF. Besides API classes there located data classes like Dataset. Their purpose is to wrap a response from the server or a request into it using an object model. let's look at an example.
```kotlin
// Create stub for DataAPI interface using Retrofit. Here baseUrl is url of your zOSMF service.
val dataAPI = Retrofit.Builder()
  .baseUrl(baseUrl)
  .addConverterFactory(GsonConverterFactory.create())
  .client(client)
  .build()
  .create(DataAPI::class.java)

// You can use basic authentication to access zOSMF.
val basicCreds = Credentials.basic(zosmfUser, zosmfPassword) ?: ""

// Call method you need and get Request object.
val request = dataAPI.listDatasetMembers(
  authorizationToken = basicCreds,
  datasetName = "EXAMPLE.DATASET"
)

// Execute request to get Response object.
val response = request.execute()

// If the request went well you can get result from response body.
if (response.isSuccessful){
  val members = response.body();
}
```
Please note that in order to create API stub, you have to specify that the response should be converted by gson. And that's how you can easily use Zowe client Kotlin SDK.

## How to run tests

### Unit tests
To run unit tests:
```
./gradlew test
```
### Integration tests
**NOTE:** integration tests use a specific environment. To test their correctness, you need either create the compliant one, or change the tests

Before running integration tests, you need three variables to be set up:
- ``ZOSMF_TEST_URL`` - URL of the real mainframe with z/OSMF API to run the tests
- ``ZOSMF_TEST_USERNAME`` - username with appropriate permissions to run the tests
- ``ZOSMF_TEST_PASSWORD`` - user password to run the tests

To run integration tests:
```
./gradlew intTest
```

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

## Properties

- `zowe.deploy.username` and `zowe.deploy.password` - credentials to [Zowe Artifactory](https://zowe.jfrog.io/)

You can set properties in two ways:

- on the command-line: `-Pzowe.deploy.username=$USERNAME -Pzowe.deploy.password=$PASSWORD`
- in `~/.gradle/gradle.properties`

**Warning!** Do not commit property changes to the Git repository. This is confidential information.

Properties are stored in GitHub Secrets.
