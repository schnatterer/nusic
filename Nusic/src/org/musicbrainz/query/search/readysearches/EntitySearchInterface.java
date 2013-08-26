package org.musicbrainz.query.search.readysearches;

import java.util.List;

public interface  EntitySearchInterface  {

    public List  getFullList();
    public List  getFirstPage();
    public List  getNextPage();

    public boolean hasMore();
}
