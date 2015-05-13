package pl.mokaz.ratio.exceptions;

public class RatioNotAvailableForDate extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4411207603001671895L;

	public RatioNotAvailableForDate(String date) {
		super("Could not find file for date " + date);
	}

}
