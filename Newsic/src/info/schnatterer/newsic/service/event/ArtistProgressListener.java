package info.schnatterer.newsic.service.event;

import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.db.model.Release;

import java.util.List;

public interface ArtistProgressListener extends
		ProgressListener<Artist, List<Release>> {

}
