package org.musicbrainz.query.submission;

import org.musicbrainz.query.QueryWs2;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.webservice.WebServiceException;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.element.Metadata;


public class SubmissionQueryWs2 extends QueryWs2{

    
    public SubmissionQueryWs2(){
      super();
    }
    
    public SubmissionQueryWs2(WebService ws){
       super(ws);
    }
    public Metadata post(Metadata md) throws WebServiceException, MbXMLException{
        
        return super.postToWebService(md);
    }

}
