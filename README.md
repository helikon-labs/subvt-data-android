<p align="center">
	<img width="400" src="https://raw.githubusercontent.com/helikon-labs/subvt/main/assets/design/logo/subvt_logo_blue.png">
</p>

![](https://github.com/helikon-labs/subvt-data-android/actions/workflows/gradle_lint_and_test.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=helikon-labs_subvt-data-android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=helikon-labs_subvt-data-android)
[![Release](https://jitpack.io/v/helikon-labs/subvt-data-android.svg)](https://jitpack.io/#helikon-labs/subvt-data-android)

# SubVT Data Access for Android

SubVT (Substrate Validator Toolkit) data access library for Android.

Please visit the [top-level Subvt repository](https://github.com/helikon-labs/subvt) for project
information.

## Test & Build

- Rename the file `subvt.properties.sample` in the root folder to `subvt.properties`, and edit
  the file contents with the service host and port details.
- Run `./gradlew lintDebug` in the folder for lint checks.
- Run `./gradlew test` for complete unit tests.
- Run `./gradlew connectedCheck` for intrumented tests (only application service for now).
- Run `./gradlew build` to build.

## Installation

1. Add the JitPack repository in your root-level `settings.gradle` file:

    ```gradle
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            jcenter() // Warning: this repository is going to shut down soon
            // add the JitPack repository here
            maven { url 'https://jitpack.io' }
        }
    }
    ```

2. Add the dependency:

    ```gradle
    dependencies {
        // ...
        implementation 'com.github.helikon-labs:subvt-data-android:0.1.2'
        // ...
    }
    ```

## Usage

Please refer to the [unit](https://github.com/helikon-labs/subvt-data-android/tree/main/subvt-data/src/test/java/io/helikon/subvt/data)
and [instrumented](https://github.com/helikon-labs/subvt-data-android/tree/main/subvt-data/src/androidTest/java/io/helikon/subvt/data) tests for information about how to use the
[report](https://github.com/helikon-labs/subvt-data-android/blob/main/subvt-data/src/test/java/io/helikon/subvt/data/ReportServiceTest.kt)
and [application](https://github.com/helikon-labs/subvt-data-android/blob/main/subvt-data/src/androidTest/java/io/helikon/subvt/data/AppServiceInstrumentedTest.kt) REST services,
and [network status](https://github.com/helikon-labs/subvt-data-android/blob/main/subvt-data/src/test/java/io/helikon/subvt/data/NetworkStatusServiceTest.kt),
[active/inactive validator list](https://github.com/helikon-labs/subvt-data-android/blob/main/subvt-data/src/test/java/io/helikon/subvt/data/ValidatorListServiceTest.kt) and
[validator details](https://github.com/helikon-labs/subvt-data-android/blob/main/subvt-data/src/test/java/io/helikon/subvt/data/ValidatorDetailsServiceTest.kt) RPC pub/sub services.