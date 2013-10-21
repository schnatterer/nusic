nusic - your new music
=====
Never again miss a new album release of your favorite artists - always stay informed by nusic.
nusic uses [MusicBrainz](http://musicbrainz.org/) - the free music encyclopedia - to find out about new releases of the artists on your phone.
No account necessary.
Note that this app is not optimized for tablets, yet. Please be patient.

Please report any issues [here] (https://github.com/schnatterer/nusic/issues).

## Download
You can download the APK from [GitHub] (https://schnatterer.github.com/nusic/release/current) or from [Google Play](https://play.google.com/store/apps/details?id=info.schnatterer.nusic)
[![Google Play Logo](https://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=info.schnatterer.nusic)

## Licenses
Copyright © 2013 Johannes Schnatterer. Licensed under the GNU General Public License Version 3.
See the [License](https://github.com/schnatterer/nusic/blob/master/Nusic/LICENSE.txt) and the [licenses of dependencies](https://github.com/schnatterer/nusic/blob/master/Nusic/NOTICE.txt)

## Release notes
See [Releases] (https://github.com/schnatterer/nusic/releases)

## Build
### Maven
In order to build the APK use the [SDK manager](https://developer.android.com/tools/help/sdk-manager.html) to download the SDK Version specified in the [POM](https://github.com/schnatterer/nusic/blob/master/Nusic/pom.xml) and deploy android and the the support API to your local maven repo using [maven-android-sdk-deployer](https://github.com/mosabua/maven-android-sdk-deployerjust).
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
When using [m2eclipse](http://eclipse.org/m2e/) and [m2e-android](http://rgladwell.github.io/m2e-android/) and have your local maven repo set up (see above), maven and m2e should set you up with all you need to build an run right away.
