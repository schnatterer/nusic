package info.schnatterer.newsic.db.loader;

public class AsyncResult<D> {
	private Exception exception;
	private D data;

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

	public void setData(D data) {
		this.data = data;
	}

	public D getData() {
		return data;
	}

}
