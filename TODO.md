TODOs - Features, enhancements, refactoring, ...

TODO Enable annotation database to improve performance.

- Modules: apk (-> Roboguice Module), ui-android

- Introduce license maven plugin
- Remove musicbrainz code in favor of linking a github maven repo

LOGGING 
- Config SLF4J in order to get log statements from modules
- Switch off logging for artwork. Using logback-android via slf4j and config in assets/logback.xml?
- https://github.com/tony19/logback-android#configuration-in-code
- Switch app to use slf4j?
- Provide means to send log via email?

- Add section about modules to README 
- Create a proper icon for status bar, that shows a CD not a white circle in android 5

- Introduce material design, remove actionbarsherlock

- Copy images to /sdcard instead of /data: 
http://www.androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
http://developer.android.com/guide/topics/data/data-storage.html#filesExternal

- Show artwork in context menu (on long tab)
- Resources XXXHDPI?

.....Build
- Restructure APK project to use maven default structure
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



