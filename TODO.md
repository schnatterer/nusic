TODOs - Features, enhancements, refactoring, ...

- Modules: apk (-> Roboguice Module), ui-android, service-android, service-api
- Separate Maven Modules with API and impl: ServiceException: How to get rid of android dependency -> getLocalizedMessage only returns a KEY!?
How to get rid of LocalMusicService in contentResolver? Factory that provides Context so Impl gets it injected and API can be generic?
- Restructure: Don't use static contexts, but INJECT
- Add section about modules to README 

- Copy images to /sdcard instead of /data: 
http://www.androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
http://developer.android.com/guide/topics/data/data-storage.html#filesExternal



LOGGING 
- Switch off logging for artwork. Using logback-android via slf4j and config in assets/logback.xml?
- https://github.com/tony19/logback-android#configuration-in-code
- Switch app to use slf4j?
- Provide means to send log via email?
- Construction of objects. Use DI? Or more singletons to save a bit of memory?

- Show artwork in context menu (on long tab)
- Resources XXXHDPI?

- Introduce material design, remove actionbarsherlock

- Covers: Download using HTTPS. Problem with certificate chain at coverartarchive.org ONLY on android. In addition, links to images are HTTP. Force using HTTPS?

- Implement an Artwork Entity using a "proxy" that takes care of the writing from/to FS?
- Replace today's date with "today"? How to refresh at midnight?
- Use WeakReferences in ReleaseRefreshService in order to allow longer time ranges

- Feature: Tablet optimization: Layout + Screens for 7" + 10"

.....Build
- Change folder hierarchy to default maven (src/main/...)
- Refactoring: Mavenize test project
- Insert Built timestamp to version
- Refactoring: Maven Use profiles for release (signing...)

....Stores
- Stores: Publish on F-Droid
- Stores: Publish on Amazon: https://developer.amazon.com/welcome.html


.... Even more ideas
- Feature: Kind of releases: Album, Release, Live ...
- Feature: Check connectivity while refreshing and cancel with error when lost: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
- Feature: Error reporter; Or a simpler workaround: Log errors to SD (microlog4android? http://stackoverflow.com/a/13479675/1845976)
- Feature: Show number of artists and releases, show date of last refresh (in status/statistik dialog?)
- Feature: Clean up DB, remove older entries
- Refactoring: Delete LoadNewServiceBinding and split its functionality to MainActivity and  ServiceConnection?
- Feature: Schedule frequence configurable via preferences (at the moment always once per day). When changed: Adapt interval.
- Feature: Preference Time period: infinitely. TODO Refactor refreshing release to decrease memory consumption.

.... Lower priority ideas
- Feature: Update only when sync is enabled: http://stackoverflow.com/questions/11252876/check-if-sync-is-enabled-within-android-app
(- Fix: Connect to Service and Start Dialog when Service started itself (e.g. after boot: wait for service to start, then tap refresh)) 
- Fix: After service was killed: Continue from last artists instead of starting new



