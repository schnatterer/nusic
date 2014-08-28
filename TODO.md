TODOs - Features, enhancements, refactoring, ...
- Remove coverartarchive-api subtree in favor of v.2.1.0 from maven.org

- db.loader package belongs to ui.loaders. The logic and DB access belongs to ReleaseService! Define explicit loader IDs in Constants as enum (then use ordinal())

- Remove Preference "Show future releases"
- Don't show releases that are older than the range in preferences

- Feature: Info dialog

=== Release v1.0


................... More TODOs
- Download and display disambiguation, e.g >20th Anniversary Edition< https://musicbrainz.org/ws/2/release/?limit=100&offset=0&query=type%3Aalbum+AND+date%3A[2014-02-20+TO+%3F]+AND+artist%3A%22%3Cpantera%3E%22
- Add SCM info to pom(s)
LOGGING 
- Switch off logging for artwork. Using logback-android via slf4j and config in assets/logback.xml?
- https://github.com/tony19/logback-android#configuration-in-code
- Switch app to use slf4j?
- Provide means to send log via email?

- Covers: Download using HTTPS. Problem with certificate chain at coverartarchive.org ONLY on android. In addition, links to images are HTTP. Force using HTTPS?

- Use the same algorithm for displaying notifations for loadNewReleasesService as in released today service: When only one album is found display infos and cover, when several are found show only nusic icon and the amount of albums
- Show artwork in context menu (on long tab)

- Refactoring: Mavenize test project
- Implement an Artwork Entity using a "proxy" that takes care of the writing from/to FS?
- Replace today's date with "today"? How to refresh at midnight?
- Use WeakReferences in ReleaseRefreshService in order to allow longer time ranges
- Display additional album info like "remastered special edition", "anniversary edition", etc. or use only the "oldest" release in a release group
- Display MusicBrainz side in internal Webview -> Better usability
- Feature: Tablet optimization: Layout + Screens for 7" + 10"

.....Build
- Refactoring: Maven Build: update manifest
- Refactoring: Maven Use profiles for release (signing...)
- Refactor testing: Maven? JUnit4, separtion of "normal" and android tests?

....Stores
- Stores: Publish on F-Droid
- Stores: "Google Play: Feature" Graphic
- Stores: Publish on Amazon: https://developer.amazon.com/welcome.html


.... Even more ideas
- Feature: Clean up DB, remove older entries or remove dateRelease in order to not display them anymore in app.
- Feature: Kind of releases: Album, Release, Live ...
- Feature: Error reporter; Or a simpler workaround: Log errors to SD (microlog4android? http://stackoverflow.com/a/13479675/1845976)
- Feature: Check connectivity while refreshing and cancel with error when lost: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
- Feature: Show number of artists and releases, show date of last refresh (in status/statistik dialog?)
- Refactoring: Delete LoadNewServiceBinding and split its functionality to MainActivity and  ServiceConnection?
- Feature: Custom Notifications icons?
- Feature: Custom Notification: Line break in Warning: You might want to try refreshing manually
- Feature: Schedule frequence configurable via preferences (at the moment always once per day). When changed: Adapt interval.
- Feature: Preferences: Delete old releases
- Feature: Preference Time period: infinitely. TODO Refactor refreshing release to decrease memory consumption.

.... Lower priority ideas
- Feature: Update only when sync is enabled: http://stackoverflow.com/questions/11252876/check-if-sync-is-enabled-within-android-app
(- Fix: Connect to Service and Start Dialog when Service started itself (e.g. after boot: wait for service to start, then tap refresh)) 
- Fix: After service was killed: Continue from last artists instead of starting new



