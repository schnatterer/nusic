nusic Changelog
=========

**v.3.3.0** - 2018/05/21

* New logo
* Minor bug fixes, e.g. [#16](https://github.com/schnatterer/nusic/issues/16)


**v.3.2.0** - 2017/03/25

* Added Share button to Release view 


**v.3.1.0** - 2016/07/08

* Added Japanese translation (thanks, naofum!)
* Added contributors to "about" menu
* Added F-Droid link


**v.3.0.0** - 2016/06/19

* Updated android support libraries - fixing errors for legacy android versions
* Technical reconstructions (maven -> gradle)


**v.2.1.1** - 2016/06/01

* Android 6: Request Read Media Permissions at Run Time


**v.2.1.0** - 2016/01/03

* Improvements for android 6
* Clean up old log files in order to preserve storage on device


**v.2.0.0** - 2015/06/30

* Introduced material design
* Restructured preferences, added developer preferences
* Implemented possibility to send log files via email
* Completely restructured app under the hood, in order to allow for better maintainability in the future


**v.1.0.2** - 2015/05/20

* Fixed recurring release date notification

**v.1.0.1** - 2014/11/25

* Fixed a possible error when displaying notification about new releases, caused during image loading

**v.1.0 Cover art and notifications** - 2014/10/04

* Feature: Now showing cover art from Cover Art Archive
* Feautre: Get notified on the day of a release
* Added changelog, licenses, privacy policy as well as links to GitHub and Google Play and an email contact to preferences.
* Added welcome screen for new users and new versions
* Multiple minor enhancements/fixes

**v.0.6 New Tabs & UTC Fix** - 2014/10/04

* New Tabs for available and announced releases
* Bugfixes (Wrong release dates when changing timezones, error on daily refresh of releases)

**v.0.5.1 Encryption and fixes** - 2014/05/29

* Fixed an exception when hiding releases
* Fixed issue #1
* Enabled encryption (HTTPS) for communication with MusicBrainz

**v.0.5 Several minor enhancements** - 2014/05/10

* Added back button to action bar in preferences menu
* Implemented swiping between tabs
* Removed error notification when update failed for some artists

**v.0.4 Hide albums** - 2014/02/03

* Possibility to hide a single album or all ablums by a specific artist by long tapping an album.
* Hidden albums can be reset in the preferences.

**v.0.3.1 Android 2.x** - 2014/01/08

* Fix: App did not start on Android 2.x devices.

**v.0.3 Android 4.4** - 2013/11/28

* Fix: artists could not be read in Android 4.4

**v.0.2.2 Changed default values** - 2013/10/20

* Prepared launch on Google Play.
* Mobile internet is now used by default in order to avoid unexpected behavior by default.

**v.0.2.1: Bugfix: Duplicated releases** - 2013/10/17

* Fixed an issue where one release could appear several times.

**v.0.2: Improvements for background task** - 2013/10/15

* Minor improvements, especially regarding background service that keeps releases up-to-date

**v.0.1: First "official" version** - 2013/09/01

* Checks for new album releases for all artists on phone using MusicBrainz
* Releases are displayed in two tabs: "Recently added" (within the last two weeks) and "All"
* Refreshs of the releases are performed daily
* When no internet connection available, refresh is postponed
* Availability of new releases are shown as notifications
* Available preferences: Time period (releases), download future releases, incremental/full update, use only wifi

