TODOs - Features, enhancements, refactoring, ...

MODERNIZATION 
- Remove musicbrainz code in favor of linking a github maven repo
- Introduce license maven plugin
- Switch to newest android SDK version?
- Java7?
- change digestalg?
- Create a proper icon for status bar, that shows a CD not a white circle in android 5
- Output statistic after finishing daily update

LOGGING 
- Provide means to send log via email?
- Manage to grab Logcat output from android to to SLF4J

- Introduce material design, remove actionbarsherlock
http://www.grokkingandroid.com/migrating-actionbarsherlock-actionbarcompat/
http://android-developers.blogspot.de/2014/10/appcompat-v21-material-design-for-pre.html

- Copy images to /sdcard instead of /data: 
http://www.androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
http://developer.android.com/guide/topics/data/data-storage.html#filesExternal

- Show artwork in context menu (on long tab)
- Resources XXXHDPI?

- Create internal structure diagram, displaying module dependencies with an appropriate open source tool
.....Build
- Restructure APK project to use maven default structure. Does this even work with eclipse?
- Insert Built timestamp to version
- Refactoring: Maven Use profiles for release (signing...)

- Covers: Download using HTTPS. Problem with certificate chain at coverartarchive.org ONLY on android. In addition, links to images are HTTP. Force using HTTPS?

- Implement an Artwork Entity using a "proxy" that takes care of the writing from/to FS?
- Replace today's date with "today"? How to refresh at midnight?
- Use WeakReferences in ReleaseRefreshService in order to allow longer time ranges

- Feature: Tablet optimization: Layout + Screens for 7" + 10"

....Stores
- Stores: Publish on F-Droid
- Stores: Publish on Amazon: https://developer.amazon.com/welcome.html


.... Even more ideas
- Feature: Kind of releases: Album, Release, Live ...
- Feature: Check connectivity while refreshing and cancel with error when lost: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
- Feature: Error reporter; Or a simpler workaround: Log errors to SD (microlog4android? http://stackoverflow.com/a/13479675/1845976)
- Feature: Show number of artists and releases, show date of last refresh (in status/statistic dialog?)
- Feature: Clean up DB, remove older entries
- Refactoring: Delete LoadNewServiceBinding and split its functionality to MainActivity and  ServiceConnection?
- Feature: Schedule frequence configurable via preferences (at the moment always once per day). When changed: Adapt interval.
- Feature: Preference Time period: infinitely. TODO Refactor refreshing release to decrease memory consumption.

.... Lower priority ideas
- Feature: Update only when sync is enabled: http://stackoverflow.com/questions/11252876/check-if-sync-is-enabled-within-android-app
(- Fix: Connect to Service and Start Dialog when Service started itself (e.g. after boot: wait for service to start, then tap refresh)) 
- Fix: After service was killed: Continue from last artists instead of starting new



