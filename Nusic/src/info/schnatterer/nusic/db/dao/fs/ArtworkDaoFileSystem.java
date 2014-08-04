package info.schnatterer.nusic.db.dao.fs;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.dao.ArtworkDao;
import info.schnatterer.nusic.db.model.Release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import android.content.Context;

public class ArtworkDaoFileSystem implements ArtworkDao {
	// private static final int DEFAULT_ARTWORK = R.drawable.ic_launcher;
	public static final String BASEDIR_PATH = "artwork";

	// path to /data/data/../app_data/..
	public static final File BASEDIR = Application.getContext().getDir(
			BASEDIR_PATH, Context.MODE_PRIVATE);

	@Override
	public boolean save(Release release, ArtworkType type, InputStream artwork)
			throws DatabaseException {
		if (release.getMusicBrainzId() == null) {
			throw new DatabaseException(
					"Unable to save artwork, corresponding release does not have a musicbrainz ID: "
							+ release);
		}

		File output = new File(BASEDIR, createFileName(release, type));
		if (output.exists()) {
			return false;
		} else {
			try {
				FileUtils.copyInputStreamToFile(artwork, output);
				return true;
			} catch (IOException e) {
				throw new DatabaseException(
						"Unable to save artwork, error writing to file system."
								+ release, e);
			}
		}
	}

	private String createFileName(Release release, ArtworkType type)
			throws DatabaseException {
		switch (type) {
		case SMALL:
			return release.getMusicBrainzId() + "_S";
		default:
			throw new DatabaseException("Unimplemented artwork type" + type);
		}
	}

	@Override
	public boolean exists(Release release, ArtworkType type) throws DatabaseException {
		if (release.getMusicBrainzId() == null) {
			return false;
		}

		return new File(BASEDIR, createFileName(release, type)).exists();
	}

	@Override
	public InputStream findByRelease(Release release, ArtworkType type)
			throws DatabaseException {
		if (release.getMusicBrainzId() != null) {
			File possibleArtwork = new File(BASEDIR, createFileName(release,
					type));
			if (possibleArtwork.exists()) {
				try {
					return new FileInputStream(possibleArtwork);
				} catch (FileNotFoundException e) {
					throw new DatabaseException(
							"Unable to read artwork from file system", e);
				}
			}
		}
		return null;
	}
	// @Override
	// public InputStream findByRelease(Release release, ArtworkType type)
	// throws DatabaseException {
	// if (release.getMusicBrainzId() == null) {
	// /*
	// * TDO do we really want to load the resource all over again here?
	// * Or should some other instance keep a Bitmap of the icon in
	// * memory? Or just return null if none found so the caller can
	// * decide for himself?
	// */
	// return Application.getContext().getResources()
	// .openRawResource(DEFAULT_ARTWORK);
	// }
	//
	// File possibleArtwork = new File(BASEDIR, createFileName(release, type));
	// if (possibleArtwork.exists()) {
	// try {
	// return new FileInputStream(possibleArtwork);
	// } catch (FileNotFoundException e) {
	// throw new DatabaseException(
	// "Unable to read artwork from file system", e);
	// }
	// } else {
	// return Application.getContext().getResources()
	// .openRawResource(DEFAULT_ARTWORK);
	// }
	// }
}
