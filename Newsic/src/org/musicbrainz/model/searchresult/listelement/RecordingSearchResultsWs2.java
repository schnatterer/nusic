package org.musicbrainz.model.searchresult.listelement;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.listelement.RecordingListWs2;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.musicbrainz.wsxml.element.ListElement;

public class RecordingSearchResultsWs2 extends ListElement{

    protected List<RecordingResultWs2> recordingResults = new ArrayList<RecordingResultWs2>();
    private RecordingListWs2 recordingList = new RecordingListWs2();
    
    public List<RecordingResultWs2> getRecordingResults() {
            return recordingResults;
    }

    public RecordingListWs2 getRecordingList() {
        return recordingList;
    }

    /**
     * Convenience method to adds an track result to the list.
     * 
     * This will create a new <code>ArrayList</code> if {@link #recordingResults} is null.
     * 
     * @param trackResult The track result to add
     */
    public void addRecordingResult(RecordingResultWs2 recordingResult) 
    {
            if (recordingResults == null) {
                    recordingResults = new ArrayList<RecordingResultWs2>();
            }
            if (recordingList == null) {
                recordingList = new RecordingListWs2();
            }
            recordingResults.add(recordingResult);
            recordingList.addRecording(recordingResult.getRecording());
        
            recordingList.setCount(getCount());
            recordingList.setOffset(getOffset());
    }
}
