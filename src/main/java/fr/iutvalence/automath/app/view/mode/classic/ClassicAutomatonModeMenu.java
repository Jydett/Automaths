package fr.iutvalence.automath.app.view.mode.classic;

import fr.iutvalence.automath.app.view.menu.AutomatonModeMenu;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.menu.ClassicMenuBar;

public class ClassicAutomatonModeMenu extends AutomatonModeMenu {

	public ClassicAutomatonModeMenu() {
		super();
	}
	
	protected void actionButton1() {
		GUIPanel editorPanel = new ClassicRecognitionGUIPanel();
		editorPanel.createFrame(new ClassicMenuBar(editorPanel)).setVisible(true);
	}
	
	protected void actionButton2() {
		GUIPanel editorPanel = new ClassicTranslationGUIPanel();
		editorPanel.createFrame(new ClassicMenuBar(editorPanel)).setVisible(true);
	}

}
