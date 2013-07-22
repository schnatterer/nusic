package info.schnatterer.newsic.ui.activities;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.PreferencesService.AppStart;
import info.schnatterer.newsic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.newsic.ui.adapters.ReleaseListAdapter;
import info.schnatterer.newsic.ui.tasks.LoadNewRelasesTask;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	private static LoadNewRelasesTask asyncTask = null;

	private ListView releasesListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		releasesListView = (ListView) findViewById(R.id.releasesListView);
		releasesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = releasesListView.getItemAtPosition(position);
				Release release = (Release) o;
				Application.toast("Selected :" + " " + release.getArtistName()
						+ " - " + release.getReleaseName());
			}

		});
		
		AppStart appStart = PreferencesServiceSharedPreferences.getInstance()
				.checkAppStart();
		switch (appStart) {
		// case FIRST_TIME_VERSION:
		// break;
		case FIRST_TIME:
			firstAppStartEver();
		default:
			break;
		}
	}

	private void firstAppStartEver() {
		if (Application.isOnline()) {
			if (asyncTask == null) {
				// Async task not started yet
				asyncTask = new LoadNewRelasesTask(this, releasesListView);
				asyncTask.execute();
			} else {
				// Set activity as new context of task
				asyncTask.updateActivity(this, releasesListView);
				if (asyncTask.getResult() != null) {
					// If async task is already finished
					setReleases(asyncTask.getResult());
				}
			}
		} else {
			Application.toast(getString(R.string.Activity_notOnline));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (asyncTask != null) {
			// Preserve memory
			asyncTask.updateActivity(null, null);
		}
	}

	public void setReleases(List<Release> result) {
		if (releasesListView != null) {
			releasesListView.setAdapter(new ReleaseListAdapter(this, result));
		}
	}
}
