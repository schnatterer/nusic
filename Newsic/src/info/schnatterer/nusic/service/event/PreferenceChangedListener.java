package info.schnatterer.nusic.service.event;

public interface PreferenceChangedListener {
	void onPreferenceChanged(String key, Object newValue);

}
