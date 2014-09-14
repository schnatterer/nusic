package org.musicbrainz.wsxml.impl;

import org.musicbrainz.DomainsWs2;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.MbXMLParseException;
import org.musicbrainz.wsxml.MbXmlParser;
import org.musicbrainz.utils.MbUtils;

import org.musicbrainz.wsxml.element.Metadata;
import org.musicbrainz.wsxml.element.ListElement;

import org.musicbrainz.model.AliasWs2;
import org.musicbrainz.model.AnnotationWs2;
import org.musicbrainz.model.ArtistCreditWs2;
import org.musicbrainz.model.entity.listelement.DiscListWs2;
import org.musicbrainz.model.entity.DiscWs2;
import org.musicbrainz.model.IsrcWs2;
import org.musicbrainz.model.LabelInfoListWs2;
import org.musicbrainz.model.LabelInfoWs2;
import org.musicbrainz.model.LifeSpanWs2;
import org.musicbrainz.model.MediumListWs2;
import org.musicbrainz.model.MediumWs2;
import org.musicbrainz.model.NameCreditWs2;
import org.musicbrainz.model.PuidWs2;
import org.musicbrainz.model.RatingsWs2;
import org.musicbrainz.model.RelationWs2;
import org.musicbrainz.model.TrackListWs2;
import org.musicbrainz.model.TrackWs2;
import org.musicbrainz.model.TagWs2;
import org.musicbrainz.model.entity.ArtistWs2;
import org.musicbrainz.model.entity.CollectionWs2;
import org.musicbrainz.model.entity.EntityWs2;
import org.musicbrainz.model.entity.LabelWs2;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.model.entity.listelement.ArtistListWs2;
import org.musicbrainz.model.entity.listelement.LabelListWs2;
import org.musicbrainz.model.entity.listelement.RecordingListWs2;
import org.musicbrainz.model.RelationListWs2;
import org.musicbrainz.model.entity.listelement.ReleaseGroupListWs2;
import org.musicbrainz.model.entity.listelement.ReleaseListWs2;
import org.musicbrainz.model.entity.listelement.WorkListWs2;
import org.musicbrainz.model.searchresult.AnnotationResultWs2;
import org.musicbrainz.model.searchresult.ArtistResultWs2;
import org.musicbrainz.model.searchresult.CollectionResultWs2;
import org.musicbrainz.model.searchresult.LabelResultWs2;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.musicbrainz.model.searchresult.ReleaseGroupResultWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;
import org.musicbrainz.model.searchresult.WorkResultWs2;
import org.musicbrainz.model.searchresult.listelement.AnnotationSearchResultsWs2;
import org.musicbrainz.model.searchresult.listelement.ArtistSearchResultsWs2;
import org.musicbrainz.model.searchresult.listelement.CollectionSearchResultsWs2;
import org.musicbrainz.model.searchresult.listelement.LabelSearchResultsWs2;
import org.musicbrainz.model.searchresult.listelement.RecordingSearchResultsWs2;
import org.musicbrainz.model.searchresult.listelement.ReleaseGroupSearchResultsWs2;
import org.musicbrainz.model.searchresult.listelement.ReleaseSearchResultsWs2;
import org.musicbrainz.model.searchresult.listelement.WorkSearchResultsWs2;


/**
 * <p>A parser for the Music Metadata XML format.</p>
 * 
 * <p>This parser supports all basic features and extensions defined by
 * MusicBrainz, including unlimited document nesting. By default it
 * reads an XML document from a stream and returns an object tree
 * representing the document using the flexible {@link Metadata}
 * class.</p>
 * 
 * <p>The implementation tries to be as permissive as possible. Invalid
 * contents are skipped, but documents have to be well-formed and using
 * the correct namespace. In case of unrecoverable errors, a 
 * {@link MbXMLParseException} exception is raised.</p>

 */
public class JDOMParserWs2 extends DomainsWs2 implements MbXmlParser  {
    
    private Log log = LogFactory.getLog(JDOMParserWs2.class);

    /**
     * Default constructor
     */
    public JDOMParserWs2() {

    }

    /* (non-Javadoc)
     * @see org.musicbrainz.wsxml.MbXmlParser#parse(java.io.InputStream)
     */
    public Metadata parse(InputStream inputStream) throws MbXMLException{
        SAXBuilder builder = new SAXBuilder();
        try 
        {
                Document doc = builder.build(inputStream);

                Element root = doc.getRootElement();
                if ( (root == null) || (!NS_MMD_2.equals(root.getNamespace())) ||
                                !METADATA.equals(root.getName())) {
                        throw new MbXMLParseException("No root element with the name metadata in " + NS_MMD_2 + " found!");
                }

                return createMetadata(root);
        } 
        catch (Exception e) 
        {
                throw new MbXMLParseException("The xml could not be read!", e);
        }
    }

    protected Metadata createMetadata(Element elem){
        Metadata md = new Metadata();

        Iterator itr = elem.getChildren().iterator();
        Element node;
        while(itr.hasNext()) 
        {
             node = (Element)itr.next();

             if (LABEL.equals(node.getName())) {
                 md.setLabelWs2(createLabel(node));
             }
             else if (ARTIST.equals(node.getName())) {
                 md.setArtistWs2(createArtist(node));
             }
             else if (RELEASEGROUP.equals(node.getName())) {
                 md.setReleaseGroupWs2(createReleaseGroup(node));
             }
             else if (RELEASE.equals(node.getName())) {
                 md.setReleaseWs2(createRelease(node));
             }
             else if (WORK.equals(node.getName())) {
                 md.setWorkWs2(createWork(node));
             }
             else if (RECORDING.equals(node.getName())) {
                 md.setRecordingWs2(createRecording(node));
             }
             else if (ANNOTATION.equals(node.getName())) {
                 md.setAnnotationWs2(createAnnotation(node));
             }
             else if (COLLECTION.equals(node.getName())) {
                 md.setCollectionWs2(createCollection(node));
             }
             else if (DISC.equals(node.getName())) {
                 md.setDiscWs2(createDisc(node));
             }
             else if (PUID.equals(node.getName())) {
                 md.setPuidWs2(createPuid(node));
             }
             else if (LABELLIST.equals(node.getName())) {
                addLabelResults(node, md.getLabelResultsWs2());
             }
             else if (ARTISTLIST.equals(node.getName())) {
                addArtistResults(node, md.getArtistResultsWs2());
             }
             else if (RELEASEGROUPLIST.equals(node.getName())) {
                 addReleaseGroupResults(node, md.getReleaseGroupResultsWs2());
             } 
             else if (RELEASELIST.equals(node.getName())) {
                 addReleaseResults(node, md.getReleaseResultsWs2());
             }
             else if (RECORDINGLIST.equals(node.getName())) {
                 addRecordingResults(node, md.getRecordingResultsWs2());
             }
             else if (WORKLIST.equals(node.getName())) {
                 addWorkResults(node, md.getWorkResultsWs2());
             }
             else if (COLLECTIONLIST.equals(node.getName())) {
                 addCollectionResults(node, md.getCollectionResultsWs2());
             }
             else if (ANNOTATIONLIST.equals(node.getName())) {
                 addAnnotationResults(node, md.getAnnotationResultsWs2());
             }
             else if (MESSAGE.equals(node.getName())) {
                          md.setMessage(createMessage(node));
             }
             else
            {
               log.warn("Unrecognised metadata element: "+node.getName());
            }
       }
       return md;
    }
    
