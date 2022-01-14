package fr.iutvalence.automath.launcher.view;

import com.mxgraph.util.mxResources;

public class UserModeMenu implements IMenuListener {

	private final Menu menu;

	public UserModeMenu() {
		menu = new Menu();
		menu.setLeftButtonText(mxResources.get("ClassicMode"));
		menu.setRightButtonText(mxResources.get("ExamMode"));
		menu.setMenuListener(this);
		menu.addLabel(mxResources.get("UserModeMenuLabel"), 230, 10, 465, 20, 20.0f);
		menu.addLabel(mxResources.get("WarningLauncher"), 349, 441, 465, 20);
	}

	@Override
	public void onLeftButtonClicked() {
		new ExamAutomatonModeMenu(menu);
	}

	@Override
	public void onRightButtonClicked() {
		new ClassicAutomatonModeMenu(menu);
	}
}
