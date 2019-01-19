package exceptions;

//example: trying to read data from a device that is OFF
public class IllegalOperationException extends Exception {

	private static final long serialVersionUID = -2714606647206871860L;

	public IllegalOperationException(String name, String operation) {
		super("The device '"+name+"' could not perform the action ["+operation+"].");
	}
}
