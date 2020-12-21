package fr.iutvalence.automath.app.view.menu;

import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.app.view.mode.classic.ClassicAutomatonModeMenu;
import fr.iutvalence.automath.app.view.mode.exam.ExamAutomatonModeMenu;

public class UserModeMenu extends Menu {

	public UserModeMenu() {
		super();
		addLabel(mxResources.get("UserModeMenuLabel"), 230, 10, 465, 20, 20.0f);
		addLabel(mxResources.get("WarningLauncher"), 349, 441, 465, 20);
		frame.setVisible(true);
	}
	
	protected String getButton1Name() {
		return mxResources.get("ClassicMode");
	}
	
	protected String getButton2Name() {
		return mxResources.get("ExamMode");
	}
	
	protected void actionButton1() {
		new ClassicAutomatonModeMenu();
	}
	
	protected void actionButton2() {
		new ExamAutomatonModeMenu();
	}
	
}
