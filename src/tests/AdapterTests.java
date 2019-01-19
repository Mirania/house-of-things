package tests;

import org.junit.Test;
import exceptions.*;
import devices.*;
import static org.junit.Assert.*;
import interfaces.*;

public class AdapterTests {

	@Test
	public void testBAdapterOneWay() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		Lamp l = new Lamp();
		l.setProtocol("Y");
		BooleanAdapter p = new BooleanAdapter(l);
		sw.attach(p);
		sw.setOn();
		assertTrue(sw.isOn());
		assertTrue(l.isOn());
	}
	
	@Test
	public void testBAdapterOneWay2() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		Lamp l = new Lamp();
		l.setProtocol("Y");
		BooleanAdapter p = new BooleanAdapter(sw);
		p.attach(l);
		p.setOn();
		assertTrue(sw.isOn());
		assertTrue(l.isOn());
	}
	
	@Test(expected = IllegalOperationException.class)
	public void failAdapter() throws IncompatibleProtocolException, IllegalOperationException {
		Lamp l = new Lamp();
		BooleanAdapter p = new BooleanAdapter(l);
		p.attach(l);
	}
	
	@Test(expected = IllegalOperationException.class)
	public void failAdapter2() throws IncompatibleProtocolException, IllegalOperationException {
		TemperatureAggregator ta = new TemperatureAggregator();
		DoubleAdapter p = new DoubleAdapter(ta);
		p.sendValue();
	}
	
	@Test(expected = IncompatibleProtocolException.class)
	public void confirmNoClassChanges() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		Lamp l = new Lamp();
		l.setProtocol("Y");
		BooleanAdapter p = new BooleanAdapter(sw);
		sw.attach(l);
	}
	
	@Test
	public void bigDAdapterTestOneWay() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		TemperatureAggregator ta = new TemperatureAggregator();
		ta.setProtocol("X");
		Thermometer t = new Thermometer();	
		t.setProtocol("Y");
		DoubleAdapter dp = new DoubleAdapter(ta);
		BooleanAdapter bp = new BooleanAdapter(t);
		BooleanGroup g = new BooleanGroup();
		g.add(ta);
		g.add(bp);
		sw.attach(g);
		sw.setOn();
		assertTrue(sw.isOn());
		assertTrue(ta.isOn());
		assertTrue(t.isOn());
		t.attach(dp);
		t.sendValue();
		assertEquals(1, ta.getTemps().size());
	}
	
	@Test
	public void bigDAdapterTestOneWay2() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		TemperatureAggregator ta = new TemperatureAggregator();
		ta.setProtocol("X");
		Thermometer t = new Thermometer();	
		t.setProtocol("Y");
		DoubleAdapter dp = new DoubleAdapter(t);
		BooleanAdapter bp = new BooleanAdapter(sw);
		bp.attach(ta);
		bp.attach(t);
		bp.setOn();
		assertTrue(sw.isOn());
		assertTrue(ta.isOn());
		assertTrue(t.isOn());
		dp.attach(ta);
		dp.sendValue();
		assertEquals(1, ta.getTemps().size());
	}
	
	@Test
	public void adapterTriangle() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		TemperatureAggregator ta = new TemperatureAggregator();
		ta.setProtocol("Y");
		Thermometer t = new Thermometer();	
		t.setProtocol("Z");
		DoubleAdapter dp = new DoubleAdapter(ta);
		BooleanAdapter bp = new BooleanAdapter(t);
		BooleanAdapter bp2 = new BooleanAdapter(ta);
		sw.attach(bp);
		sw.attach(bp2);
		sw.setOn();
		assertTrue(sw.isOn());
		assertTrue(ta.isOn());
		assertTrue(t.isOn());
		t.attach(dp);
		t.sendValue();
		assertEquals(1, ta.getTemps().size());
	}
	
	@Test
	public void refactorLogic() throws IllegalOperationException, IncompatibleProtocolException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		Lamp l = new Lamp();
		l.setProtocol("Y");
		BooleanAdapter bp = new BooleanAdapter(l);
		sw.attach(bp);
		sw.setOn();
		assertTrue(l.isOn());
	}
	
	@Test
	public void stringTest() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		LuminosityHistory h = new LuminosityHistory();
		h.setProtocol("X");
		LuminositySensor ls = new LuminositySensor();
		ls.setProtocol("Y");
		StringAdapter sp = new StringAdapter(ls);
		sw.attach(ls);
		sw.attach(h);
		sw.setOn();
		sp.attach(h);
		sp.sendMessage();
		assertEquals(1,h.getHistory().size());
	}
}
