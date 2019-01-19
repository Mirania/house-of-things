package tests;

import hot.*;
import org.junit.Test;

import devices.Lamp;
import devices.SlackState;
import devices.TelegramState;

import org.junit.Before;
import static org.junit.Assert.*;

import org.junit.After;

public class LoggerTest {
	
	Logger l = Logger.getInstance();
	CommandCenter c = CommandCenter.getInstance();
	
	@After
	public void tearDown() {
		l.clear();
		c.clear();
	}
	
	@Test
	public void testNotify() {
    l.setState(new SlackState("a"));
    assertEquals(1,l.getLogs().size());
    c.add(new Lamp());
    assertEquals(2,l.getLogs().size());
  }

}