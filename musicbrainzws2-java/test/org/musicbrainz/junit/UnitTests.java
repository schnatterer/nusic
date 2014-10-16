/*
 * 
 * Some Use cases here that you could use as a reference in your application.
 */
package org.musicbrainz.junit;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.musicbrainz.model.entity.DiscWs2;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.controller.Artist;
import org.musicbrainz.controller.Collection;
import org.musicbrainz.controller.Disc;
import org.musicbrainz.controller.Label;
import org.musicbrainz.controller.Puid;
import org.musicbrainz.controller.Recording;
import org.musicbrainz.controller.Release;
import org.musicbrainz.controller.ReleaseGroup;
import org.musicbrainz.controller.Work;

import org.musicbrainz.filter.ReleaseTypeFilterWs2;

import org.musicbrainz.model.DiscTrackWs2;
import org.musicbrainz.model.PuidWs2;
import org.musicbrainz.model.TagWs2;
import org.musicbrainz.model.entity.ArtistWs2;
import org.musicbrainz.model.entity.CollectionWs2;
import org.musicbrainz.model.entity.LabelWs2;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.model.searchresult.ArtistResultWs2;
import org.musicbrainz.model.searchresult.CollectionResultWs2;
import org.musicbrainz.model.searchresult.LabelResultWs2;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.musicbrainz.model.searchresult.ReleaseGroupResultWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;
import org.musicbrainz.model.searchresult.WorkResultWs2;

import org.musicbrainz.query.QueryWs2;
import org.musicbrainz.webservice.AuthorizationException;
import org.musicbrainz.webservice.DefaultWebServiceWs2;

// TODO proper unit test coverage is needed and all commented @test should pass
public class UnitTests {

    protected Log log = LogFactory.getLog(QueryWs2.class);
     
    String labelName = "EMI";
    String artistName = "Pink Floyd";
    String releaseGroupTitle = "ummagumma";
    String releaseTitle = "ummagumma";
    String recordingTitle = "astronomy domine";
    String workTitle = "astronomy domine";
    
    String username ="username";
    String password="password";
    String client = "xxx-1.02beta";

    /**
     * @throws java.lang.Exception
  */
    @Before
    public void setUp() throws Exception {
       

    }

