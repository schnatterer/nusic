package info.schnatterer.nusic.android;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.core.ArtistService;
import info.schnatterer.nusic.core.ConnectivityService;
import info.schnatterer.nusic.core.DeviceMusicService;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.RemoteMusicDatabaseService;
import info.schnatterer.nusic.core.SyncReleasesService;
import info.schnatterer.nusic.core.impl.ArtistServiceImpl;
import info.schnatterer.nusic.core.impl.ConnectivityServiceAndroid;
import info.schnatterer.nusic.core.impl.DeviceMusicServiceAndroid;
import info.schnatterer.nusic.core.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.core.impl.ReleaseServiceImpl;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz.ApplicationContact;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz.ApplicationName;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz.ApplicationVersion;
import info.schnatterer.nusic.core.impl.SyncReleasesServiceImpl;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.dao.ArtworkDao;
import info.schnatterer.nusic.data.dao.ReleaseDao;
import info.schnatterer.nusic.data.dao.fs.ArtworkDaoFileSystem;
import info.schnatterer.nusic.data.dao.sqlite.ArtistDaoSqlite;
import info.schnatterer.nusic.data.dao.sqlite.ReleaseDaoSqlite;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Google guice {@link com.google.inject.Module} that configures the bindings
 * service interfaces and the implementing classes. The information provided by
 * this class is used by guice when resolving {@link javax.inject.Inject}
 *
 * @author schnatterer
 *
 */
public class NusicAndroidModule extends AbstractModule {
	private Context context;

	public NusicAndroidModule(Context context) {
		this.context = context;
	}

	@Override
	protected void configure() {
		// Services
		bind(ArtistService.class).to(ArtistServiceImpl.class);
		bind(ConnectivityService.class).to(ConnectivityServiceAndroid.class);
		bind(DeviceMusicService.class).to(DeviceMusicServiceAndroid.class);
		bind(PreferencesService.class).to(
				PreferencesServiceSharedPreferences.class);
		bind(ReleaseService.class).to(ReleaseServiceImpl.class);
		bind(RemoteMusicDatabaseService.class).to(
				RemoteMusicDatabaseServiceMusicBrainz.class);
		bind(SyncReleasesService.class).to(SyncReleasesServiceImpl.class);

		// DAOs
		bind(ReleaseDao.class).to(ReleaseDaoSqlite.class);
		bind(ArtistDao.class).to(ArtistDaoSqlite.class);
		bind(ArtworkDao.class).to(ArtworkDaoFileSystem.class);

		// Resources
		bind(String.class).annotatedWith(ApplicationName.class).toInstance(
				provideGlobalContext().getString(R.string.app_name));
		bind(String.class).annotatedWith(ApplicationVersion.class).toInstance(
				NusicApplication.getCurrentVersionName());
		bind(String.class).annotatedWith(ApplicationContact.class).toInstance(
				Constants.APPLICATION_URL);
	}

	@Provides
	Context provideGlobalContext() {
		return context;
	}
}
