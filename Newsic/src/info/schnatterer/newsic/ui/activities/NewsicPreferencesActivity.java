package info.schnatterer.newsic.ui.activities;

import info.schnatterer.newsic.ui.fragments.NewsicPreferencesFragment;
import android.app.Activity;
import android.os.Bundle;

public class NewsicPreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new NewsicPreferencesFragment())
				.commit();
	}

}
