package fr.iutvalence.automath.launcher.view;

import fr.iutvalence.automath.app.model.Header;
import fr.iutvalence.automath.app.model.Header.UserProfile;
import fr.iutvalence.automath.app.view.mode.exam.ExamMenuBar;
import fr.iutvalence.automath.app.view.mode.exam.ExamRecognitionGUIPanel;
import fr.iutvalence.automath.app.view.mode.exam.ExamTranslationGUIPanel;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamAutomatonModeMenu extends AutomatonModeMenu {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExamAutomatonModeMenu.class);

	private final Menu menu;

	public ExamAutomatonModeMenu(Menu menu) {
		super(menu);
		this.menu = menu;
	}

	@Override
	public void onLeftButtonClicked() {
		LOGGER.info("Recognition mode selected");
		openGui(new ExamRecognitionGUIPanel());
	}

	@Override
	public void onRightButtonClicked() {
		LOGGER.info("Translation mode selected");
		openGui(new ExamTranslationGUIPanel());
	}

	private void openGui(GUIPanel guiPanel) {
		menu.dispose();
		Header.getInstanceOfHeader(UserProfile.EXAM);
		guiPanel.createFrame(new ExamMenuBar(guiPanel)).setVisible(true);
	}
}
