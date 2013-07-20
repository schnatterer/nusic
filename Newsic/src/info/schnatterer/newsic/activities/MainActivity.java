package info.schnatterer.newsic.activities;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.adapters.ReleaseListAdapter;
import info.schnatterer.newsic.model.Artist;
import info.schnatterer.newsic.tasks.LoadNewRelasesTask;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	// private LoadNewRelasesTask loadNewRelasesTask = null;
	// private List<Artist> releases = new LinkedList<Artist>();

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
				Artist artist = (Artist) o;
				Application.toast("Selected :" + " " + artist.getArtistName());
			}

		});

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

	public void setReleases(List<Artist> result) {
		if (releasesListView != null) {
			releasesListView.setAdapter(new ReleaseListAdapter(this, result));
		}
	}
}
