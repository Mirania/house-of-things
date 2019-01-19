package devices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import interfaces.Programmable;
import interfaces.Visitor;

public class LuminosityHistory implements Programmable {

	private List<String> list;
	private Device d;

	public LuminosityHistory() {
		d = createInstance();
		list = new ArrayList<>();
	}

	@Override
	public void update(boolean b) {
		if (b)
			d.setOn();
		else
			d.setOff();
	}

	@Override
	public void update(String key, String msg) throws IllegalOperationException {
		if (!d.isOn())
			throw new IllegalOperationException(d.getName(), "Read data while OFF");
		list.add(makeEntry(key, msg));
	}

	public String printAll() throws IllegalOperationException {
		if (!d.isOn())
			throw new IllegalOperationException(d.getName(), "Show data while OFF");
		StringBuilder sb = new StringBuilder("Lumi. history '" + d.getName() + "' full data:\n");
		for (String k : list) {
			sb.append(k+"\n");
		}
		if (list.size() == 0)
			sb.append("- none -");
		return sb.toString();
	}

	@Override
	public Device createInstance() {
		return new StringListener(new BooleanListener(new Device("LumiHistory", this))).getDevice();
	}

	@Override
	public Device getDevice() {
		return d;
	}
	
	public void setList(List<String> list) {
		this.list = list;
	}

	@Override
	public void setDevice(Device d) {
		this.d = d;
	}

	public List<String> getHistory() {
		return list;
	}

	public <T> T acceptVisitor(Visitor<T> v) throws IllegalOperationException, IncompatibleProtocolException {
		return v.visitLumiH(this);
	}
	
	public Programmable copy() {
        LuminosityHistory x = new LuminosityHistory();
        try {
        	x.setName(this.getName());
        	if (this.isOn()) x.getDevice().setOn();
        	x.setProtocol(this.getProtocol());
			x.setSenders(new ArrayList<Programmable>(this.getSenders()));
			x.setList(new ArrayList<String>(this.list));
		} catch (IllegalOperationException e) {
			return null;
		}
        
        return x;
    }

	public String toString() {
		return d.toString();
	}

	public String makeEntry(String name, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d yyyy HH:mm");
		return String.format("At %s, '%s' reported it was '%s'.", sdf.format(new Date()), name, msg);
	}

}
