package exceptions;

public class IncompatibleProtocolException extends Exception {

	private static final long serialVersionUID = 4897776405854405192L;

	public IncompatibleProtocolException(String n1, String p1, String n2, String p2) {
		super("Devices '"+n1+"' (protocol "+p1+") and '"+n2+"' (protocol "+p2+") are not compatible.");
	}
}
