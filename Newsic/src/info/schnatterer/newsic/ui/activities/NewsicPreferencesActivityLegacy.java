package info.schnatterer.newsic.ui.activities;

import info.schnatterer.newsic.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class NewsicPreferencesActivityLegacy extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
