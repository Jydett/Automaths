package fr.iutvalence.automath.app.view.menu;

import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.app.view.menu.Menu;

public abstract class AutomatonModeMenu extends Menu {

	protected AutomatonModeMenu() {
		super();
		addLabel(mxResources.get("AutomatonModeMenuLabel"), 230, 10, 465, 20, 20.0f);
		frame.setVisible(true);
	}
	
	protected final String getButton1Name() {
		return mxResources.get("RecognitionMode");
	}
	
	protected final String getButton2Name() {
		return mxResources.get("TranslationMode");
	}
	
}
