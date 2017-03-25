nusic - your new music
=====

| Branch        | Build Status  | Quality Gate |
| ------------- |-------------  | ------------ |
| Develop       | [![Build Status Develop](https://jenkins.schnatterer.info/job/nusic-develop/badge/icon)](https://jenkins.schnatterer.info/job/nusic-develop/)  |[![Quality Gates Develop](https://sonarqube.schnatterer.info/api/badges/gate?key=info.schnatterer.nusic:nusic-develop)](https://sonarqube.schnatterer.info/dashboard/index/279?did=1) |
| Features       | [![Build Status Features](https://jenkins.schnatterer.info/job/nusic-features/badge/icon)](https://jenkins.schnatterer.info/job/nusic-features/)  | |

  [![License](https://img.shields.io/github/license/schnatterer/nusic.svg)](LICENSE.txt)
  [<img alt="powered by openshift" align="right" src="https://www.openshift.com/images/logos/powered_by_openshift.png"/>](https://www.openshift.com/)
  
Never again miss a new album release of your favorite artists - always stay informed by nusic.

<img alt="nusic icon" src="https://raw.githubusercontent.com/schnatterer/nusic/develop/resources/ic_launcher_highres.png" width="200" height="200" />


nusic uses [MusicBrainz](http://musicbrainz.org/) - the free music encyclopedia - to find out about new releases of the artists on your phone.

No account necessary.

Note that this app is not optimized for tablets, yet. Please be patient.

Please report any issues [here] (https://github.com/schnatterer/nusic/issues).

## Download
You can download the lates version as APK from [GitHub] (https://github.com/schnatterer/nusic/releases/latest) or get the app directly from [F-Droid](https://f-droid.org/app/info.schnatterer.nusic) or [Google Play](https://play.google.com/store/apps/details?id=info.schnatterer.nusic).

[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/app/info.schnatterer.nusic)

[<img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" height="60"/>](https://play.google.com/store/apps/details?id=info.schnatterer.nusic)

## Licenses
Copyright © 2013 Johannes Schnatterer.

Licensed under the GNU General Public License Version 3.
See the [License](LICENSE.txt) and the [licenses of dependencies](NOTICE.md).

## Release notes
See [Changelog](CHANGELOG.md) and [Releases] (https://github.com/schnatterer/nusic/releases).

## Permissions
What kind of permission does nusic require and why does it require them?
- Network communication, full network access: Check MusicBrainz for new releases
- Nework communication, view network connections: Get notified about available connection to the internet in order to start checking for new releases.
- Yor applications information, run at startup: Schedule regular checking for new releases via the Android alarm manager
- System tools, test access to protected storage: Get the artists that are stored on the device
- Affects Battery, prevent phone from sleeping: Prevent the device from falling back to sleep while searching for new releases


## Build
### Modules
| Module             | Packaging      | Description   |
| -------------      | ------------- | ------------- |
| parent             | - | Global build properties for all modules |
| nusic-apk          | apk | Assembly maven module containing the guice module, wires up all dependencies and builds the APK |
| nusic-ui-android   | aar | Contains all android specific frontend-code |
| nusic-core-api     | jar | Interfaces of the central logic module |
| nusic-core-android | jar | Android-specific implementation of the central logic module |
| nusic-data-api     | jar | Interfaces of the persistence module |
| nusic-data-android | jar | Android-specific implementation of the persistence module |
| nusic-domain       | jar | Domain objects for persistence and migration between the layers |
| nusic-util         | jar | Utility module, containing logic common to all modules |

### Gradle
In order to build the APK use the [SDK manager](https://developer.android.com/tools/help/sdk-manager.html) to download the SDK Version specified in the [parent project's build.gradle](build.gradle) and deploy android to your local maven repo using [maven-android-sdk-deployer](https://github.com/mosabua/maven-android-sdk-deployer). Also make sure to set your `ANDROID_HOME` environment variable to `sdk` folder of your Android SDK.
Then just run  
```sh
gradlew clean check assembleDebug
```
to compile from scratch, run the tests and create a debug-signed APK, or run  

```sh
gradlew clean check assembleRelease
```
to create a signed APK, using an custom keystore.  

For passing the credentials for this keystore via the command line there are four options  

1. Define them in your `~/.gradle/gradle.properties` like so  

   ```ini
   signKeystore=FULL/path/to/keystore
   signAlias=the key's alias within the keystore
   signKeypass=password for keystore
   signStorepasss=password for key
   ```
2. Pass them as command line properties, e.g.  

   ```sh
   gradle clean check assembleRelease -PsignAlias="the key's alias within the keystore"
   ```  
3. Pass them as environment variables, e.g.  

   ```sh
   export ORG_GRADLE_PROJECT_signAlias=the key's alias within the keystore
   ```
4. or pass them as system property, e.g.

   ```sh
   gradle clean check assembleRelease -Dorg.gradle.project.signKeystore=signAlias="the key's alias within the keystore"
   ```


## Creating a release
TODO Automate this, e.g. via Jenkins

- Start release  

   ```sh
   git-flow release start v.2.1.1
   ```
- Set Version  

   ```sh
   gradle setVersion -PnewVersion=2.1.1
   ```
- Update [changelog](CHANGELOG.md)
- Commit  

    ```sh
    git add .
    git commit -m 'Prepare release v.2.1.1'
    ```
- Finish release & Tag (+ tag message)

   ```sh
   git-flow release finish v.2.1.1
   ```
- Set next dev version & commit

    ```sh
    gradle setVersion -PnewVersion=2.1.2-SNAPSHOT
    git add .
    git commit -m "Prepare for next development iteration v.2.1.2-SNAPSHOT"
    ```
- Checkout and build tag

    ```sh
    git checkout tags/v.2.1.1
    gradle clean check assembleRelease
    ```
- Push all branches & tags

    ```sh
    git push --all
    git push --tags
    ```
- Update [F-Droid metadata](https://gitlab.com/fdroid/fdroiddata/blob/master/metadata/info.schnatterer.nusic.txt) by adding new release to the metadata (e.g. via [this fork](https://gitlab.com/schnatterer/fdroiddata)) and [creating a merge request](https://gitlab.com/schnatterer/fdroiddata/merge_requests/new) 
- Upload artifact: [Github](https://github.com/schnatterer/nusic/releases), [Google Play](https://play.google.com/apps/publish/)
- Add changelog to github release page: https://github.com/schnatterer/nusic/releases/tag/v.2.1.1
- Add changelog to google play entry
