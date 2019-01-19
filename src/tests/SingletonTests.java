package tests;

import devices.*;
import exceptions.*;
import hot.CommandCenter;
import interfaces.*;
import static org.junit.Assert.*;
import org.junit.*;


public class SingletonTests {
	
	CommandCenter c = CommandCenter.getInstance();
	
	@After
	public void tearDown() {
		c.clear();
	}

	@Test
	public void testInit() throws IncompatibleProtocolException, IllegalOperationException {
		assertEquals(0, c.getTotalUtilities());
		Lamp l1 = new Lamp();
		Switch sw = new Switch();
		Switch sw2 = new Switch();
		Switch sw3 = new Switch();
		c.add(l1);
		c.add(sw);
		c.add(sw2);
		c.add(sw3);
		sw.attach(l1);
		assertEquals(4, c.getTotalUtilities());
		CommandCenter c2 = CommandCenter.getInstance();
		assertEquals(4, c2.getTotalUtilities());
		c2.add(l1);
		assertEquals(4, c2.getTotalUtilities());
		Lamp l2 = new Lamp();
		c2.remove(l2);
		assertEquals(4, c2.getTotalUtilities());
		c2.remove(l1);
		assertEquals(3, c2.getTotalUtilities());
		assertEquals(3, c.getTotalUtilities());	
	}
	
	@Test
	public void testFactory() throws UnsupportedUtilityException {
		c.createUtility("Lamp", null);
		c.createUtilityWithProtocol("Switch", "X", null);
		assertEquals(2, c.getTotalUtilities());
	}
	
	@Test(expected = UnsupportedUtilityException.class)
	public void failFactory() throws UnsupportedUtilityException {
		c.createUtility("", null);
	}
	
	@Test
	public void iterTest() {
		int t = 0;
		Lamp l1 = new Lamp();
		Switch sw = new Switch();
		Switch sw2 = new Switch();
		Switch sw3 = new Switch();
		c.add(l1);
		c.add(sw);
		c.add(sw2);
		c.add(sw3);
		for (Programmable p: c)
			t++;
		assertEquals(4,t);
		int x = 0;
		DoorLock dl = new DoorLock();
		c.add(dl);
		for (Programmable p: c)
			x++;
		assertEquals(5,x);
	}
	
	@Test
	public void mementoTest() throws IllegalOperationException, IncompatibleProtocolException, NoSuchElementException {
		Lamp l1 = new Lamp();
		Lamp l2 = new Lamp();
		Switch sw = new Switch();
		BooleanGroup g1 = new BooleanGroup();
		BooleanGroup g2 = new BooleanGroup();
		c.add(l1);
		c.add(sw);
		c.addGroup(g1);
		c.save("");
		c.add(l2);
		sw.attach(l1);
		c.addGroup(g2);
		assertEquals(3,c.getTotalUtilities());
		assertEquals(2,c.getGroups().size());
		assertEquals(1,c.getDeviceByName(sw.getName()).getListeners().size());
		c.load(0);
		assertEquals(2,c.getTotalUtilities());
		assertEquals(1,c.getGroups().size());
		assertEquals(0,c.getDeviceByName(sw.getName()).getListeners().size());
		c.save("");
		g1.add(l1);
		c.load(0);
		assertEquals(0,c.getGroupByName(g1.getName()).getCount());
		assertEquals(0,c.getGroupByName(g1.getName()).getCount());
	}

}
