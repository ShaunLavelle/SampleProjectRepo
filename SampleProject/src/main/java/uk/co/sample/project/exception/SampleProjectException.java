package uk.co.sample.project.exception;

public class SampleProjectException extends RuntimeException {

	private static final long serialVersionUID = -5698157160354262803L;

	public SampleProjectException(String message) {
		super(message);
	}

	public SampleProjectException(Exception cause) {
		super(cause);
	}

}
