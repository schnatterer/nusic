nusic - your new music
=====
Never again miss a new album release of your favorite artists - always stay informed by nusic.

<img alt="nusic icon" src="https://raw.githubusercontent.com/schnatterer/nusic/develop/resources/ic_launcher_highres.png" width="200" height="200" />


nusic uses [MusicBrainz](http://musicbrainz.org/) - the free music encyclopedia - to find out about new releases of the artists on your phone.

No account necessary.

Note that this app is not optimized for tablets, yet. Please be patient.

Please report any issues [here] (https://github.com/schnatterer/nusic/issues).

## Download
You can download the lates version as APK from [GitHub] (https://github.com/schnatterer/nusic/releases/latest) or get the app directly from [Google Play](https://play.google.com/store/apps/details?id=info.schnatterer.nusic).

[![Google Play Logo](https://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=info.schnatterer.nusic)

## Licenses
Copyright © 2013 Johannes Schnatterer.

Licensed under the GNU General Public License Version 3.
See the [License](https://github.com/schnatterer/nusic/blob/master/Nusic/LICENSE.txt) and the [licenses of dependencies](https://github.com/schnatterer/nusic/blob/master/Nusic/NOTICE.txt).

## Release notes
See [Releases] (https://github.com/schnatterer/nusic/releases).

## Build
### Modules
| Module             | Packaging      | Description   |
| -------------      | ------------- | ------------- |
| parent             | pom | Global build properties for all modules |
| nusic-apk          | apk | Assembly maven module containing the guice module, wires up all dependencies and builds the APK |
| nusic-ui-android   | aar | Contains all android specific frontend-code |
| nusic-core-api     | jar | Interfaces of the central logic module |
| nusic-core-android | jar | Android-specific implementation of the central logic module |
| nusic-data-api     | jar | Interfaces of the persistence module |
| nusic-data-android | jar | Android-specific implementation of the persistence module |
| nusic-domain       | jar | Domain objects for persistence and migration between the layers |
| nusic-util         | jar | Utility module, containing logic common to all modules |

### Maven
In order to build the APK use the [SDK manager](https://developer.android.com/tools/help/sdk-manager.html) to download the SDK Version specified in the [parent POM's properties](https://github.com/schnatterer/nusic/blob/develop/pom.xml) and deploy android to your local maven repo using [maven-android-sdk-deployer](https://github.com/mosabua/maven-android-sdk-deployer). Also make sure to set your `ANDROID_HOME` environment variable to `sdk` folder of your Android SDK.
Then just run
    mvn install
Or if you want to deploy to your devices (vie ADB):
    mvn install android:deploy android:run
    
Note: During the build, the APK is signed using a keystore. In order not to hard-code any credentials, it uses variables.
You could pass those via the command line or define them in your ~/.m2/settings.xml like so:
```xml
<profile>
 <id>keystore</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
        <sign.keystore>FULL/path/to/keystore</sign.keystore>
        <sign.alias>the key's alias within the keystore</sign.alias>
        <sign.keypass>password for keystore</sign.keypass>
        <sign.storepass>password for key</sign.storepass>
    </properties>
</profile>
```

### Eclipse
* When using [m2eclipse](http://eclipse.org/m2e/) and [m2e-android](http://rgladwell.github.io/m2e-android/) and have your local maven repo set up (see above), maven and m2e should set you up with all you need to build an run right away.
* For setting up android libs, add nuisc-ui-android as library to nusic-apk and android-support-v7-appcompat from ANDROID_HOME/extras/android/support/v7/appcompat.
* In order to be able to use RoboBlender properly, add a source folder that points to `target/generated-sources/annotations` to each android-module (nusic-data-android, nusic-core-android, nusic-ui-android). Not doing this will most likely result in a `java.lang.ClassNotFoundException: AnnotationDatabaseImpl` when starting the app.
* For running robolectric tests from eclipse, you need to add the robolectric libraries (robolectric*.jar and android_all*.jar) to the modules (e.g. nusic-data-android). Those have been removed from the maven build of m2e because m2e doesn't know about scopes and and adding android_all*.jar to the APK will dramatically slow down the eclipse build or even fail it (exceed the 65K limit).

## Permissions
What kind of permission does nusic require and why does it require them?
- Network communication, full network access: Check MusicBrainz for new releases
- Nework communication, view network connections: Get notified about available connection to the internet in order to start checking for new releases.
- Yor applications information, run at startup: Schedule regular checking for new releases via the Android alarm manager
- System tools, test access to protected storage: Get the artists that are stored on the device
- Affects Battery, prevent phone from sleeping: Prevent the device from falling back to sleep while searching for new releases
