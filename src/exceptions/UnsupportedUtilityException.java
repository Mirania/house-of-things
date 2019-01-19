package exceptions;

public class UnsupportedUtilityException extends Exception {

	private static final long serialVersionUID = -4283276207981745589L;

	public UnsupportedUtilityException(String type) {
		super("A utility of the type '"+type+"' could not be created.");
	}
	
}