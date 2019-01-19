package hot;

import java.awt.EventQueue;

import javax.swing.JFrame;

import devices.*;
import exceptions.IllegalOperationException;
import exceptions.IncompatibleProtocolException;
import ui.UiFactory;

public class App {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws IllegalOperationException
	 * @throws IncompatibleProtocolException
	 */
	public App() throws IncompatibleProtocolException, IllegalOperationException {
		frame = UiFactory.createFrame("HoT");

		// =================================== //
		// this code is just to initialize with a few objects
		Lamp l1 = new Lamp();
		Lamp l2 = new Lamp();
		Lamp l3 = new Lamp();
		Lamp l4 = new Lamp();
		Lamp l5 = new Lamp();
		Lamp l6 = new Lamp();
		l6.setName("U-type lamp");
		l6.setProtocol("U");
		TemperatureAggregator ta = new TemperatureAggregator();
		Thermometer t = new Thermometer();
		LuminosityHistory lh = new LuminosityHistory();
		LuminositySensor ls = new LuminositySensor();
		Switch sw = new Switch();

		CommandCenter c = CommandCenter.getInstance();
		c.add(l1);
		c.add(l2);
		c.add(l3);
		c.add(l4);
		c.add(l5);
		c.add(l6);
		c.add(ta);
		c.add(t);
		c.add(lh);
		c.add(ls);
		c.add(sw);

		BooleanGroup g1 = new BooleanGroup();
		g1.add(l1);
		g1.add(l2);

		BooleanGroup g2 = new BooleanGroup();
		g2.add(l3);
		g2.add(l4);
		g2.add(l5);
		g2.add(g1);

		DoorLock d = new DoorLock();
		c.add(d);

		c.addGroup(g1);
		c.addGroup(g2);

		BooleanAdapter x = new BooleanAdapter(l6);
		x.setName("Adapter for U-lamp");
		BooleanAdapter x2 = new BooleanAdapter(sw);
		x2.attach(l5);
		c.add(x);
		c.add(x2);
		sw.attach(x);
		sw.attach(ta);

		DoubleGroup d1 = new DoubleGroup();
		d1.setName("Numbers");
		c.addGroup(d1);

		c.save("base");

		// =================================== //

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IllegalOperationException
	 * @throws IncompatibleProtocolException
	 */
	private void initialize() throws IncompatibleProtocolException, IllegalOperationException {
		UiFactory.nav(this.frame, new MainPage());
	}
}
