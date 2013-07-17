package info.schnatterer.newsic.service.event;

import java.util.EventListener;

/**
 * The listener interface for receiving notifications that an artist has been
 * processed.
 * 
 * The class that is interested in processing an action event implements this
 * interface, and the object created with that class is registered with a
 * component, using the component's <code>addArtistProcessedListener</code>
 * method. When the action event occurs, that object's
 * <code>artistProcessed</code> method is invoked.
 * 
 * @see <a
 *      href="http://java.sun.com/docs/books/tutorial/post1.0/ui/eventmodel.html">Tutorial:
 *      Java 1.1 Event Model</a>
 * 
 * @author schnatterer
 */
public interface ArtistProcessListener extends EventListener {
	/**
	 * Invoked when an artist is processed.
	 * 
	 * @param artist
	 * @param artistNumber
	 * @param potentialException
	 */
	void artistProcessed(ArtistProcessEvent event);

}
