package info.schnatterer.nusic.db.model;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.R;

import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Release implements Entity {
	private static final int DEFAULT_ARTWORK = R.drawable.ic_launcher;

	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";
	private static final String MUSIC_BRAINZ_BASE_URI = "musicbrainz.org/release/";
	private static final String MUSIC_BRAINZ_BASE_URI_HTTP = HTTP
			+ MUSIC_BRAINZ_BASE_URI;
	private static final String MUSIC_BRAINZ_BASE_URI_HTTPS = HTTPS
			+ MUSIC_BRAINZ_BASE_URI;

	private Long id;
	private String musicBrainzId;

	private Artist artist;
	private String releaseName;
	private Date releaseDate;
	private Date dateCreated;
	// private ? releaseType;
	private Bitmap artwork = null;
	private String artworkPath = null;

	public Release() {
	}

	public Release(Date dateCreated) {
		setDateCreated(dateCreated);
	}

	public Bitmap getArtwork() {
		if (artwork == null) {
			return BitmapFactory.decodeResource(Application.getContext()
					.getResources(), DEFAULT_ARTWORK);
		}
		return artwork;
	}

	public void setArtwork(Bitmap artwork) {
		this.artwork = artwork;
	}

	public String getArtworkPath() {
		return artworkPath;
	}

	public void setArtworkPath(String artworkPath) {
		this.artworkPath = artworkPath;
	}

	public String getArtistName() {
		return artist.getArtistName();
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public String getReleaseName() {
		return releaseName;
	}

	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result
				+ ((releaseName == null) ? 0 : releaseName.hashCode());
		result = prime * result
				+ ((releaseDate == null) ? 0 : releaseDate.hashCode());
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
		Release other = (Release) obj;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (releaseName == null) {
			if (other.releaseName != null)
				return false;
		} else if (!releaseName.equals(other.releaseName))
			return false;
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		} else if (!releaseDate.equals(other.releaseDate))
			return false;
		return true;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	protected void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		return "Release [artist=" + artist.getArtistName() + ", releaseName="
				+ releaseName + ", releaseDate=" + releaseDate
				+ ", dateCreated=" + dateCreated + ", thumbnail=" + artwork
				+ "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMusicBrainzId() {
		return musicBrainzId;
	}

	public void setMusicBrainzId(String musicBrainzId) {
		this.musicBrainzId = musicBrainzId;
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
