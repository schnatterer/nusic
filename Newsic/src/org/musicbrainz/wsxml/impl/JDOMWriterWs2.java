
package org.musicbrainz.wsxml.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.musicbrainz.DomainsWs2;



import org.musicbrainz.query.submission.EntityElement;
import org.musicbrainz.query.submission.EntityTypeElementList;
import org.musicbrainz.query.submission.SubmissionException;
import org.musicbrainz.query.submission.UserRatingSubmissionWs2;
import org.musicbrainz.query.submission.UserTagSubmissionWs2;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.MbXmlWriter;
import org.musicbrainz.wsxml.element.Metadata;

public class JDOMWriterWs2 extends DomainsWs2 implements MbXmlWriter {

    private Log log = LogFactory.getLog(JDOMWriterWs2.class);
    
    /**
    * Default constructor
    */
    public JDOMWriterWs2() {

    }
    public String getXmlString (Metadata metadata) throws MbXMLException{
        String xmlString=null;
        
        if (metadata.getSubmissionWs2() == null) 
            throw new SubmissionException ("Empty Submission not allowed");
        
        if (metadata.getSubmissionWs2() instanceof UserTagSubmissionWs2){
            try {
                xmlString= writeTagRequest(
                        (UserTagSubmissionWs2)metadata.getSubmissionWs2());
            } catch (IOException ex) {
                 throw new MbXMLException(ex);
            }
            
        }
        else if (metadata.getSubmissionWs2() instanceof UserRatingSubmissionWs2){
            try {
                xmlString= writeRatingRequest(
                        (UserRatingSubmissionWs2)metadata.getSubmissionWs2());
            } catch (IOException ex) {
                throw new MbXMLException(ex);
            }
        }
        else throw new SubmissionException("Invalid Submission request");
        
        return xmlString;
    }
    private String writeTagRequest(UserTagSubmissionWs2 sub) throws IOException{
        
        Element root = new Element(METADATA, NS_MMD_2);
        
        for (EntityTypeElementList t : sub.getSubmissionList())
        {
            Element entityList = new Element(t.getEntityListType(), NS_MMD_2);
            
            for (EntityElement e : t.getEntityElementList())
            {
                Element entity = new Element(e.getEntityType(), NS_MMD_2);
                entity.setAttribute(ID, e.getId());
                Element userTagList = new Element(USERTAGLIST, NS_MMD_2);
                
                for (String tag : e.getTagList())
                {
                    Element userTag = new Element(USERTAG, NS_MMD_2);
                    Element name = new Element(NAME, NS_MMD_2);
                    name.setText(tag);
                    
                    userTag.addContent(name);
                    userTagList.addContent(userTag);
                }
                entity.addContent(userTagList);
                entityList.addContent(entity);
            }
            root.addContent(entityList);
        }
        return getXMlString(new Document(root));
    }
    private String writeRatingRequest(UserRatingSubmissionWs2 sub) throws IOException{
        
        Element root = new Element(METADATA, NS_MMD_2);
        
        for (EntityTypeElementList t : sub.getSubmissionList())
        {
            Element entityList = new Element(t.getEntityListType(), NS_MMD_2);
            
            for (EntityElement e : t.getEntityElementList())
            {
                Element entity = new Element(e.getEntityType(), NS_MMD_2);
                entity.setAttribute(ID, e.getId());
                Element userRating = new Element(USERRATING, NS_MMD_2);
                userRating.setText(""+e.getUserRating());
                
                entity.addContent(userRating);
                entityList.addContent(entity);
            }
            root.addContent(entityList);
        }
        return getXMlString(new Document(root));
    }
    private String getXMlString(Document doc) throws IOException{
        
        ByteArrayOutputStream outData=new ByteArrayOutputStream();
        XMLOutputter serializer = new XMLOutputter();
        
        //Enabled for debug purpose.
        //serializer.setFormat(serializer.getFormat().setIndent("  "));
        //serializer.output(doc,System.out);
        //
        
        serializer.output(doc,outData);
        return outData.toString("UTF-8");
    }
}
