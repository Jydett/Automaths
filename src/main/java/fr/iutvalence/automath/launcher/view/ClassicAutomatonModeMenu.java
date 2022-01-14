package fr.iutvalence.automath.launcher.view;

import fr.iutvalence.automath.app.view.mode.classic.ClassicTranslationGUIPanel;
import fr.iutvalence.automath.launcher.view.AutomatonModeMenu;
import fr.iutvalence.automath.app.view.menu.ClassicMenuBar;
import fr.iutvalence.automath.app.view.mode.exam.ExamRecognitionGUIPanel;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.launcher.view.Menu;

public class ClassicAutomatonModeMenu extends AutomatonModeMenu {

	public ClassicAutomatonModeMenu(Menu menu) {
		super(menu);
	}

	@Override
	public void onLeftButtonClicked() {
		openGui(new ExamRecognitionGUIPanel());
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
