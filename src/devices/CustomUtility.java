package devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class CustomUtility implements Programmable {
	
	private Device d;
	private Random random;
	private List<Tuple<String,Double>> numericValues;
	private List<Tuple<String, String>> textValues;
	
	public CustomUtility() {
		d = createInstance();
		numericValues = new ArrayList<>();
		textValues = new ArrayList<>();
	}

	@Override
	public Device createInstance() {
		return new Device("Custom", this);
	}

	@Override
	public void setDevice(Device d) {
		this.d = d;
	}

	@Override
	public Device getDevice() {
		return d;
	}
	
	public boolean isOn() {
		return d.isOn();
	}

	@Override
	public void setOn() throws IllegalOperationException {
		d.setOn();
		if (d.booleanSender) d.notifyListeners(true);
	}
	
	@Override
	public void setOff() throws IllegalOperationException {
		d.setOff();
		if (d.booleanSender) d.notifyListeners(false);
	}
	
	@Override
	public void sendValue() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Send data while OFF");
		notifyListeners(d.getName(), computeValue());
	}
	
	public void sendMessage() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Send data while OFF");
		notifyListeners(d.getName(), computeReadableValue());
	}
	
	private double computeValue() {
		random = new Random();
		return random.nextDouble()*100f; // [0.0, 100.0[
	}
	
	private String computeReadableValue() {
		return computeValue()+"%";
	}
	
	private void notifyListeners(String name, String msg) throws IllegalOperationException {
		if (!d.stringSender) throw new IllegalOperationException(getName(), "Notify target devices");
		d.notifyListeners(name, msg);
	}
	
	private void notifyListeners(String name, double val) throws IllegalOperationException {
		if (!d.doubleSender) throw new IllegalOperationException(getName(), "Notify target devices");
		d.notifyListeners(name, val);
	}
	
	@Override
	public void update(String key, String msg) throws IllegalOperationException {
		if (!d.stringListener) throw new IllegalOperationException(d.getName(), "Update message history");
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Update message history while OFF");
		textValues.add(new Tuple<String,String>(key, msg));
	}
	
	@Override
	public void update(String key, double val) throws IllegalOperationException {
		if (!d.doubleListener) throw new IllegalOperationException(d.getName(), "Update numeric values");
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Update numeric values while OFF");
		numericValues.add(new Tuple<String,Double>(key, val));
	}
	
	@Override
	public void update(boolean b) throws IllegalOperationException {
		if (!d.booleanListener) throw new IllegalOperationException(getName(), "Update power status");
		if (b) d.setOn();
		else d.setOff();
	}
	
	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException { 
		return v.visitCustom(this);
	}
	
	public String printAllTextualData() throws IllegalOperationException {
		if (!d.stringListener) throw new IllegalOperationException(d.getName(), "Show textual data");
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Show textual data while OFF");
		StringBuilder sb = new StringBuilder("'"+d.getName()+"' full data:\n");
		for (Tuple<String,String> k: textValues) {
			sb.append(String.format("[%s] -> %s\n",k.getKey(),k.getValue()));
		}
		if (textValues.size()==0) sb.append("- empty -");
		return sb.toString();
	}
	
	public String printAllNumericData() throws IllegalOperationException {
		if (!d.doubleListener) throw new IllegalOperationException(d.getName(), "Show numeric data");
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Show numeric data while OFF");
		StringBuilder sb = new StringBuilder("'"+d.getName()+"' full data:\n");
		for (Tuple<String,Double> k: numericValues) {
			sb.append(String.format("[%s] -> %2f\n",k.getKey(),k.getValue()));
		}
		if (numericValues.size()==0) sb.append("- empty -");
		return sb.toString();
	}
	
	public List<Tuple<String,Double>> getNumericValues() {
		return numericValues;
	}
	
	public void setNumericValues(List<Tuple<String,Double>> list) {
		this.numericValues = list;
	}
	
	public List<Tuple<String,String>> getTextValues() {
		return textValues;
	}
	
	public void setTextValues(List<Tuple<String,String>> list) {
		this.textValues = list;
	}
	
	public Programmable copy() {
        CustomUtility x = new CustomUtility();
        try {
        	Device z = x.getDevice();
        	if (d.booleanListener) z.booleanListener = true;
        	if (d.booleanSender) z.booleanSender = true;
        	if (d.doubleListener) z.doubleListener = true;
        	if (d.doubleSender) z.doubleSender = true;
        	if (d.stringListener) z.stringListener = true;
        	if (d.stringSender) z.stringSender = true;
        	x.setName(this.getName());
        	if (this.isOn()) z.setOn();
        	x.setProtocol(this.getProtocol());
			if (z.booleanSender || z.doubleSender || z.stringSender) 
				x.setListeners(new ArrayList<Programmable>(this.getListeners()));
			if (z.booleanListener || z.doubleListener || z.stringListener) 
				x.setSenders(new ArrayList<Programmable>(this.getSenders()));
			if (z.doubleListener) x.setNumericValues(new ArrayList<Tuple<String,Double>>(this.getNumericValues()));
			if (z.stringListener) x.setTextValues(new ArrayList<Tuple<String,String>>(this.getTextValues()));
		} catch (IllegalOperationException e) {
			return null;
		}
        
        return x;
    }
	
	public String toString() {
		return d.toString();
	}

	private class Tuple<T,E> {
		
		private T key;
		private E val;
		
		public Tuple(T t, E e) {
			key = t;
			val = e;
		}
		
		public T getKey() {
			return key;
		}
		
		public E getValue() {
			return val;
		}
		
	}
}
