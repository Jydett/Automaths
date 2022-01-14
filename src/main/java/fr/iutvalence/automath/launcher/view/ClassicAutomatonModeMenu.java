package fr.iutvalence.automath.launcher.view;

import fr.iutvalence.automath.app.view.menu.ClassicMenuBar;
import fr.iutvalence.automath.app.view.mode.classic.ClassicRecognitionGUIPanel;
import fr.iutvalence.automath.app.view.mode.classic.ClassicTranslationGUIPanel;
import fr.iutvalence.automath.app.view.panel.GUIPanel;

public class ClassicAutomatonModeMenu extends AutomatonModeMenu {

	public ClassicAutomatonModeMenu(Menu menu) {
		super(menu);
	}

	@Override
	public void onLeftButtonClicked() {
		openGui(new ClassicRecognitionGUIPanel());
	}

	@Override
	public void onRightButtonClicked() {
		openGui(new ClassicTranslationGUIPanel());
	}

	private void openGui(GUIPanel guiPanel) {
		menu.dispose();
		guiPanel.createFrame(new ClassicMenuBar(guiPanel)).setVisible(true);
	}
}
