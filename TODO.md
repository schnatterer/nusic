TODOs - Features, enhancements, refactoring, ...

- Feature: Swipe between Tabs
- Feature: Info dialog
- Feature: Tabs: Available, Announced
- Feature: Use WeakReferences to improve performance while scrolling list: http://stackoverflow.com/questions/3243215/how-to-use-weakreference-in-java-and-android-development
=== RELEASE v.0.5

- Feature: Query and display album covers: Last fm? (See Apollo Music player) or Discogs? Store covers in sqlite oder in file system? Get Last.FM ID/Discogs ID from MusicBrainz?
=== Release v1.0


................... More TODOs
- Refactor testing: Maven? JUnit4, separtion of "normal" and android tests?
- Feature: Tablet optimization: Layout + Screens for 7" + 10"

.....Build
- Refactoring: Optimize Maven build: parent,  app,  test
- Refactoring: Maven build: Extract MusicBrainz lib into separate maven project. Separate GitHub repo?
- Refactoring: Maven Build: update manifest
- Refactoring: Maven Use profiles for release (signing...)

....Stores
- Stores: Publish on F-Droid
- Stores: "Google Play: Feature" Graphic
- Stores: Publish on Amazon: https://developer.amazon.com/welcome.html


.... Even more ideas
- Feature: Kind of releases: Album, Release, Live ...
- Feature: Error reporter
- Feature: Check connectivity while refreshing and cancel with error when lost: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
- Feature: Show number of artists and releases, show date of last refresh (in status/statistik dialog?)
- Refactoring: Delete LoadNewServiceBinding and split its functionality to MainActivity and  ServiceConnection?
- Feature: Custom Notifications icons?
- Feature: Custom Notification: Line break in Warning: You might want to try refreshing manually
- Feature: Schedule frequence configurable via preferences (at the moment always once per day). When changed: Adapt interval.
- Feature: Preferences: Delete old releases
- Feature: Preference Time period: infinitely. TODO Refactor refreshing release to decrease memory consumption.

.... Lower priority ideas
- Feature: Use HTTPS?
- Feature: Update only when sync is enabled: http://stackoverflow.com/questions/11252876/check-if-sync-is-enabled-within-android-app
(- Fix: Connect to Service and Start Dialog when Service started itself (e.g. after boot: wait for service to start, then tap refresh)) 
- Fix: After service was killed: Continue from last artists instead of starting new



