package tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import interfaces.Programmable;
import devices.*;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;

public class CopyTests {

	@Test
	public void cloning() throws IncompatibleProtocolException, IllegalOperationException {
		List<Programmable> l = new ArrayList<>();
		l.add(new DoorLock());
		l.add(new Lamp());
		l.add(new LuminosityHistory());
		l.add(new LuminositySensor());
		l.add(new Switch());
		l.add(new TemperatureAggregator());
		l.add(new Thermometer());
		BooleanGroup a1 = new BooleanGroup();
		a1.add(new Lamp());
		DoubleGroup a2 = new DoubleGroup();
		a2.add(new Thermometer());
		StringGroup a3 = new StringGroup();
		a3.add(new LuminositySensor());
		l.add(new BooleanAdapter(new Lamp()));
		l.add(new DoubleAdapter(new Thermometer()));
		l.add(new StringAdapter(new LuminositySensor()));
		l.add(a1);
		l.add(a2);
		l.add(a3);
		Programmable c1 = UtilityFactory.createCustomDevice("a", new boolean[] {true,true,true,true,true,true});
		Programmable c2 = UtilityFactory.createCustomDevice("a", new boolean[] {true,true,false,true,true,false});
		Programmable c3 = UtilityFactory.createCustomDevice("a", new boolean[] {false,false,false,false,false,false});
		l.add(c1);
		l.add(c2);
		l.add(c3);
		for (Programmable p: l) {
			assertNotNull(p.copy());
		}
	}
}
