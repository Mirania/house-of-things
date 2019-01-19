package devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class TemperatureAggregator implements Programmable {
	
	private Map<String,Double> temps;
	private Device d;
	
	public TemperatureAggregator() {
	    d = createInstance();
	    temps = new HashMap<>();
	}
	
	@Override
	public void update(boolean b) {
		if (b) d.setOn();
		else d.setOff();
	}
	
	@Override
	public void update(String key, double val) throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Read data while OFF");
		temps.put(key, val);
	}

	public String printAll() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Show data while OFF");
		StringBuilder sb = new StringBuilder("Temp. aggregator '"+d.getName()+"' full data:\n");
		for (String k: temps.keySet()) {
			sb.append(String.format("%s -> %.2f\n",k,temps.get(k)));
		}
		if (temps.keySet().size()==0) sb.append("- none -");
		return sb.toString();
	}
	
	public String printAvgTemp() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Show data while OFF");
		if (temps.keySet().size()==0) return String.format("Temp. aggregator '"+d.getName()+"' registers:\nno data");
		return String.format("Temp. aggregator '"+d.getName()+"' registers:\n%.2f average",avgTemp());
	}
	
	public double avgTemp() throws IllegalOperationException {
		if (!d.isOn()) throw new IllegalOperationException(d.getName(), "Show data while OFF");
		double x = 0;
		Set<String> set = temps.keySet();
		for (String k: set) {
			x += temps.get(k);
		}
		x /= set.size();
		return x;
	}

	@Override
	public Device createInstance() {
		return new BooleanListener(new DoubleListener((new Device("TempAggregator", this)))).getDevice();
	}

	@Override
	public Device getDevice() {
		return d;
	}
	
	@Override
	public void setDevice(Device d) {
		this.d = d;
	}
	
	public void setTemps(Map<String,Double> temps) {
		this.temps = temps;
	}
	
	public Map<String,Double> getTemps() {
		return temps;
	}
	
	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException { 
		return v.visitTempAgg(this);
	}
	
	public Programmable copy() {
        TemperatureAggregator x = new TemperatureAggregator();
        try {
        	x.setName(this.getName());
        	if (this.isOn()) x.getDevice().setOn();
        	x.setProtocol(this.getProtocol());
			x.setSenders(new ArrayList<Programmable>(this.getSenders()));
			x.setTemps(new HashMap<String,Double>(this.getTemps()));
		} catch (IllegalOperationException e) {
			return null;
		}
        
        return x;
    }
	
	public String toString() {
		return d.toString();
	}
}
