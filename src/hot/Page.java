package hot;

import javax.swing.JFrame;

//template algorithm pattern
public abstract class Page {
	
	public final void build(JFrame frame) {
		setTarget(frame);
		collectInformation();
		populateWindow();
		validatePage();
	}
	
	public abstract void setTarget(JFrame frame);
	public abstract void collectInformation();
	public abstract void populateWindow();
	public abstract void validatePage();
}
