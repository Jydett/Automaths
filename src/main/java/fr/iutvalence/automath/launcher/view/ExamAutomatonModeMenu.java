package fr.iutvalence.automath.launcher.view;

import fr.iutvalence.automath.app.model.Header;
import fr.iutvalence.automath.app.model.Header.UserProfile;
import fr.iutvalence.automath.app.view.mode.exam.ExamMenuBar;
import fr.iutvalence.automath.app.view.mode.exam.ExamRecognitionGUIPanel;
import fr.iutvalence.automath.app.view.mode.exam.ExamTranslationGUIPanel;
import fr.iutvalence.automath.app.view.panel.GUIPanel;

public class ExamAutomatonModeMenu extends AutomatonModeMenu {

	private final Menu menu;

	public ExamAutomatonModeMenu(Menu menu) {
		super(menu);
		this.menu = menu;
	}

	@Override
	public void onLeftButtonClicked() {
		openGui(new ExamRecognitionGUIPanel());
	}

	@Override
	public void onRightButtonClicked() {
		openGui(new ExamTranslationGUIPanel());
	}

	private void openGui(GUIPanel guiPanel) {
		menu.dispose();
		Header.getInstanceOfHeader(UserProfile.EXAM);
		guiPanel.createFrame(new ExamMenuBar(guiPanel)).setVisible(true);
	}
}
