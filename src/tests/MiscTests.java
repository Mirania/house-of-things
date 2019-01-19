package tests;

import devices.*;
import exceptions.*;
import interfaces.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;

public class MiscTests {

	@Test
	public void disattachTest() throws IllegalOperationException, IncompatibleProtocolException {
		Switch sw = new Switch();
		Lamp l = new Lamp();
		sw.attach(l);
		assertEquals(1,l.getDevice().getSenders().size());
		assertEquals(1,sw.getDevice().getListeners().size());
		sw.disattach(l);
		assertEquals(0,l.getDevice().getSenders().size());
		assertEquals(0,sw.getDevice().getListeners().size());
		TemperatureAggregator ta = new TemperatureAggregator();
		Thermometer t = new Thermometer();
		t.attach(ta);
		assertEquals(1,ta.getDevice().getSenders().size());
		assertEquals(1,t.getDevice().getListeners().size());
		t.disattach(ta);
		assertEquals(0,ta.getDevice().getSenders().size());
		assertEquals(0,t.getDevice().getListeners().size());
	}
	
	public void adapterDisattachTest() throws IllegalOperationException, IncompatibleProtocolException {
		Switch sw2 = new Switch();
		sw2.setProtocol("X");
		Lamp l2 = new Lamp();
		l2.setProtocol("Y");
		BooleanAdapter bp = new BooleanAdapter(l2);
		sw2.attach(bp);
		assertEquals(1,bp.getDevice().getSenders().size());
		assertEquals(1,sw2.getDevice().getListeners().size());
		sw2.disattach(bp);
		assertEquals(0,bp.getDevice().getSenders().size());
		assertEquals(0,sw2.getDevice().getListeners().size());
	}
	
	public void groupDisattachTest() throws IllegalOperationException, IncompatibleProtocolException {
		Switch sw = new Switch();
		Lamp l = new Lamp();
		Lamp l2 = new Lamp();
		BooleanGroup g = new BooleanGroup();
		g.add(l);
		g.add(l2);
		sw.attach(g);
		assertEquals(1,l.getDevice().getSenders().size());
		assertEquals(1,l2.getDevice().getSenders().size());
		assertEquals(2,sw.getDevice().getListeners().size());
		sw.disattach(l);
		assertEquals(0,l.getDevice().getSenders().size());
		assertEquals(1,l2.getDevice().getSenders().size());
		assertEquals(1,sw.getDevice().getListeners().size());
	}
	
	
}
