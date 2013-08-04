package info.schnatterer.newsic.service.event;

public interface PreferenceChangedListener {
	void onPreferenceChanged(String key, Object newValue);

}
