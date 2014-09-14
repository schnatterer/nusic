package org.musicbrainz.includes;

import java.util.List;

/**
 * A specification on how much data to return with a work.
 */

public class WorkIncludesWs2 extends IncludesWs2
{

      // Misc inc= arguments 

      private boolean aliases = false;
     
    /**
     * Default constructor
     */
    public WorkIncludesWs2()
    {

    }
    /**
     * @return the aliases
     */
    public boolean isAliases() {
        return aliases;
    }
    /**
     * @param aliases the aliases to set
     */
    public void setAliases(boolean aliases) {
        this.aliases = aliases;
    }
    /* (non-Javadoc)
     * @see org.musicbrainz.webservice.Includes#createIncludeTags()
     */
    @Override
    public List<String> createIncludeTags() 
    {
            List<String> includeTags = super.createIncludeTags();

            // not that elegant but straight forward :)
            if (isAliases()) includeTags.add(ALIASES_INC);
            
            return includeTags;
    }
 /**
   * set all the parameters to false.
   */
    @Override
    public void excludeAll(){
        
        super.excludeAll();
        setAliases(false);
        
    }
    /**
   * set all the parameters to true.
   */
    @Override
    public void includeAll(){
        
       super.includeAll();
        setAliases(true);

    }
    /**
   *  clone the current status of IncludesWs2 to a new one.
   */
    @Override
    public WorkIncludesWs2 clone(){
        
        WorkIncludesWs2 target = new WorkIncludesWs2();
        copyTo(target);
        target.setAliases(isAliases());
        return target;
    }

}
