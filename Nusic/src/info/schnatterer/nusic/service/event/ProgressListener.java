package info.schnatterer.nusic.service.event;

import java.util.EventListener;

/**
 * The listener interface for receiving notifications about the progress of some
 * process that batch process a set of (generic) entities and finally returns a
 * another set of entites.
 * 
 * The class that is interested in processing the evetns implements this
 * interface, and the object created with that class is registered with a
 * component, using the component's <code>addProgressListener</code> method.
 * When the action event occurs, that object's progress methods (e.g.
 * {@link #onProgress(Object, int, int, Throwable)}) are invoked.
 * 
 * @see <a
 *      href="http://java.sun.com/docs/books/tutorial/post1.0/ui/eventmodel.html">Tutorial:
 *      Java 1.1 Event Model</a>
 * 
 * @author schnatterer
 */
public interface ProgressListener<PROCESSED_ENTITY, RESULT_ENTITY> extends
		EventListener {

	void onProgressStarted(int nEntities);

	/**
	 * The main progress method.
	 * 
	 * @param entity
	 * @param progress
	 * @param max
	 * @param potentialException
	 *            a non-fatal exception that might have occurred during
	 *            progressing this specific instance of the entity but didn't
	 *            fail the whole progress
	 */
	void onProgress(PROCESSED_ENTITY entity, int progress, int max,
			Throwable potentialException);

	void onProgressFinished(RESULT_ENTITY result);

	/**
	 * A fatal error occurred, stopping the process.
	 * 
	 * @param entity
	 * @param progress
	 * @param max
	 * @param resultOnFailure
	 *            the intermediate result, when the error occured.
	 * @param potentialException
	 */
	void onProgressFailed(PROCESSED_ENTITY entity, int progress, int max,
			RESULT_ENTITY resultOnFailure, Throwable potentialException);
}
