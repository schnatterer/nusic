package info.schnatterer.newsic.db.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Artist implements Entity {
	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";
	private static final String MUSIC_BRAINZ_BASE_URI = "musicbrainz.org/artist/";
	private static final String MUSIC_BRAINZ_BASE_URI_HTTP = HTTP
			+ MUSIC_BRAINZ_BASE_URI;
	private static final String MUSIC_BRAINZ_BASE_URI_HTTPS = HTTPS
			+ MUSIC_BRAINZ_BASE_URI;

	private Long id;
	private Long androidAudioArtistId;
	private String musicBrainzId;
	/**
	 * Artist name from android db
	 */
	private String artistName;
	private List<Release> releases = new LinkedList<Release>();
	private Date dateCreated;

	public Artist() {
	}

	public Artist(Date dateCreated) {
		setDateCreated(dateCreated);
	}

	public List<Release> getReleases() {
		return releases;
	}

	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public Release getNewestRelease() {
		if (releases != null && releases.size() > 0) {
			return releases.get(0);
		}
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long l) {
		this.id = l;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	protected void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		return "Artist [id=" + id + ", artistName=" + artistName
				+ ", releases=" + releases + ", dateCreated=" + dateCreated
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getMusicBrainzId() {
		return musicBrainzId;
	}

	public void setMusicBrainzId(String musicBrainzId) {
		this.musicBrainzId = musicBrainzId;
	}

	public Long getAndroidAudioArtistId() {
		return androidAudioArtistId;
	}

	public void setAndroidAudioArtistId(Long androidAudioArtistId) {
		this.androidAudioArtistId = androidAudioArtistId;
	}

	public String getMusicBrainzUri() {
		return MUSIC_BRAINZ_BASE_URI_HTTP + getMusicBrainzId();
	}

	public String getMusicBrainzUriHttps() {
		return MUSIC_BRAINZ_BASE_URI_HTTPS + getMusicBrainzId();
	}

	@Override
	public void prePersist() {
		if (dateCreated == null)
			setDateCreated(new Date());

	}
}
