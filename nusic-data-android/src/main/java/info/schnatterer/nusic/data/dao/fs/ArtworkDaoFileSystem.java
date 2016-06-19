/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic.
 *
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.data.dao.fs;

import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.dao.ArtworkDao;
import info.schnatterer.nusic.data.model.Release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import android.content.Context;

public class ArtworkDaoFileSystem implements ArtworkDao {
    public static final String FILE_SCHEME = "file://";
    public static final String BASEDIR_PATH = "artwork";

    @Inject
    private Context context;

    /** path to /data/data/../app_data/.. */
    private File BASEDIR;

    @Inject
    private void init() {
        BASEDIR = context.getDir(BASEDIR_PATH, Context.MODE_PRIVATE);
    }

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
    public boolean exists(Release release, ArtworkType type)
            throws DatabaseException {
        if (release.getMusicBrainzId() == null) {
            return false;
        }

        return new File(BASEDIR, createFileName(release, type)).exists();
    }

    @Override
    public String findUriByRelease(Release release, ArtworkType type)
            throws DatabaseException {
        File possibleArtwork;
        try {
            possibleArtwork = new File(BASEDIR, createFileName(release, type));
            if (!possibleArtwork.exists()) {
                return null;
            }
            return FILE_SCHEME + BASEDIR + '/' + createFileName(release, type);
            // return possibleArtwork.toURI().toString(); // returns only
            // file:<path> but we want file://<path>
        } catch (DatabaseException e) {
            throw new DatabaseException(
                    "Unable to read artwork from file system", e);
        }
    }

    @Override
    public InputStream findStreamByRelease(Release release, ArtworkType type)
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