    //@Test
    public void searchISRC(){
        
        String isrc= "GBAYE6900522";
        
        Recording recording = new Recording();

        recording.search("isrc:"+isrc);

        List<RecordingResultWs2> results  =  recording.getFullSearchResultList();
        for (RecordingResultWs2 rec : results)
         {
             System.out.println(rec.getRecording().toString());
         }
    }
    //@Test
    public void searchISWC(){
        
        String iswc= "T-010.475.727-8";
        
        Work work = new Work();

        work.search("iswc:"+iswc);

        List<WorkResultWs2> results  =  work.getFullSearchResultList();
        for (WorkResultWs2 rec : results)
         {
             System.out.println(rec.getWork().toString());
         }
    }
   //@Test
    public void SubmitReleasesToCollectionbyId() throws MBWS2Exception, AuthorizationException, Exception{
        
        Collection controller = new Collection();

        controller.getQueryWs().setUsername(username);
        controller.getQueryWs().setPassword(password);
        controller.getQueryWs().setClient(client);
        
        List <CollectionResultWs2> list = controller.search();
        
        CollectionWs2 collection = list.get(0).getCollection();
        
        collection = controller.lookUp(collection);
        for (ReleaseWs2 rel : collection.getReleases())
        {
             System.out.println(rel.toString());
        }
        List<String> releases= new ArrayList<String>();
        
        releases.add("b54e4ad1-188a-352f-85d8-ca40d4ec4748");
        releases.add("c501d29f-6b2e-3cbb-a298-ac53b5c00cb4");

        controller.addReleasesById(releases); 
        collection = controller.lookUp(collection);
        for (ReleaseWs2 rel : collection.getReleases())
        {
             System.out.println(rel.toString());
        }
        controller.removeReleasesById(releases); 
        collection = controller.lookUp(collection);
        for (ReleaseWs2 rel : collection.getReleases())
        {
             System.out.println(rel.toString());
        }

    }
   //@Test
   public void SubmitReleasesToCollection() throws MBWS2Exception, AuthorizationException, Exception{
        
        // retrieving some releases.
        Release relc = new Release();
        relc.search("ummagumma");
        List <ReleaseResultWs2>  rr = relc.getFullSearchResultList();
        List<ReleaseWs2> releases = new ArrayList<ReleaseWs2>();
        
        for (ReleaseResultWs2 r : rr)
        {
            releases.add(r.getRelease());
        }
       
       Collection controller = new Collection();

        controller.getQueryWs().setUsername(username);
        controller.getQueryWs().setPassword(password);
        controller.getQueryWs().setClient(client);
        
        List <CollectionResultWs2> list = controller.search();
        
        CollectionWs2 collection = list.get(0).getCollection();
        
        collection = controller.lookUp(collection);
        for (ReleaseWs2 rel : collection.getReleases())
        {
             System.out.println(rel.toString());
        }

        controller.addReleases(releases); 
        collection = controller.lookUp(collection);
        for (ReleaseWs2 rel : collection.getReleases())
        {
             System.out.println(rel.toString());
        }
        controller.removeReleases(releases); 
        collection = controller.lookUp(collection);
        for (ReleaseWs2 rel : collection.getReleases())
        {
             System.out.println(rel.toString());
        }
    }
    //@Test
    public void PostTagsAndRating() throws MBWS2Exception{
        
        Artist controller = new Artist();

        controller.getQueryWs().setUsername(username);
        controller.getQueryWs().setPassword(password);
        controller.getQueryWs().setClient(client);
        
        controller.getIncludes().setUserRatings(true);
        controller.getIncludes().setUserTags(true);
        
        ArtistWs2 artist= controller.lookUp("83d91898-7763-47d7-b03b-b92132375c47");
        for (TagWs2 tag : artist.getUserTags())
         {
             System.out.println(tag.getName());
         }
        System.out.println(artist.getUserRating().getAverageRating());
        
        artist.getUserRating().setAverageRating(1F);
        artist.getUserTags().clear();
        artist.getUserTags().add(new TagWs2("rock"));
        artist.getUserTags().add(new TagWs2("progressive"));
       
        controller.postUserRatings();
        controller.postUserTags();
        
        controller.lookUp(artist);
        for (TagWs2 tag : artist.getUserTags())
         {
             System.out.println(tag.getName());
         }
        System.out.println(artist.getUserRating().getAverageRating());
        

    }
    //@Test
    public void AddTagsAndRating() throws MBWS2Exception{
        
        Artist controller = new Artist();

        controller.getQueryWs().setUsername(username);
        controller.getQueryWs().setPassword(password);
        controller.getQueryWs().setClient(client);
        
        controller.getIncludes().setUserRatings(true);
        controller.getIncludes().setUserTags(true);
        
        ArtistWs2 artist= controller.lookUp("83d91898-7763-47d7-b03b-b92132375c47");
        for (TagWs2 tag : artist.getUserTags())
         {
             System.out.println(tag.getName());
         }
        System.out.println(artist.getUserRating().getAverageRating());
        
        String[] tags ={"progressive", "electronic","english"};
        
        controller.AddTags(tags);
        controller.rate(5F);
        controller.lookUp(artist);
        for (TagWs2 tag : artist.getUserTags())
         {
             System.out.println(tag.getName());
         }
        System.out.println(artist.getUserRating().getAverageRating());
        

    }
     //@Test
    public void PuidLookUp() throws MBWS2Exception{
         
         String id="96dfb3ee-2f68-b0f9-6fbb-6730b6a5124f";
         Puid controller = new Puid();
         
         PuidWs2 puidWs2 = controller.lookUp(id);
         System.out.println("PUID: "+puidWs2.getId()+" match: "+puidWs2.getRecordings().size()+" recordings");
         for (RecordingWs2 rec : puidWs2.getRecordings())
         {
             System.out.println(rec.toString());
         }
         
         puidWs2 = new PuidWs2();
         puidWs2.setId(id);
         controller = new Puid();
         
         puidWs2 = controller.lookUp(puidWs2);
         System.out.println("PUID: "+puidWs2.getId()+" match: "+puidWs2.getRecordings().size()+" recordings");
         for (RecordingWs2 rec : puidWs2.getRecordings())
         {
             
             System.out.println(rec.toString());
         }
    }
    //@Test
    public void DiscIdfromCD(){
        
        Disc controller = new Disc();
        String drive = "D:"; // BE SURE TO USE AN EXISTING DRIVE!!!

        try {
                DiscWs2 disc =controller.lookUp(drive);
                 System.out.println("DISC: "+disc.getDiscId()+" match: "+disc.getReleases().size()+" releases");
                 for (ReleaseWs2 rel : disc.getReleases())
                 {
                     System.out.println(rel.toString());
                 }
             } catch (MBWS2Exception ex) {
                    Logger.getLogger(UnitTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //@Test
    public void DiscIdfromToc(){
        String toc = "1 5 198910 150 61125 94600 117697 143212";
        
        Disc controller = new Disc();

        try {
                DiscWs2 disc =controller.lookUp(null, toc);
                 System.out.println("DISC: "+disc.getDiscId()+" match: "+disc.getReleases().size()+" releases");
                 for (ReleaseWs2 rel : disc.getReleases())
                 {
                     System.out.println(rel.toString());
                 }
             } catch (MBWS2Exception ex) {
                    Logger.getLogger(UnitTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //@Test
    public void DiscIdfromId(){
        String id = "9BHdYEmI5lNjGKqd5hUIc6W3oRc-";
        
        Disc controller = new Disc();
        
        try {
                DiscWs2 disc =controller.lookUp(id, null);
                 System.out.println("DISC: "+disc.getDiscId()+" match: "+disc.getReleases().size()+" releases");
                 for (ReleaseWs2 rel : disc.getReleases())
                 {
                     System.out.println(rel.toString());
                 }
             } catch (MBWS2Exception ex) {
                    Logger.getLogger(UnitTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //@Test
    public void DiscIdfromIdAndToc(){
        String id = "9BHdYEmI5lNjGKqd5hUIc6W3oRc-";
        String toc = "1 5 198910 150 61125 94600 117697 143212";
        
         Disc controller = new Disc();
        
        try {
                DiscWs2 disc =controller.lookUp(id, toc);
                 System.out.println("DISC: "+disc.getDiscId()+" match: "+disc.getReleases().size()+" releases");
                 for (ReleaseWs2 rel : disc.getReleases())
                 {
                     System.out.println(rel.toString());
                 }
             } catch (MBWS2Exception ex) {
                    Logger.getLogger(UnitTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //@Test
    public void DiscIdfromTrackListInSectors(){
        
        DiscWs2 disc = new DiscWs2();
        
        DiscTrackWs2 track = new DiscTrackWs2();
        track.setTracknum(1);
        track.setOffset(0);
        track.setLength(60975);
        disc.addTrack(track);
        
        track = new DiscTrackWs2();
        track.setTracknum(2);
       
        track.setLength(33900);
        disc.addTrack(track);
        
        track = new DiscTrackWs2();
        track.setTracknum(3);
        
        track.setLength(23100);
        disc.addTrack(track);
        
        track = new DiscTrackWs2();
        track.setTracknum(4);
        
        track.setLength(25125);
        disc.addTrack(track);
        
        track = new DiscTrackWs2();
        track.setTracknum(5);
        
        track.setLength(56250);
        disc.addTrack(track);
        
        Disc controller = new Disc();
        
        try {
               controller.lookUp(disc);
               System.out.println("DISC: "+disc.getDiscId()+" match: "+disc.getReleases().size()+" releases");
                 for (ReleaseWs2 rel : disc.getReleases())
                 {
                     System.out.println(rel.toString());
                 }
             } catch (MBWS2Exception ex) {
                    Logger.getLogger(UnitTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //@Test
    public void synopsisUseCase1(){
        
        log.debug("synopsisUseCase1");
        
        //To retrieve all the labels with name similar to "EMI":

        Label label = new Label();
        label.search("EMI");

        List <LabelResultWs2> results = label.getFullSearchResultList();
    }
    //@Test
    public void SynopsisUseCase2(){
        
        log.debug("synopsisUseCase2");
        //To retrieve the first 30 artists with name similar to "pink floyd",
       //scored 50 or over:

        Artist artist = new Artist();
      
        artist.getSearchFilter().setLimit((long)30);
        artist.getSearchFilter().setMinScore((long)50);

        artist.search("pink floyd");
        List<ArtistResultWs2> results  =  artist.getFirstSearchResultPage();
         //doSomething(results);
        
        //to get the remainders (if any):

        while (artist.hasMore()) {

            results = artist.getNextSearchResultPage();
            // doSomethingElse(results);
        }
    }
    //@Test
    public void SynopsisUseCase3() throws MBWS2Exception {
        log.debug("synopsisUseCase3");
        
        //To search releases by pink floyd, released in 1982:

        Release release = new Release();

        release.search("date:1990-??-?? AND creditname:pink floyd");

        List<ReleaseResultWs2> results  =  release.getFullSearchResultList();
        
    }
    //@Test
    public void SynopsisUseCase4() throws MBWS2Exception{
        
        log.debug("synopsisUseCase4"); 
        Artist artist = new Artist();
      
        artist.getSearchFilter().setLimit((long)30);
        artist.getSearchFilter().setMinScore((long)50);

        artist.search("pink floyd");
        List<ArtistResultWs2> results  =  artist.getFirstSearchResultPage();
        
        if (results.isEmpty()) return;
        
        ArtistWs2 pf = results.get(0).getArtist();
        
        artist = new Artist();
        ArtistWs2 pinkFloyd= artist.lookUp(pf);

        artist = new Artist();
        pinkFloyd= artist.lookUp(pf.getId());
        
 
    }
   //@Test
    public void SynopsisUseCase5() throws MBWS2Exception{
        
        log.debug("synopsisUseCase5");
        
        String name = "pink floyd";

        Artist artist = new Artist();
        artist.search(name);
        List<ArtistResultWs2> results  =  artist.getFullSearchResultList();

        if (results.isEmpty()) return;

        ArtistWs2 pinkFloyd = results.get(0).getArtist();
        artist = new Artist();

        pinkFloyd = artist.getComplete(pinkFloyd);
        
        // OR
        artist = new Artist();

        pinkFloyd = artist.lookUp(pinkFloyd);
        
        //you could get results in a List if you want:

        List<ReleaseGroupWs2> rgl = artist.getFullReleaseGroupList();
        
        //paginate:

        artist.getFirstReleaseListPage();
        
        while (artist.hasMoreReleases())
        {
            artist.getNextReleaseListPage();

        }

        //or simply store the results in pinkFloyd;

        artist.getFullReleaseVAList();
        artist.getFullRecordingList();
        artist.getFullWorkList();

        //at the end, get it:
        
        pinkFloyd = artist.lookUp(pinkFloyd);
        
        //or 
        
        pinkFloyd = artist.getComplete(pinkFloyd);

        //note they are equivalent at this point.

    }
    //@Test
    public void SynopsisUseCase6() throws MBWS2Exception{

        log.debug("synopsisUseCase6");
        
        String name = "pink floyd";

        Artist artist = new Artist();
        artist.search(name);
        List<ArtistResultWs2> results  =  artist.getFullSearchResultList();

        if (results.isEmpty()) return;

        ArtistWs2 pinkFloyd = results.get(0).getArtist();
        artist = new Artist();
        
        // To get Only Releases by type "Album" and status "Official"

        artist.getReleaseBrowseFilter().getReleaseTypeFilter().setTypeAlbum(true);
        artist.getReleaseBrowseFilter().getReleaseStatusFilter().setStatusOfficial(true);

        // To get 50 releases per page:

        artist.getReleaseBrowseFilter().setLimit((long)50);
        
        pinkFloyd = artist.lookUp(pinkFloyd);
        artist.getFullReleaseList();
        
    }
     //@Test
    public void SynopsisUseCase7() throws MBWS2Exception{

        log.debug("synopsisUseCase7");
        
        String name = "pink floyd";

        Artist artist = new Artist();
        artist.search(name);
        List<ArtistResultWs2> results  =  artist.getFullSearchResultList();

        if (results.isEmpty()) return;

        ArtistWs2 pinkFloyd = results.get(0).getArtist();
        artist = new Artist();
        
        artist.getIncludes().setAliases(false);
        artist.getIncludes().setArtistRelations(false);
        artist.getIncludes().setWorkRelations(false);
        artist.getIncludes().setAnnotation(false);

        artist.lookUp(pinkFloyd);

        //will load only the ReleaseRelations, UrlRelations and ReleaseGroupRelations.

        //then, when needed, you could add the others:

        artist.getIncludes().setAliases(true);
        artist.lookUp(pinkFloyd);

        artist.getIncludes().setAnnotation(true);
        artist.lookUp(pinkFloyd);

        artist.getIncludes().setArtistRelations(true);
        artist.getIncludes().setWorkRelations(true);
        artist.lookUp(pinkFloyd);

    }
    //@Test
    public void SynopsisUseCase8() throws MBWS2Exception{

        log.debug("synopsisUseCase8");
        
        String name = "pink floyd";

        Artist artist = new Artist();
        artist.search(name);
        List<ArtistResultWs2> results  =  artist.getFullSearchResultList();

        if (results.isEmpty()) return;

        ArtistWs2 pinkFloyd = results.get(0).getArtist();
        artist = new Artist();
        
        artist.getIncludes().setAliases(false);
        artist.getIncludes().setArtistRelations(false);
        artist.getIncludes().setWorkRelations(false);
        artist.getIncludes().setAnnotation(false);
        
        artist.getIncludes().setReleaseGroups(false);
        artist.getIncludes().setReleases(false);
        artist.getIncludes().setRecordings(false);
        artist.getIncludes().setVariousArtists(false);
        artist.getIncludes().setWorks(false);

        artist.getComplete(pinkFloyd);
        
        //will perform exactly as

        //artist.LookUp(pinkFloyd);
        
       //again, You could set to true the appropriates parameter first, 
        
        artist.getIncludes().setAnnotation(true);
        artist.getIncludes().setAliases(true);
       
        //then hit as many subsequent 
        
        artist.getComplete(pinkFloyd);

        artist.getIncludes().setArtistRelations(true);
        artist.getIncludes().setWorkRelations(true);
        artist.getComplete(pinkFloyd);
        
        artist.getIncludes().setReleaseGroups(true);
        artist.getComplete(pinkFloyd);
        
        artist.getIncludes().setReleases(false);
        artist.getIncludes().setRecordings(false);
        artist.getIncludes().setVariousArtists(false);
        artist.getIncludes().setWorks(false);
        artist.getComplete(pinkFloyd);

    }
    //@Test
    public void SynopsisUseCase9() throws MBWS2Exception{

        log.debug("synopsisUseCase9");
        
        String name = "pink floyd";

        Artist artist = new Artist();
        artist.search(name);
        List<ArtistResultWs2> results  =  artist.getFullSearchResultList();

        if (results.isEmpty()) return;

        ArtistWs2 pinkFloyd = results.get(0).getArtist();
        
        artist = new Artist();
        
        artist.getIncludes().setAliases(false);
        artist.getIncludes().setArtistRelations(false);
        artist.getIncludes().setWorkRelations(false);
        artist.getIncludes().setAnnotation(false);
        
        artist.getIncludes().setReleaseGroups(false);
        artist.getIncludes().setReleases(false);
        artist.getIncludes().setRecordings(false);
        artist.getIncludes().setVariousArtists(false);
        artist.getIncludes().setWorks(false);
        
        artist.getComplete(pinkFloyd);
        
        // is a simple LookUp with no Incs.

        artist.getFirstWorkListPage();
        
        // get the first page and turn on the Include param for works

        artist.getComplete(pinkFloyd);
        
        // will complete ONLY the work list.
        
        artist.getFirstReleaseListPage();

        artist.getReleaseIncludes().setLabel(false);
        artist.getReleaseIncludes().setRecordings(true);
        artist.getReleaseIncludes().setArtistRelations(true);

        artist.getComplete(pinkFloyd);
        
        // will complete the release list, but be careful...
        // only the last results reports Recordings and ArtistRelations!!!
        
    }
    //@Test
    public void SynopsisUseCase10() throws MBWS2Exception{
        
        log.debug("SynopsisUseCase10");
        
        String name = "pink floyd";
        
        MyWebServiceImplementation myQueryWs = 
                new MyWebServiceImplementation();
        
        MyWebServiceImplementation myAnnotationsWs = 
                new MyWebServiceImplementation();
        
        ((DefaultWebServiceWs2)myAnnotationsWs).setHost("search.musicbrainz.org");
        
        Artist artist = new Artist();
        artist.setQueryWs(myQueryWs);
        artist.setAnnotationWs(myAnnotationsWs);
        artist.search(name);

        List<ArtistResultWs2> results  =  artist.getFullSearchResultList();

        if (results.isEmpty()) return;

        ArtistWs2 pinkFloyd = results.get(0).getArtist();
        
        artist = new Artist();
        artist.lookUp(pinkFloyd);
        
        artist.setQueryWs(myQueryWs);
        artist.setAnnotationWs(myAnnotationsWs);
        
        artist.getComplete(pinkFloyd);
        
        // ERROR: Keep using the default ws

        artist = new Artist();
        artist.setQueryWs(myQueryWs);
        artist.setAnnotationWs(myAnnotationsWs);
        artist.getComplete(pinkFloyd);
        
        // OK: Is using the correct one
    }
    //@Test
    public void searchLabels() throws MBWS2Exception {
        
        Label label = new Label();
        
        label.search(labelName);

        // getting the full list.
        List<LabelResultWs2> results  =  label.getFullSearchResultList();

        doSomethingWithLabels(results);
    }
        private void doSomethingWithLabels(List<LabelResultWs2> results) throws MBWS2Exception{
     
        // Focus on the first result
        getOneLabel(results.get(0).getLabel());
        
    }
    private void getOneLabel(LabelWs2 in) throws MBWS2Exception{

        Label label = new Label();
        
        // get all the informations at once.
        
        LabelWs2 result = label.getComplete(in);
        
        // or if you have the id like:
        
        String id = result.getId();
        result = label.getComplete(id);
        
        // do something with the data.
        
        doSomething(result);
    }
    private void doSomething(LabelWs2 label){
        //Add some actions here.
    }

    //@Test
    public void searchArtists() throws MBWS2Exception {
        
       Artist artist = new Artist();
       
       // Adding parameters to the search filter
       
       artist.getSearchFilter().setLimit((long)10); // passed to the query   
       artist.getSearchFilter().setMinScore((long)30); // stop the pagination    
       
        artist.search(artistName);
        
        // getting directly the full list.
        List<ArtistResultWs2> results  =  artist.getFullSearchResultList();
        
        doSomethingWithArtists(results);
    }
    private void doSomethingWithArtists(List<ArtistResultWs2> results) throws MBWS2Exception{
        // Focus on the first result
        getOneArtist(results.get(0).getArtist());
   }
   private void getOneArtist(ArtistWs2 in) throws MBWS2Exception{

        Artist artist = new Artist();
        
        ArtistWs2 result;
        
        // get bunch of informations in different API calls:

        artist.getIncludes().setAliases(false);
        artist.getIncludes().setArtistRelations(false);
        artist.getIncludes().setWorkRelations(false);
        artist.getIncludes().setAnnotation(false);
        
        artist.lookUp(in);
  
        artist.getIncludes().setAliases(true);
        artist.lookUp(in);
        
        artist.getIncludes().setAnnotation(true);
        artist.lookUp(in);
        
        artist.getIncludes().setArtistRelations(true);
        artist.getIncludes().setWorkRelations(true);
        
        
        artist.getIncludes().setReleaseGroups(true);
        artist.getIncludes().setReleases(true);
        artist.getIncludes().setRecordings(true);
        artist.getIncludes().setVariousArtists(true);
        artist.getIncludes().setWorks(true);
        
        
        // Using the id
        artist.lookUp(in.getId());

        // get the complete release group list.
        artist.getFullReleaseGroupList();

        // paginating Releases
        
        artist.getFirstReleaseListPage();
        while (artist.hasMoreReleases())
        {
            artist.getNextReleaseListPage();
        }
        ReleaseTypeFilterWs2 f = new ReleaseTypeFilterWs2();
        f.setTypeAlbum(true);
        
        artist.getReleaseBrowseFilter().getReleaseTypeFilter().setTypeAlbum(true);
        artist.getReleaseBrowseFilter().getReleaseStatusFilter().setStatusOfficial(true);
        artist.getReleaseBrowseFilter().setLimit((long)50);
        //get everything else at once
        
        result = artist.getComplete(in);
        
        // do something with the data.
        
        doSomething(result);

    }

    private void doSomething(ArtistWs2 result){
       
   }
    //@Test
    public void searchReleaseGroups() throws MBWS2Exception {
         
        ReleaseGroup releaseGroup = new ReleaseGroup();
       
       // Looking for exacting matches
        long perPage = (long)100;
        
       releaseGroup.getSearchFilter().setLimit((long)10);  
       releaseGroup.getSearchFilter().setMinScore((long)100);
       
       releaseGroup.search(releaseGroupTitle);
        
        // getting the list.
        List<ReleaseGroupResultWs2> results  =  releaseGroup.getFullSearchResultList();
        
        if (results.size() >0)
            doSomethingWithReleaseGroups(results);
        else
        {
            // do something else.
        }
    }
    private void doSomethingWithReleaseGroups(List<ReleaseGroupResultWs2> results) throws MBWS2Exception{
        // Focus on the first result
        getOneReleaseGroup(results.get(0).getReleaseGroup());
   }
   private void getOneReleaseGroup(ReleaseGroupWs2 in) throws MBWS2Exception{
       
        ReleaseGroup releaseGroup = new ReleaseGroup();

        ReleaseGroupWs2 result;
        
        //include or exclude some data in the subquery results
        
        releaseGroup.getReleaseIncludes().setLabel(false);
        releaseGroup.getReleaseIncludes().setRecordings(true);
        releaseGroup.getReleaseIncludes().setArtistRelations(true);
       
        result = releaseGroup.getComplete(in);
       
        // do something with the data.
        doSomething(result);

    }

    private void doSomething(ReleaseGroupWs2 result){
       
   }
   //@Test
   public void searchReleases() throws MBWS2Exception {
        
      Release release = new Release();

       //search for release by pinkFloyd released in 1982
      // (see: http://musicbrainz.org/doc/Text_Search_Syntax).
      
       release.search("date:1990-??-?? AND creditname:pink floyd");
        
        // getting the list.
        List<ReleaseResultWs2> results  =  release.getFullSearchResultList();
        
        if (results.size() >0)
            doSomethingWithRelease(results);
        
    }
     private void doSomethingWithRelease(List<ReleaseResultWs2> results) throws MBWS2Exception{
        // Focus on the first result
        getOneRelease(results.get(0).getRelease());
   }
     private void getOneRelease(ReleaseWs2 in) throws MBWS2Exception{
         
     Release release = new Release();
        
        ReleaseWs2 result;
        
        //include or exclude some data in LookUp
        
        release.getIncludes().setWorkLevelRelations(true);
        release.getIncludes().setRecordingLevelRelations(true);
        release.getIncludes().setArtistRelations(true);
       
        result = release.getComplete(in);
       
        // do something with the data.
        doSomething(result);

    }

    private void doSomething(ReleaseWs2 result){
       
   }
    //@Test
    public void searchRecordings() throws MBWS2Exception {
                
            Recording recording = new Recording();
            
            recording.search(recordingTitle);
            List<RecordingResultWs2> results  =  recording.getFullSearchResultList();
            
            if (results.isEmpty()) return;
            
            RecordingWs2 recordingWs2 = recording.getComplete(results.get(0).getRecording());
    }
    //@Test
    public void searchWorks() throws MBWS2Exception {
            Work work = new Work();
            
            work.search(workTitle);
            List<WorkResultWs2> results  =  work.getFullSearchResultList();
            
            if (results.isEmpty()) return;
            
            WorkWs2 workWs2 = work.getComplete(results.get(0).getWork());
       
    }
}
