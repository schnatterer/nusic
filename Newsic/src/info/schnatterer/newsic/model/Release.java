package info.schnatterer.newsic.model;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.R;

import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Release {
	private static final int DEFAULT_THUMBNAIL = R.drawable.ic_launcher;

	private Artist artist;
	private String releaseName;
	private Date releaseDate;
	private Date dateCreated;
	// private ? releaseType;
	private Bitmap thumbnail = null;

	public Release(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Release() {
		this(new Date());
	}

	public Bitmap getThumbnail() {
		if (thumbnail == null) {
			return BitmapFactory.decodeResource(
					Application.getDefaulResources(), DEFAULT_THUMBNAIL);
		}
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result
				+ ((releaseDate == null) ? 0 : releaseDate.hashCode());
		result = prime * result
				+ ((releaseName == null) ? 0 : releaseName.hashCode());
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
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		} else if (!releaseDate.equals(other.releaseDate))
			return false;
		if (releaseName == null) {
			if (other.releaseName != null)
				return false;
		} else if (!releaseName.equals(other.releaseName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Release [artist=" + artist + ", releaseName=" + releaseName
				+ ", releaseDate=" + releaseDate + ", dateCreated="
				+ dateCreated + ", thumbnail=" + thumbnail + "]";
	}

}
