package tests;

import devices.*;
import exceptions.*;
import interfaces.*;
import static org.junit.Assert.*;
import org.junit.*;

public class GroupTests {

	@Test
	public void testSwitchGroup() throws IncompatibleProtocolException, IllegalOperationException {
		Lamp l1 = new Lamp();
		Lamp l2 = new Lamp();
		l2.setProtocol("X");
		Lamp l3 = new Lamp();
		Lamp l4 = new Lamp();
		l4.setProtocol("X");
		BooleanGroup g = new BooleanGroup();
		g.add(l1);
		g.add(l2);
		BooleanGroup g2 = new BooleanGroup();
		g2.add(l3);
		g2.add(l4);
		g.add(g2);
		Switch sw = new Switch();
		sw.attach(g);
		sw.setOn();
		assertTrue(l1.isOn());
		assertTrue(l2.isOn());
		assertTrue(l3.isOn());
		assertTrue(l4.isOn());
		assertEquals("X", g.getProtocol());
		assertEquals("X", g2.getProtocol());
	}
	
	@Test
	public void testGroupProtocol() throws IncompatibleProtocolException, IllegalOperationException {
		Lamp l1 = new Lamp();
		Lamp l2 = new Lamp();
		l2.setProtocol("X");
		BooleanGroup g = new BooleanGroup();
		assertEquals("Universal", g.getProtocol());
		g.add(l1);
		assertEquals("Universal", g.getProtocol());
		BooleanGroup g2 = new BooleanGroup();
		g2.add(l2);
		assertEquals("X", g2.getProtocol());
		g.add(g2);
		assertEquals("X", g.getProtocol());
	}
	
	@Test
	public void testGroupProtocol2() throws IncompatibleProtocolException, IllegalOperationException {
		Lamp l1 = new Lamp();
		Lamp l2 = new Lamp();
		l2.setProtocol("X");
		Lamp l3 = new Lamp();	
		BooleanGroup g = new BooleanGroup();
		assertEquals("Universal", g.getProtocol());
		g.add(l1);
		assertEquals("Universal", g.getProtocol());
		g.add(l2);
		assertEquals("X", g.getProtocol());
		g.add(l3);
		assertEquals("X", g.getProtocol());
	}
	
	@Test(expected = IncompatibleProtocolException.class)
	public void failGrouping() throws IncompatibleProtocolException, IllegalOperationException {
		Lamp l1 = new Lamp();
		l1.setProtocol("Y");
		Lamp l2 = new Lamp();
		l2.setProtocol("X");
		BooleanGroup g = new BooleanGroup();
		g.add(l1);
		g.add(l2);
	}
	
	@Test(expected = IncompatibleProtocolException.class)
	public void failGrouping2() throws IncompatibleProtocolException, IllegalOperationException {
		Thermometer t1 = new Thermometer();
		Thermometer t2 = new Thermometer();
		t2.setProtocol("X");
		Thermometer t3 = new Thermometer();
		Thermometer t4 = new Thermometer();
		TemperatureAggregator ta = new TemperatureAggregator();
		ta.setProtocol("Y");
		DoubleGroup g = new DoubleGroup();
		g.add(t1);
		g.add(t2);
		g.add(t3);
		g.add(t4);
		g.attach(ta);
	}
	
	@Test
	public void testDoubleGroup() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		Thermometer t1 = new Thermometer();
		Thermometer t2 = new Thermometer();
		t2.setProtocol("X");
		Thermometer t3 = new Thermometer();
		Thermometer t4 = new Thermometer();
		TemperatureAggregator ta = new TemperatureAggregator();
		DoubleGroup dg = new DoubleGroup();
		DoubleGroup dg2 = new DoubleGroup();
		dg.add(t1);
		dg.add(t2);
		dg2.add(t3);
		dg2.add(t4);
		dg.add(dg2);
		dg.attach(ta);
		BooleanGroup bg = new BooleanGroup();
		bg.add(t1);
		bg.add(t2);
		bg.add(t3);
		bg.add(t4);
		bg.add(ta);
		sw.attach(bg);
		
		sw.setOn();
		dg.sendValue();
		assertEquals(4,ta.getTemps().size());
	}
	
	@Test
	public void adapterInGroup() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		sw.setProtocol("X");
		Lamp l1 = new Lamp();
		l1.setProtocol("X");
		Lamp l2 = new Lamp();
		l2.setProtocol("Y");
		BooleanAdapter bp = new BooleanAdapter(l2);
		BooleanGroup g = new BooleanGroup();
		g.add(l1);
		g.add(bp);
		assertFalse(sw.isOn());
		assertFalse(l1.isOn());
		assertFalse(l2.isOn());
		sw.attach(g);
		sw.setOn();
		assertTrue(sw.isOn());
		assertTrue(l1.isOn());
		assertTrue(l2.isOn());
	}
	
	@Test
	public void attachLogic() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		Thermometer t1 = new Thermometer();
		Thermometer t2 = new Thermometer();
		Thermometer t3 = new Thermometer();
		Thermometer t4 = new Thermometer();
		TemperatureAggregator ta = new TemperatureAggregator();
		BooleanGroup bg = new BooleanGroup();
		bg.add(t1);
		bg.add(t2);
		bg.add(t3);
		bg.add(t4);
		bg.add(ta);
		sw.attach(bg);
		sw.setOn();
		DoubleGroup g = new DoubleGroup();
		g.add(t1);
		g.add(t2);
		g.add(t3);
		g.add(t4);
		g.attach(ta);
		assertEquals(ta.getName(), t2.getDevice().getListeners().get(0).getName());
		g.sendValue();
		assertEquals(4, ta.getTemps().size());
		g.disattach(ta);
	}
	
	@Test
	public void removeTest() throws IncompatibleProtocolException, IllegalOperationException {
		Lamp l = new Lamp();
		Lamp l2 = new Lamp();
		BooleanGroup g = new BooleanGroup();
		g.add(l, l2);
		assertEquals(2,g.getMembers().size());
		g.remove(l);
		assertEquals(1,g.getMembers().size());
	}
	
	@Test
	public void stringTest() throws IncompatibleProtocolException, IllegalOperationException {
		Switch sw = new Switch();
		LuminositySensor ls1 = new LuminositySensor();
		LuminositySensor ls2 = new LuminositySensor();
		LuminositySensor ls3 = new LuminositySensor();
		StringGroup g = new StringGroup();
		g.add(ls1, ls2, ls3);
		LuminosityHistory h = new LuminosityHistory();
		g.attach(h);
		BooleanGroup bg = new BooleanGroup();
		bg.add(ls1, ls2, ls3, h);
		sw.attach(bg);
		sw.setOn();
		g.sendMessage();
		assertEquals(3, h.getHistory().size());
	}

}
