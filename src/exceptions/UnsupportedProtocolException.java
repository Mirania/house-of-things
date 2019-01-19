package exceptions;

public class UnsupportedProtocolException extends Exception {

	private static final long serialVersionUID = -5124829222273730673L;

	public UnsupportedProtocolException(String p) {
		super("Protocol '"+p+"' is not yet supported.");
	}
}
