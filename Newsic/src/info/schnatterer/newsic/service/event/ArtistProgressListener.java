package info.schnatterer.newsic.service.event;

import info.schnatterer.newsic.db.model.Artist;

public interface ArtistProgressListener extends
		ProgressListener<Artist, Void> {

}
