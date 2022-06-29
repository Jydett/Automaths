package fr.iutvalence.automath.launcher.view;

import fr.iutvalence.automath.app.view.menu.ClassicMenuBar;
import fr.iutvalence.automath.app.view.mode.classic.ClassicRecognitionGUIPanel;
import fr.iutvalence.automath.app.view.mode.classic.ClassicTranslationGUIPanel;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassicAutomatonModeMenu extends AutomatonModeMenu {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassicAutomatonModeMenu.class);

	public ClassicAutomatonModeMenu(Menu menu) {
		super(menu);
	}

	@Override
	public void onLeftButtonClicked() {
		LOGGER.info("Recognition mode selected");
		openGui(new ClassicRecognitionGUIPanel());
	}

	@Override
	public void onRightButtonClicked() {
		LOGGER.info("Translation mode selected");
		openGui(new ClassicTranslationGUIPanel());
	}

	private void openGui(GUIPanel guiPanel) {
		menu.dispose();
		guiPanel.createFrame(new ClassicMenuBar(guiPanel)).setVisible(true);
	}
}
