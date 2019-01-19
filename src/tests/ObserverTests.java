package tests;

import devices.*;
import exceptions.*;
import interfaces.*;
import static org.junit.Assert.*;
import org.junit.*;

public class ObserverTests {

	@Test
	public void testSwitchLamp() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		Lamp l = new Lamp();
		l.getDevice().setProtocol("Y");
		
		assertFalse(sw.isOn());
		assertFalse(l.isOn());
		sw.attach(l);
		sw.setOn();
		assertTrue(sw.isOn());
		assertTrue(l.isOn());
	}
	
	@Test(expected = IllegalOperationException.class)
	public void failtempAgg() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		Thermometer t = new Thermometer();
		TemperatureAggregator ta = new TemperatureAggregator();
		sw.attach(t);
		t.attach(ta);
		sw.setOn();
		t.sendValue();
	}
	
	@Test
	public void testThermos() throws IllegalOperationException, IncompatibleProtocolException {
		Switch sw = new Switch();
		Thermometer t1 = new Thermometer();
		Thermometer t2 = new Thermometer();
		TemperatureAggregator ta = new TemperatureAggregator();
		sw.attach(t1);
		sw.attach(t2);
		sw.attach(ta);
		sw.setOn();
		t1.attach(ta);
		t2.attach(ta);
		t1.sendValue();
		t2.sendValue();
		assertEquals(2, ta.getTemps().size());
		assertEquals((ta.getTemps().get(t1.getName())+ta.getTemps().get(t2.getName()))/2,ta.avgTemp(),0.001);
	}
	
	
	
	@Test(expected = IllegalOperationException.class)
	public void failThermos() throws IllegalOperationException, IncompatibleProtocolException {
		Thermometer t1 = new Thermometer();
		TemperatureAggregator ta = new TemperatureAggregator();
		t1.attach(ta);
		t1.sendValue();
	}
	
	@Test(expected = IncompatibleProtocolException.class)
	public void failBooleanProtocol() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		Lamp l = new Lamp();
		l.setProtocol("Y");
		sw.attach(l);
	}
	
	@Test(expected = IncompatibleProtocolException.class)
	public void failDoubleProtocol() throws IncompatibleProtocolException, IllegalOperationException {
		Thermometer t = new Thermometer();
		t.setProtocol("X");
		TemperatureAggregator ta = new TemperatureAggregator();
		ta.setProtocol("Y");
		t.attach(ta);
	}
	
	@Test
	public void disattachTest() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		Lamp l1 = new Lamp();
		Lamp l2 = new Lamp();
		sw.attach(l1);
		Switch sw2 = new Switch();
		sw2.attach(l2);
		assertEquals(1, sw.getDevice().getListeners().size());
	}
	
	@Test
	public void sendersTest() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		Lamp l = new Lamp();
		sw.attach(l);
		assertEquals(1, l.getDevice().getBooleanSenders().size());
	}
	
	@Test
	public void lockTest() throws IllegalOperationException {
		DoorLock d = new DoorLock();
		assertFalse(d.isOn());
		d.setOn();
		assertTrue(d.isOn());
	}
	
	@Test
	public void testSenderDisattach() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		Thermometer t1 = new Thermometer();
		TemperatureAggregator ta = new TemperatureAggregator();
		sw.attach(t1);
		sw.attach(ta);
		t1.attach(ta);
		assertEquals(2, sw.getDevice().getListeners().size());
		assertEquals(1, t1.getDevice().getBooleanSenders().size());
		assertEquals(1, ta.getDevice().getBooleanSenders().size());
		assertEquals(1, ta.getDevice().getDoubleSenders().size());
		sw.disattach(t1);
		sw.disattach(ta);
		t1.disattach(ta);
		assertEquals(0, sw.getDevice().getListeners().size());
		assertEquals(0, t1.getDevice().getBooleanSenders().size());
		assertEquals(0, ta.getDevice().getBooleanSenders().size());
		assertEquals(0, ta.getDevice().getDoubleSenders().size());
	}
	
	@Test(expected = IllegalOperationException.class)
	public void failStringInterfaces() throws IllegalOperationException {
		LuminositySensor ls = new LuminositySensor();
		ls.sendMessage();
	}
	
	@Test
	public void testStringInterfaces() throws IllegalOperationException, IncompatibleProtocolException {
		Switch sw = new Switch();
		LuminositySensor ls = new LuminositySensor();
		LuminosityHistory h = new LuminosityHistory();
		sw.attach(ls);
		sw.attach(h);
		sw.setOn();
		ls.attach(h);
		ls.sendMessage();
		ls.sendMessage();
		ls.sendMessage();
		ls.sendMessage();
		assertEquals(4, h.getHistory().size());
		h.printAll();
	}

}