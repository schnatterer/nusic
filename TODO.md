TODOs - Features, enhancements, refactoring, ...

- Create a proper icon for status bar, that shows a CD not a white circle in android 5
- constants.xml -  translatable="false"
- Release?

- A new view that shows icon of the release and a link to MB on tap ? --> Remove webview

LOGGING 
- Provide means to send log via email?
- Output statistic after finishing daily update
- Manage to grab Logcat output from android to to SLF4J

- Copy images to /sdcard instead of /data: 
http://www.androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
http://developer.android.com/guide/topics/data/data-storage.html#filesExternal

- Show progress in a snack bar instead of the dialog?
- Show artwork in context menu (on long tab)

- Create internal structure diagram, displaying module dependencies with an appropriate open source tool
.....Build
- change digestalg?
- Restructure APK project to use maven default structure. Does this even work with eclipse?
- Insert Built timestamp to version
- Refactoring: Maven Use profiles for release (signing...)

- Covers: Download using HTTPS. Problem with certificate chain at coverartarchive.org ONLY on android. In addition, links to images are HTTP. Force using HTTPS?

- Implement an Artwork Entity using a "proxy" that takes care of the writing from/to FS?
- Replace today's date with "today"? How to refresh at midnight (http://stackoverflow.com/questions/4928570/need-to-know-when-its-a-new-day-i-e-when-the-time-is-000000)?
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