    protected String createMessage(Element node){
        return node.getValue();
    }
    
    protected CollectionWs2 createCollection (Element node) {
        CollectionWs2 collection = new CollectionWs2();
       
        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
            Attribute attribute = (Attribute)itr.next();

            if (attribute.getName().equals(ID))
            {
                collection.setIdUri(MbUtils.convertIdToURI(attribute.getValue(), COLLECTION));
            }
            else
            {
                log.warn("Unrecognised Collection attribute: "+attribute.getName());
            }
        }
        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();

            if (EDITOR.equals(child.getName())) {
                 collection.setEditor(child.getText());
             }
             else if (NAME.equals(child.getName())) {
                 collection.setName(child.getText());
             }
             else if (RELEASELIST.equals(child.getName())) {
                 addReleasesToList(child, collection.getReleaseList());
             }
            else
            {
                log.warn("Unrecognised Collection element: "+child.getName());
            }
        }
        return collection;
    }
    protected AnnotationWs2 createAnnotation(Element node) {
        AnnotationWs2 annotation = new AnnotationWs2();
        
        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
            Attribute attribute = (Attribute)itr.next();

            if (attribute.getName().equals(TYPE))
            {
                annotation.setType(MbUtils.convertTypeToURI(attribute.getValue(), NS_MMD_2_PREFIX));
            }
            else if (attribute.getName().equals(SCORE))
            {
              //ignore.
            }  
            else
            {
                log.warn("Unrecognised Annotation attribute: "+attribute.getName());
            }
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();

            if (ENTITY.equals(child.getName())) {
                annotation.setEntity(child.getText());
            }
            else if (NAME.equals(child.getName())) {
                annotation.setName(child.getText());
            }
            else if (TEXT.equals(child.getName())) {
                annotation.setText(child.getText());
            }
            else{
               log.warn("Unrecognised Annotation element: "+child.getName());
            }
        }
        return annotation;
    }
    protected LabelWs2 createLabel(Element node) {
        LabelWs2 label = new LabelWs2();
        
        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
            Attribute attribute = (Attribute)itr.next();

            if (attribute.getName().equals(ID)){
                label.setIdUri(MbUtils.convertIdToURI(attribute.getValue(), LABEL));
            }
            else if (attribute.getName().equals(TYPE)){
                label.setType(MbUtils.convertTypeToURI(attribute.getValue(), NS_MMD_2_PREFIX));
            }
            else if (attribute.getName().equals(SCORE)){
              //ignore.
            }  
            else{
              log.warn("Unrecognised Label attribute: "+attribute.getName());
            }
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
             child = (Element)itr.next();

             if (NAME.equals(child.getName())) {
                label.setName(child.getText());
             }
             else if (SORTNAME.equals(child.getName())) {
                label.setSortName(child.getText());
             }
             else if (DISAMBIGUATION.equals(child.getName())) {
                label.setDisambiguation(child.getText());
             }
             else if (LABELCODE.equals(child.getName())) {
                label.setLabelCode(child.getText());
             }
             else if (IPI.equals(child.getName())) {
                label.setIpi(child.getText());
             }
             else if (COUNTRY.equals(child.getName())) {
                label.setCountry(child.getText());
             }
             else if (LIFESPAN.equals(child.getName())) {
                label.setLifeSpan(createLifeSpan(child));
             }
             else if (RATING.equals(child.getName())) {
                label.setRating(createRating(child));
             }
             else if (USERRATING.equals(child.getName())) {
                label.setUserRating(createUserRating(child));
             }
             else if (ALIASLIST.equals(child.getName())) {
                addAliases(child, label);
             }
             else if (RELATIONLIST.equals(child.getName())) {
                addRelationsToEntity(child, label.getRelationList());
                   }
             else if (RELEASELIST.equals(child.getName())) {
                addReleasesToList(child, label.getReleaseList());
             }
             else if (TAGLIST.equals(child.getName())) {
                addTags (child, label);
             }
             else if (USERTAGLIST.equals(child.getName())) {
                addUserTags (child, label);
             } 
             else
            {
               log.warn("Unrecognised Label element: "+child.getName());
            }
        }
        return label;
    }
    protected ArtistWs2 createArtist(Element node){ 
        
        ArtistWs2 artist = new ArtistWs2();
        //artist.setIdUri(MbUtils.convertIdToURI(node.getAttributeValue(ID), ARTIST) );
        //artist.setType(MbUtils.convertTypeToURI(node.getAttributeValue(TYPE), EntityWs2.NS_MMD_2));

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
            Attribute attribute = (Attribute)itr.next();

            if (attribute.getName().equals(ID)){
              artist.setIdUri(MbUtils.convertIdToURI(attribute.getValue(), ARTIST));
            }
            else if (attribute.getName().equals(TYPE)){
              artist.setType(MbUtils.convertTypeToURI(attribute.getValue(), NS_MMD_2_PREFIX));
            }
            else if (attribute.getName().equals(SCORE)){
              //ignore.
            }  
            else{
              log.warn("Unrecognised Artist attribute: "+attribute.getName());
            }
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();

            if (NAME.equals(child.getName())) {
                 artist.setName(child.getText());
            }
            else if (SORTNAME.equals(child.getName())) {
                 artist.setSortName(child.getText());
            }
            else if (DISAMBIGUATION.equals(child.getName())) {
                 artist.setDisambiguation(child.getText());
            }
            else if (COUNTRY.equals(child.getName())) {
                 artist.setCountry(child.getText());
            }
            else if (GENDER.equals(child.getName())) {
                 artist.setGender(child.getText());
             }
            else if (IPI.equals(child.getName())) {
                artist.setIpi(child.getText());
             }
             else if (LIFESPAN.equals(child.getName())) {
                 artist.setLifeSpan(createLifeSpan(child));
             }
             else if (RATING.equals(child.getName())) {
                 artist.setRating(createRating(child));
             }
             else if (USERRATING.equals(child.getName())) {
                 artist.setUserRating(createUserRating(child));
             }
             else if (ALIASLIST.equals(child.getName())) {
                 addAliases(child, artist);
             } 
                   /*  is in mmd but not allowed in ws.
                    else if (LABELLIST.equals(child.getName())) {
                 addLabelsToList(child, artist.getLabelList());
           }
           */
             else if (RELATIONLIST.equals(child.getName())) {
                 addRelationsToEntity(child, artist.getRelationList());
             }
             else if (RELEASEGROUPLIST.equals(child.getName())) {
                 addReleaseGroupsToList(child, artist.getReleaseGroupList());
             }
             else if (RELEASELIST.equals(child.getName())) {
                 addReleasesToList(child, artist.getReleaseList());
             }
             else if (RECORDINGLIST.equals(child.getName())) {
                 addRecordingToList(child, artist.getRecordingList());
             }
             else if (WORKLIST.equals(child.getName())) {
                 addWorkToList(child, artist.getWorkList());
             }
             else if (TAGLIST.equals(child.getName())) {
                 addTags(child, artist);
             }
             else if (USERTAGLIST.equals(child.getName())) {
                 addUserTags (child, artist);
             } 
             else {
             log.warn("Unrecognised Artist element: "+child.getName());
             }
        }
        return artist;
    }
    protected ReleaseGroupWs2 createReleaseGroup(Element node){
        
        if (node.getAttributeValue(ID)==null) return null;

        ReleaseGroupWs2 releaseGroup = new ReleaseGroupWs2();

        Iterator itr = node.getAttributes().iterator();
          while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(ID)){
                  releaseGroup.setIdUri(
                          MbUtils.convertIdToURI(attribute.getValue(), 
                          RELEASEGROUP));
              }
              else if (attribute.getName().equals(TYPE)){
                  releaseGroup.setType(
                          MbUtils.convertTypeToURI(attribute.getValue(), 
                          NS_MMD_2_PREFIX));
                  
                  releaseGroup.setTypeString(attribute.getValue());
              }
              else if (attribute.getName().equals(SCORE)) {
                  //ignore.
              }  
              else{
                  log.warn("Unrecognised Artist attribute: "+attribute.getName());
              }
          }

          itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (TITLE.equals(child.getName())) {
                releaseGroup.setTitle(child.getText());
            }
            else if (FIRSTRELEASEDATE.equals(child.getName())) {
                releaseGroup.setFirstReleaseDateStr(child.getText());
            }
            else if (DISAMBIGUATION.equals(child.getName())) {
                releaseGroup.setDisambiguation(child.getText());
            }
            else if (ARTISTCREDIT.equals(child.getName())) {
                releaseGroup.setArtistCredit(createArtistCredit(child));
            }
            else if (RATING.equals(child.getName())) {
                releaseGroup.setRating(createRating(child));
            }
            else if (USERRATING.equals(child.getName())) {
                releaseGroup.setUserRating(createUserRating(child));
            }
            else if (RELEASELIST.equals(child.getName())) {
                addReleasesToList(child, releaseGroup.getReleaseList());
            }
            else if (RELATIONLIST.equals(child.getName())) {
                addRelationsToEntity(child, releaseGroup.getRelationList());
            }
            else if (TAGLIST.equals(child.getName())) {
                addTags(child, releaseGroup);
            }
            else if (USERTAGLIST.equals(child.getName())) {
                addUserTags (child, releaseGroup);
            }
            else
            {
               log.warn("Unrecognised Release Group element: "+child.getName());
            }
        }
        return releaseGroup;
    }
    protected ReleaseWs2 createRelease(Element node){
        
        ReleaseWs2 release = new ReleaseWs2();

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
            Attribute attribute = (Attribute)itr.next();

            if (attribute.getName().equals(ID)){
                release.setIdUri(MbUtils.convertIdToURI(attribute.getValue(),
                                        RELEASE));
            }
            else if (attribute.getName().equals(SCORE)){
              //ignore.
            }  
            else{
              log.warn("Unrecognised Release attribute: "+attribute.getName());
            }
        }

        itr  = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (TITLE.equals(child.getName())) {
                 release.setTitle(child.getText());
            }
            else if (STATUS.equals(child.getName())) {
                 release.setStatus(child.getText());
            }
            else if (QUALITY.equals(child.getName())) {
                 release.setQualityStr(child.getText());
                 release.setQuality(MbUtils.convertTypeToURI(
                                                child.getText(), 
                                                NS_MMD_2_PREFIX));
            }
            else if (DISAMBIGUATION.equals(child.getName())) {
                release.setDisambiguation(child.getText());
            }
            else if (PACKAGING.equals(child.getName())) {
                release.setPackaging(child.getText());
            }
            else if (TEXTREPRESENTATION.equals(child.getName())) {

                Iterator itr2 = child.getChildren().iterator();
                Element el;
                while(itr2.hasNext()) 
                {
                    el = (Element)itr2.next();
                    if (LANGUAGE.equals(el.getName())) {
                            release.setTextLanguage(getText(el.getText(),languagePattern,el.getText()));
                    }
                    else if (SCRIPT.equals(el.getName())) {
                            release.setTextScript(getText(el.getText(),scriptPattern,el.getText()));
                    }
                }
            }
            else if (ARTISTCREDIT.equals(child.getName())) {
                release.setArtistCredit(createArtistCredit(child));
            }   
            else if (RELEASEGROUP.equals(child.getName())) {
                release.setReleaseGroup(createReleaseGroup(child));
            }
            else if (DATE.equals(child.getName())) {
                release.setDateStr(child.getText());
            }
            else if (COUNTRY.equals(child.getName())) {
                release.setCountryId(getText(child.getText().toUpperCase(),countryPattern,null));
            }
            else if (BARCODE.equals(child.getName())) {
                release.setBarcode(child.getText());
            }
            else if (ASIN.equals(child.getName())) {
                release.setAsin(child.getText());
            }
            else if (RATING.equals(child.getName())) {
                // not in MB at the moment.
                release.setRating(createRating(child));
            }
            else if (USERRATING.equals(child.getName())) {
                // not in MB at the moment.
                release.setUserRating(createUserRating(child));
            }
            else if (LABELINFOLIST.equals(child.getName())) {
                release.setLabelInfoList(createLabelInfoList(child));
            }
            else if (MEDIUMLIST.equals(child.getName())) {
                release.setMediumList(createMediumList(child));
            }
            else if (RELATIONLIST.equals(child.getName())) {
                addRelationsToEntity(child, release.getRelationList());
            }
            else if (TAGLIST.equals(child.getName())) {
                 // not in MB at the moment.
            addTags(child, release);
            }
            else if (USERTAGLIST.equals(child.getName())) {
            // not in MB at the moment. 
                addUserTags (child, release);
            }
            else{
               log.warn("Unrecognised Release element: "+child.getName());
            }
        }
        return release;
    }
    protected RecordingWs2 createRecording(Element node) {
        
        RecordingWs2 recording = new RecordingWs2();

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(ID)){
                  recording.setIdUri(MbUtils.convertIdToURI(attribute.getValue(), RECORDING));
              }
              else if (attribute.getName().equals(SCORE)){
                  //ignore.
              }  
              else{
                  log.warn("Unrecognised Recording attribute: "+attribute.getName());
              }
        }

        itr = node.getChildren().iterator();
        Element child;
        
        while(itr.hasNext()) {
            
            child = (Element)itr.next();
            if (TITLE.equals(child.getName())) {
                recording.setTitle(child.getText());
            }
            else if (LENGTH.equals(child.getName())) {
                String d = child.getValue();
                if (d != null) {
                    try {
                            Long l = Long.parseLong(d);
                            recording.setDurationInMillis(l);
                    } catch (NumberFormatException e) {
                            log.warn("Illegal duration value!", e);
                    }
                }	
            }
            else if (DISAMBIGUATION.equals(child.getName())) {
                recording.setDisambiguation(child.getText());
            }
            else if (ARTISTCREDIT.equals(child.getName())) {
                recording.setArtistCredit(createArtistCredit(child));
            }
            else if (RATING.equals(child.getName())) {
                recording.setRating(createRating(child));
            }
            else if (USERRATING.equals(child.getName())) {
                recording.setUserRating(createUserRating(child));
            }
            else if (PUIDLIST.equals(child.getName())) {
                addPuidsToRecording(child, recording);
            }
            else if (ISRCLIST.equals(child.getName())) {
                addIsrcsToRecording(child, recording);
            }
            else if (RELEASELIST.equals(child.getName())) {
                addReleasesToList(child, recording.getReleaseList());
            }
            else if (RELATIONLIST.equals(child.getName())) {
                addRelationsToEntity(child, recording.getRelationList());
            }
            else if (TAGLIST.equals(child.getName())) {
                addTags(child, recording);
            }
            else if (USERTAGLIST.equals(child.getName())) { 
                addUserTags (child, recording);
            }
            else{
                log.warn("Unrecognised Recording element: "+child.getName());
            }
        }   
        return recording;
}
    protected WorkWs2 createWork(Element node){

        WorkWs2 work = new WorkWs2();

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

              Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(ID)){
                  work.setIdUri(MbUtils.convertIdToURI(attribute.getValue(), WORK));
              }
              else if (attribute.getName().equals(TYPE)){
                  work.setTypeURI(MbUtils.convertTypeToURI(attribute.getValue(), NS_MMD_2_PREFIX));
              }
              else if (attribute.getName().equals(SCORE)){
                  //ignore.
              }  
              else{
                  log.warn("Unrecognised Work attribute: "+attribute.getName());
              }
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (TITLE.equals(child.getName())) {
                work.setTitle(child.getText());
            }
            else if (DISAMBIGUATION.equals(child.getName())) {
                work.setDisambiguation(child.getText());
            }
            else if (ISWC.equals(child.getName())) {
                work.setIswc(child.getText());
            }
            else if (ARTISTCREDIT.equals(child.getName())) {
                work.setArtistCredit(createArtistCredit(child));
            }
            else if (RATING.equals(child.getName())) {
                work.setRating(createRating(child));
            }
            else if (USERRATING.equals(child.getName())) {
                          work.setUserRating(createUserRating(child));
            }
            else if (ALIASLIST.equals(child.getName())) {
                addAliases(child, work);
            } 
            else if (RELATIONLIST.equals(child.getName())) {
                    addRelationsToEntity(child, work.getRelationList());
            }
            else if (TAGLIST.equals(child.getName())) {
                 addTags(child, work);
            }
            else if (USERTAGLIST.equals(child.getName())) { 
                        addUserTags (child, work);
            }
            else
            {
                log.warn("Unrecognised Work element: "+child.getName());
            }
        }
        return work;
    }

    protected RelationWs2 createRelation(Element node, String targetType){

        RelationWs2 relation = new RelationWs2();

        relation.setTargetType(targetType);
        String resType = MbUtils.extractTypeFromURI(targetType);
        EntityWs2 target = null;

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

              Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(TYPE)){
                  relation.setType(MbUtils.convertTypeToURI(attribute.getValue(), NS_REL_2_PREFIX));
              }
              else{
                  log.warn("Unrecognised Relation attribute: "+attribute.getName());
              }
        }

        itr = node.getChildren().iterator();
        Element child;

        while(itr.hasNext()) 
        {
            child = (Element)itr.next();

            if (TARGET.equals(child.getName())){
                relation.setTargetId(MbUtils.convertIdToURI(child.getText(), resType));
            }
            else if (DIRECTION.equals(child.getName())){
                relation.setDirection(getText(child.getText(),directionPattern,RelationWs2.DIR_BOTH));
            }
            else if (BEGIN.equals(child.getName())){
                relation.setBeginDate(child.getText());
            }
            else if (END.equals(child.getName())){
                relation.setEndDate(child.getText());
            }
            else if (ATTRIBUTELIST.equals(child.getName())){
                relation.setAttributes(getAttributeList(child));
            }
            else if (LABEL.equals(child.getName())) {
                target = createLabel(child);
            }
            else if (ARTIST.equals(child.getName())) {
                target = createArtist(child);
            }
            else if (RELEASEGROUP.equals(child.getName())) {
                target = createReleaseGroup(child);
            } 
            else if (RELEASE.equals(child.getName())) {
                target = createRelease(child);
            }
            else if (RECORDING.equals(child.getName())) {
                target = createRecording(child);
            }
            else if (WORK.equals(child.getName())) {
                target = createWork(child);
            }
            // URL is ok this way..
            else{
                log.warn("Unrecognised Relation element: "+child.getName());
            }
        }
        if (relation.getType() == null) return null;
        if (relation.getTargetType() == null) return null;

        if (relation.getTargetId()== null) relation.setTargetId(target.getId());

        relation.setTarget(target);
        return relation;
    }

    protected ArtistCreditWs2 createArtistCredit(Element node) {

        List<NameCreditWs2> nameCredits= new ArrayList<NameCreditWs2>(); 

        Iterator itrNameCredit = node.getAttributes().iterator();
        while (itrNameCredit.hasNext()) {
              Attribute attribute = (Attribute)itrNameCredit.next();
              log.warn("Unrecognised ArtistCredit attribute: "+attribute.getName());
        }

        itrNameCredit = node.getChildren().iterator();
        Element elNameCredit;
        while(itrNameCredit.hasNext()) 
        {
            elNameCredit = (Element)itrNameCredit.next();

            if (NAMECREDIT.equals(elNameCredit.getName())) {

                NameCreditWs2 nameCredit = new NameCreditWs2();

                Iterator itrNameCreditAttributes = elNameCredit.getAttributes().iterator();
                while (itrNameCreditAttributes.hasNext()) {
                      Attribute attribute = (Attribute)itrNameCreditAttributes.next();

                      if (attribute.getName().equals(JOINPHRASE)){
                            nameCredit.setJoinphrase(attribute.getValue());
                      }
                      else log.warn("Unrecognised Name Credit attribute: "+attribute.getName());
                }

                Iterator itrArtistOrName = elNameCredit.getChildren().iterator();

                Element elArtistOrName;
                while(itrArtistOrName.hasNext()) 
                {
                        elArtistOrName = (Element)itrArtistOrName.next();

                        if (NAME.equals(elArtistOrName.getName())) {
                            nameCredit.setName(elArtistOrName.getText());

                            Iterator itrNameAttributes = elArtistOrName.getAttributes().iterator();
                            while (itrNameAttributes.hasNext()) {
                                Attribute attribute = (Attribute)itrNameAttributes.next();
                                log.warn("Unrecognised NameCredit, name attribute: "+attribute.getName());
                            }
                        }
                        else if (ARTIST.equals(elArtistOrName.getName())){
                            nameCredit.setArtist(createArtist(elArtistOrName));
                        }
                        else
                        {
                            log.warn("Unrecognised Name Credit element: "+elArtistOrName.getName());
                        }
                }
                nameCredits.add(nameCredit);
            }
            else
            {
                log.warn("Unrecognised Artist Credit element: "+elNameCredit.getName());
            }
        }
        return new ArtistCreditWs2(nameCredits);
    }
    protected LifeSpanWs2 createLifeSpan(Element node) {
        String begin=null;
        String end=null;

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();
              log.warn("Unrecognised LifeSpan attribute: "+attribute.getName());
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (BEGIN.equals(child.getName())) {
                    begin=(child.getText());

            }
            else if (END.equals(child.getName())) {
                    end=(child.getText());
            }
            else
            {
                    log.warn("Unrecognised Life Span element: "+child.getName());
            }
        }
        return new LifeSpanWs2(begin, end);
}
    protected LabelInfoListWs2 createLabelInfoList(Element node) {
        List<LabelInfoWs2> LabelInfos
            = new ArrayList<LabelInfoWs2>(); 

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(COUNT)||
                   attribute.getName().equals(OFFSET))
              {
                 //ignore (see updateListElement).
              }
              else log.warn("Unrecognised LabelInfoList attribute: "+attribute.getName());
        }

        itr = node.getChildren().iterator();
        Element child;

        while(itr.hasNext()) 
        {
                child = (Element)itr.next();

                Iterator itrC = child.getAttributes().iterator();

                while (itrC.hasNext()) {
                      Attribute attribute = (Attribute)itrC.next();
                      log.warn("Unrecognised LabelInfo attribute: "+attribute.getName());
                }

                if (LABELINFO.equals(child.getName())) {

                        LabelInfoWs2 labelInfo = new LabelInfoWs2();

                        Iterator itr2 = child.getChildren().iterator();

                        Element el;
                        while(itr2.hasNext()) 
                        {
                                el = (Element)itr2.next();

                                if (CATALOGNUMBER.equals(el.getName())) {
                                    labelInfo.setCatalogNumber(el.getText());

                                    Iterator itrC2 = el.getAttributes().iterator();
                                    while (itrC2.hasNext()) {
                                        Attribute attribute = (Attribute)itrC2.next();
                                         log.warn("Unrecognised labelinfo catno attribute: "+attribute.getName());
                                    }
                                }
                                else if (LABEL.equals(el.getName())){
                                    labelInfo.setLabel(createLabel(el));
                                }
                                else
                                {
                                        log.warn("Unrecognised Label Info element: "+child.getName());
                                }
                        }
                        LabelInfos.add(labelInfo);
                }
                else
                {
                        log.warn("Unrecognised Life Info List element: "+child.getName());
                }
        }
        LabelInfoListWs2 out = new LabelInfoListWs2(LabelInfos);
        updateListElement(node, out);

        return out;
    }
    protected MediumListWs2 createMediumList(Element node) {
        List<MediumWs2> media
            = new ArrayList<MediumWs2>(); 

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(COUNT)||
                   attribute.getName().equals(OFFSET)){
                 //ignore (see updateListElement).
              }
              else log.warn("Unrecognised MediumList attribute: "+attribute.getName());
        }

        int trackCount =0;
        long duration=0L;

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()){
            
            child = (Element)itr.next();

            if (TRACKCOUNT.equals(child.getName())){
                trackCount = getInt(child.getText());
            }
            else if (MEDIUM.equals(child.getName())) {
                MediumWs2 m = createMedium(child);
                media.add(m); 
            }
            else{
                    log.warn("Unrecognised Medium List element: "+child.getName());
            }
        }
        MediumListWs2 out = new MediumListWs2(media);
        if (trackCount==0) {
            for (MediumWs2 m : media){
                trackCount=trackCount+m.getTracksCount();
            }
        }
        out.setTracksCount(trackCount);
        updateListElement(node, out);

        return out;
    }
    protected TrackListWs2 createTrackList(Element node, MediumWs2 medium){
        
        List<TrackWs2> tracks= new ArrayList<TrackWs2>(); 

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();
              if (attribute.getName().equals(COUNT)||
                   attribute.getName().equals(OFFSET)){
                 //ignore (see updateListElement).
              }
              else log.warn("Unrecognised TrackList attribute: "+attribute.getName());
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()){
            child = (Element)itr.next();
            if (TRACK.equals(child.getName())) {
                tracks.add(createTrack(child,medium));
            }
            else {
                    log.warn("Unrecognised TrackList element: "+child.getName());
            }
        }
        TrackListWs2 out = new TrackListWs2(tracks);
        updateListElement(node, out);

        return out;
   }
    protected DiscListWs2 createDiscList(Element node,MediumWs2 medium){
        List<DiscWs2> discs
            = new ArrayList<DiscWs2>(); 

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();
              if (attribute.getName().equals(COUNT)||
                   attribute.getName().equals(OFFSET))
              {
                 //ignore (see updateListElement).
              }
              else log.warn("Unrecognised DiscList attribute: "+attribute.getName());
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (DISC.equals(child.getName())) 
            {
                discs.add(createDisc(child,medium));
            }
            else
            {
                    log.warn("Unrecognised Disc List element: "+child.getName());
            }
        }
        DiscListWs2 out = new DiscListWs2(discs);
        updateListElement(node, out);

        return out;
   }
    protected MediumWs2 createMedium(Element node){
        MediumWs2 medium = new MediumWs2();

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();
              log.warn("Unrecognised Medium attribute: "+attribute.getName());
        }

        itr = node.getChildren().iterator();

        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();

            if (TITLE.equals(child.getName())) 
            {
                medium.setTitle(child.getText());
            }
            else if (POSITION.equals(child.getName()))
            {
                medium.setPosition(getInt(child.getText()));
            }
            else if (FORMAT.equals(child.getName()))
            {
                medium.setFormat(child.getText());
            }
            else if (TRACKLIST.equals(child.getName()))
            {
                medium.setTracksCount(getInt(child.getAttributeValue(COUNT)));
                medium.setTrackList(createTrackList(child,medium));

            }
            else if (DISCLIST.equals(child.getName()))
            {
                medium.setDiscCount(getInt(child.getAttributeValue(COUNT)));
                medium.setDiscList(createDiscList(child,medium));
            }
            else
            {
                    log.warn("Unrecognised Medium element: "+child.getName());
            }
        }
        return medium;
   }
    protected TrackWs2 createTrack(Element node,MediumWs2 medium){
        TrackWs2 t = new TrackWs2();
        t.setMedium(medium);

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();
              log.warn("Unrecognised Track attribute: "+attribute.getName());
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (POSITION.equals(child.getName())) {
                t.setPosition(getInt(child.getText()));
            }
            else if (TITLE.equals(child.getName())) {
                t.setTitle(child.getText());
            }
            else if (RECORDING.equals(child.getName())) {
                t.setRecording(createRecording(child));
            }
            else if (LENGTH.equals(child.getName())) 
            {
                String d = child.getValue();
                if (d != null) {
                    try {
                            Long l = Long.parseLong(d);
                            t.setDurationInMillis(l);
                    } catch (NumberFormatException e) {
                            log.warn("Illegal duration value!", e);
                    }
                }	
            }
            else
            {
                    log.warn("Unrecognised Track element: "+child.getName());
            }
        }
        return t;
    }
    protected DiscWs2 createDisc(Element node) {
        return createDisc(node, null);
        
    }
    protected DiscWs2 createDisc(Element node,MediumWs2 medium) {
        DiscWs2 disc = new DiscWs2();
         disc.setMedium(medium);
       // d.setDiscId(node.getAttributeValue(ID));

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

            Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(ID))
              {
                   disc.setDiscId(attribute.getValue());
              }
              else
              {
                log.warn("Unrecognised Disk attribute: "+attribute.getName());
              }
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (SECTORS.equals(child.getName())) {
                disc.setSectors(getInt(child.getText()));
            }
            else if (RELEASELIST.equals(child.getName())) {
                addReleasesToList(child, disc.getReleaseList());
            }
            else
            {
                    log.warn("Unrecognised Disc element: "+child.getName());
            }
        }
        return disc;
    }
    protected PuidWs2 createPuid(Element node){
        PuidWs2 puid = new PuidWs2();
        //puid.setId(node.getAttributeValue(ID));

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

            Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(ID))
              {
                   puid.setId(attribute.getValue());
              }
              else
              {
                log.warn("Unrecognised Puid attribute: "+attribute.getName());
              }
        }
        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (RECORDINGLIST.equals(child.getName())) {
                addRecordingToList(child, puid.getRecordingList());
            }
            else
            {
                    log.warn("Unrecognised Puid element: "+child.getName());
            }
        }
        return puid;	
    }
    protected IsrcWs2 createIsrc(Element node) {
        IsrcWs2 isrc = new IsrcWs2();
        //isrc.setId(node.getAttributeValue(ID));

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

            Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(ID))
              {
                   isrc.setId(attribute.getValue());
              }
              else
              {
                log.warn("Unrecognised Isrc attribute: "+attribute.getName());
              }
        }

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (RECORDINGLIST.equals(child.getName())) {
                addRecordingToList(child, isrc.getRecordingList());
            }
            else
            {
                    log.warn("Unrecognised Isrc element: "+child.getName());
            }
        }
        return isrc;	
    }
    protected RatingsWs2 createRating(Element node) {
        RatingsWs2 rating = new RatingsWs2();

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

            Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(VOTESCOUNT))
              {
                    try {
                        rating.setVotesCount(attribute.getLongValue());
                    } catch (DataConversionException ex) {
                        Logger.getLogger(JDOMParserWs2.class.getName()).log(Level.SEVERE, null, ex);
                    }
              }
              else
              {
                log.warn("Unrecognised Rating attribute: "+attribute.getName());
              }
        }
        rating.setAverageRating(Float.parseFloat(node.getValue()));

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            log.warn("Unrecognised Rating element: "+child.getName());
        }

        return rating;	
    }
    protected RatingsWs2 createUserRating(Element node) {
        RatingsWs2 rating = new RatingsWs2();

        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

            Attribute attribute = (Attribute)itr.next();
            log.warn("Unrecognised Rating attribute: "+attribute.getName());

        }
        rating.setAverageRating(Float.parseFloat(node.getValue()));

        itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            log.warn("Unrecognised Rating element: "+child.getName());
        }

        return rating;	
    }
    
    protected void addLabelResults(Element node, 
                         LabelSearchResultsWs2 labelResults){
        if (labelResults == null)
            throw new NullPointerException();

        updateListElement(node, labelResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            LabelResultWs2 r = new LabelResultWs2();
            r.setLabel(createLabel(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getLabel() != null) {
                    labelResults.addLabelResult(r);
            }
        }
    }
    protected void addArtistResults(Element node, 
                         ArtistSearchResultsWs2 artistResults){
        if (artistResults == null)
            throw new NullPointerException();

        updateListElement(node, artistResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            ArtistResultWs2 r = new ArtistResultWs2();
            r.setArtist(createArtist(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getArtist() != null) {
                    artistResults.addArtistResult(r);
            }
        }
    }
    protected void addReleaseGroupResults(Element node, 
                         ReleaseGroupSearchResultsWs2 releaseGroupResults){
        if (releaseGroupResults == null)
            throw new NullPointerException();

        updateListElement(node, releaseGroupResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            ReleaseGroupResultWs2 r = new ReleaseGroupResultWs2();
            r.setReleaseGroup(createReleaseGroup(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getReleaseGroup() != null) {
                    releaseGroupResults.addReleaseGroupResult(r);
            }
        }
    }
    protected void addReleaseResults(Element node, 
                         ReleaseSearchResultsWs2 releaseResults){
        if (releaseResults == null)
            throw new NullPointerException();

        updateListElement(node, releaseResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            ReleaseResultWs2 r = new ReleaseResultWs2();
            r.setRelease(createRelease(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getRelease() != null) {
                    releaseResults.addReleaseResult(r);
            }
        }
    }
    protected void addWorkResults(Element node, 
                         WorkSearchResultsWs2 workResults){
        if (workResults == null)
            throw new NullPointerException();

        updateListElement(node, workResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            WorkResultWs2 r = new WorkResultWs2();
            r.setWork(createWork(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getWork() != null) {
                    workResults.addWorkResult(r);
            }
        }
    }
    protected void addRecordingResults(Element node, 
                         RecordingSearchResultsWs2 recordingResults){
        if (recordingResults == null)
            throw new NullPointerException();

        updateListElement(node, recordingResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            RecordingResultWs2 r = new RecordingResultWs2();
            r.setRecording(createRecording(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getRecording() != null) {
                recordingResults.addRecordingResult(r);
            }
        }
    }
    protected void addCollectionResults(Element node, 
                         CollectionSearchResultsWs2 collectionResults){
        if (collectionResults == null)
            throw new NullPointerException();

        updateListElement(node, collectionResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            CollectionResultWs2 r = new CollectionResultWs2();
            r.setCollection(createCollection(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getCollection() != null) {
                    collectionResults.addCollectionResult(r);
            }
        }
    }
   protected void addAnnotationResults(Element node, 
                         AnnotationSearchResultsWs2 annotationResults){
        if (annotationResults == null)
            throw new NullPointerException();

        updateListElement(node, annotationResults);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            AnnotationResultWs2 r = new AnnotationResultWs2();
            r.setAnnotation(createAnnotation(child));
            r.setScore(getIntAttr(child, SCORE, NS_EXT_2));
            if (r.getAnnotation() != null) {
                    annotationResults.addAnnotationResult(r);
            }
        }
    }

    protected void addAliases(Element node, EntityWs2 entity){
        
        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {

              Attribute attribute = (Attribute)itr.next();

              if (attribute.getName().equals(COUNT)){
                    //ignore
              }
              else{
                log.warn("Unrecognised AliasList attribute: "+attribute.getName());
              }
        }
        itr = node.getChildren().iterator();
        Element child;

        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            AliasWs2 alias  = new AliasWs2();

            Iterator itrC = child.getAttributes().iterator();
            while (itrC.hasNext()) {

                  Attribute attribute = (Attribute)itrC.next();

                  if (attribute.getName().equals(LOCALE)){
                        alias.setLocale(getAttr(child, LOCALE, localePattern, "en",null));
                  }
                  else{
                    log.warn("Unrecognised Alias attribute: "+attribute.getName());
                  }
            }
            if (ALIAS.equals(child.getName())){
                alias.setValue(child.getText());
                entity.addAlias(alias);
            }
            else{
                log.warn("Unrecognised Alias element: "+child.getName());
            }
        }
    }
    protected void addTags(Element node, EntityWs2 entity) {
        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
            Attribute attribute = (Attribute)itr.next();
            log.warn("Unrecognised TagList attribute: "+attribute.getName());
        }
        itr = node.getChildren().iterator();
        Element child;

        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            TagWs2 tag  = new TagWs2();

            Iterator itrC = child.getAttributes().iterator();
            while (itrC.hasNext()) {

                Attribute attribute = (Attribute)itrC.next();

                if (attribute.getName().equals(COUNT))
                {
                    try {
                        tag.setCount(attribute.getLongValue());
                    } catch (DataConversionException ex) {
                        Logger.getLogger(JDOMParserWs2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                log.warn("Unrecognised Tag attribute: "+attribute.getName());
                }
            }
            itrC = child.getChildren().iterator();
            Element el;
            while(itrC.hasNext()) 
            {
                el = (Element)itrC.next();
                Iterator itrE = el.getAttributes().iterator();
                while (itrE.hasNext()) {
                      Attribute attribute = (Attribute)itrE.next();
                      log.warn("Unrecognised TagName attribute: "+attribute.getName());
                }

                if (NAME.equals(el.getName())){
                    tag.setName(el.getText());
                    entity.addTag(tag);
                }
                else {
                    log.warn("Unrecognised Tag element: "+el.getName());
                }
            }
        }
    }
    protected void addUserTags(Element node, EntityWs2 entity) {
        Iterator itr = node.getAttributes().iterator();
        while (itr.hasNext()) {
              Attribute attribute = (Attribute)itr.next();
             log.warn("Unrecognised UserTagList attribute: "+attribute.getName());
        }
        itr = node.getChildren().iterator();
        Element child;

        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            TagWs2 tag  = new TagWs2();

            Iterator itrC = child.getAttributes().iterator();
            while (itrC.hasNext()) {

                  Attribute attribute = (Attribute)itrC.next();
                    log.warn("Unrecognised userTag attribute: "+attribute.getName());
            }
            itrC = child.getChildren().iterator();
            Element el;
            while(itrC.hasNext()){
                el = (Element)itrC.next();
                Iterator itrE = el.getAttributes().iterator();
                while (itrE.hasNext()) {
                    Attribute attribute = (Attribute)itrE.next();
                    log.warn("Unrecognised TagName attribute: "+attribute.getName());
                }

                if (NAME.equals(el.getName())) 
                {
                    tag.setName(el.getText());
                    entity.addUserTag(tag);
                }
                else
                {
                    log.warn("Unrecognised userTag element: "+el.getName());
                }
            }
        }
    }
    
    protected void addLabelsToList(Element node, 
                            LabelListWs2 labelList){
        if (labelList == null)
            throw new NullPointerException();

        updateListElement(node, labelList);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (LABEL.equals(child.getName())) {
                    labelList.addLabel(createLabel(child));
            }
        }
    }
    protected void addArtistToList(Element node, 
                            ArtistListWs2 artistList){
        if (artistList == null)
            throw new NullPointerException();

        updateListElement(node, artistList);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) {
            child = (Element)itr.next();
            if (ARTIST.equals(child.getName())) {
                    artistList.addArtist(createArtist(child));
            }
        }
    }
    protected void addReleaseGroupsToList(Element node, 
                            ReleaseGroupListWs2 releaseGroupList){
            if (releaseGroupList == null)
                throw new NullPointerException();

            updateListElement(node, releaseGroupList);

            Iterator itr = node.getChildren().iterator();
            Element child;
            while(itr.hasNext()){
                child = (Element)itr.next();
                if (RELEASEGROUP.equals(child.getName())) {
                        releaseGroupList.addReleaseGroup(createReleaseGroup(child));
                }
            }
    }
    protected void addReleasesToList(Element node, 
                            ReleaseListWs2 releaseList) {
        if (releaseList == null)
            throw new NullPointerException();

        updateListElement(node, releaseList);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()){
            child = (Element)itr.next();
            if (RELEASE.equals(child.getName())) {
                    releaseList.addRelease(createRelease(child));
            }
        }
    }
    protected void addWorkToList(Element node, 
                            WorkListWs2 workList) {
        if (workList == null)
            throw new NullPointerException();

        updateListElement(node, workList);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()){
            child = (Element)itr.next();
            if (WORK.equals(child.getName())) {
                    workList.addWorks(createWork(child));
            }
        }
    }
    protected void addRecordingToList(Element node, 
                            RecordingListWs2 recordingList) {
        if (recordingList == null)
            throw new NullPointerException();

        updateListElement(node, recordingList);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (RECORDING.equals(child.getName())) {
                    recordingList.addRecording(createRecording(child));
            }
        }
    }
    protected void addRelationsToEntity(Element node, 
                            RelationListWs2 relationList) {
        
        String targetType = MbUtils.convertTypeToURI(
            node.getAttributeValue(TARGETTYPE), EntityWs2.NS_REL_2_PREFIX);
        
        if (targetType == null) {
                return;
        }

        if (relationList == null)
                throw new NullPointerException();

        updateListElement(node, relationList);

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()){
            child = (Element)itr.next();
            RelationWs2 rel;
            if (RELATION.equals(child.getName())) {
                    rel = createRelation(child, targetType);
                    if (rel != null) {
                            relationList.addRelation(rel);
                    }
            }
        }
    }

    protected void addPuidsToRecording(Element node, 
                            RecordingWs2 recording) {
        if (recording == null)
            throw new NullPointerException();

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (PUID.equals(child.getName())) {
                    recording.addPuid(createPuid(child));
            }
        }
    }
    protected void addIsrcsToRecording(Element node, 
                            RecordingWs2 recording) {
        if (recording == null)
            throw new NullPointerException();

        Iterator itr = node.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            if (ISRC.equals(child.getName())) {
                    recording.addIsrc(createIsrc(child));
            }
        }
    }

    protected void updateListElement(Element node, ListElement le) {
        le.setCount(getLongAttr(node, COUNT));
        le.setOffset(getLongAttr(node, OFFSET));
    }

    protected String getAttr(Element element, String attrName) {
            return getAttr(element, attrName, null, null, null);
    }

    protected String getAttr(Element element, String attrName, Pattern regex) {
        return getAttr(element, attrName, regex, null, null);
    }

    /**
     * Returns an attribute of the given element.
     * 
     * If there is no attribute with that name or the attribute doesn'recording
     * match the regular expression, default is returned.
     * 		
     * @param element
     * @param attrName
     * @param regex
     * @param def
     * @param ns
     * @return A String
     */
    protected String getAttr(Element element, 
                                        String attrName, 
                                        Pattern regex, 
                                        String def, 
                                        Namespace ns){
        Attribute at;
        if (ns != null) {
            at = element.getAttribute(attrName, ns);
        } 
        else {
            at = element.getAttribute(attrName);
        }

        if (at != null){
            String txt = at.getValue();
            return getText(txt,regex,def);
        }
        return def;
    }
    
    protected String getText(String txt, Pattern regex, String def){
        if (txt != null &&  regex != null) {
            Matcher m = regex.matcher(txt);
            if (m.matches()) {
                    return txt;
            }
            else {
                    return def;
            }
        }
        return txt;
   }

    protected Long getLongAttr(Element element, String attrName){
        String value=getAttr(element, attrName);
        if (value != null) {
            try {
                    return Long.parseLong(value);
            } catch (NumberFormatException e) {
                    return null;
            }
        }
        return null;
    }
    protected Integer getIntAttr(Element element, String attrName){
        return getIntAttr(element, attrName, null);
    }

    protected Integer getIntAttr(Element element, 
                                            String attrName, 
                                            Namespace ns) {
        String value=getAttr(element, attrName, null, null, ns);
        return getInt(value);
    }

    protected List<String> getAttributeList(Element element){
        List<String> out = new ArrayList<String>();

        Iterator itr = element.getChildren().iterator();
        Element child;
        while(itr.hasNext()) 
        {
            child = (Element)itr.next();
            String s =child.getText();         
            out.add(s);
        }
        return out;
    }
    
    private int getInt(String str){

        if (str != null) {
                try {
                        return Integer.parseInt(str);
                } catch (NumberFormatException e) {
                        return 0;
                }
        }
        return 0;
   }
}
