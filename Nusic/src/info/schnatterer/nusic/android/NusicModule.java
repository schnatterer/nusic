package info.schnatterer.nusic.android;

import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.core.impl.ReleaseServiceImpl;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.dao.ReleaseDao;
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
public class NusicModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(PreferencesService.class).to(
				PreferencesServiceSharedPreferences.class);
		bind(ReleaseService.class).to(ReleaseServiceImpl.class);
		bind(ReleaseDao.class).to(ReleaseDaoSqlite.class);
		bind(ArtistDao.class).to(ArtistDaoSqlite.class);
	}

	@Provides
	Context provideGlobalContext() {
		return NusicApplication.getContext();
	}
}
