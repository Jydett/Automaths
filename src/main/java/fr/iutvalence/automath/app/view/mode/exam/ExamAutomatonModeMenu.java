package fr.iutvalence.automath.app.view.mode.exam;

import fr.iutvalence.automath.app.view.menu.AutomatonModeMenu;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.model.Header;
import fr.iutvalence.automath.app.model.Header.UserProfile;

public class ExamAutomatonModeMenu extends AutomatonModeMenu {

	public ExamAutomatonModeMenu() {
		super();
	}
	
	protected void actionButton1() {
		Header.getInstanceOfHeader(UserProfile.EXAMEN);
		GUIPanel editorPanel = new ExamRecognitionGUIPanel();
		editorPanel.createFrame(new ExamMenuBar(editorPanel)).setVisible(true);
	}
	
	protected void actionButton2() {
		Header.getInstanceOfHeader(UserProfile.EXAMEN);
		GUIPanel editorPanel = new ExamTranslationGUIPanel();
		editorPanel.createFrame(new ExamMenuBar(editorPanel)).setVisible(true);
	}

}
