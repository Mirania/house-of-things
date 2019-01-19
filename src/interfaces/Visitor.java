package interfaces;

import devices.*;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;

public interface Visitor<T> {
	
	public T visitOther(Programmable d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitCustom(CustomUtility d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitLock(DoorLock d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitLamp(Lamp d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitSwitch(Switch d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitTempAgg(TemperatureAggregator d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitThermo(Thermometer d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitLumiH(LuminosityHistory d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitLumiS(LuminositySensor d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitBooleanAdapter(BooleanAdapter d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitDoubleAdapter(DoubleAdapter d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitStringAdapter(StringAdapter d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitBooleanGroup(BooleanGroup d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitDoubleGroup(DoubleGroup d) throws IllegalOperationException, IncompatibleProtocolException;
	
	public T visitStringGroup(StringGroup d) throws IllegalOperationException, IncompatibleProtocolException;
	
}
