package exceptions;

public class NoSuchElementException extends Exception {

	private static final long serialVersionUID = -2629618654520640838L;

	public NoSuchElementException(String n) {
		super("No element with the identifier '"+n+"' could be found.");
	}
}
