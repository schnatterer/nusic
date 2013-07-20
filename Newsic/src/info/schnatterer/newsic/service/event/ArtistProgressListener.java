package info.schnatterer.newsic.service.event;

import info.schnatterer.newsic.model.Artist;

import java.util.List;

public interface ArtistProgressListener extends
		ProgressListener<Artist, List<Artist>> {

}
