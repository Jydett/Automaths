package fr.iutvalence.automath.launcher.view;

import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.launcher.view.IMenuListener;
import fr.iutvalence.automath.launcher.view.Menu;

public abstract class AutomatonModeMenu implements IMenuListener {

	protected Menu menu;

	protected AutomatonModeMenu(Menu menu) {
		this.menu = menu;
		menu.removeLabels();
		menu.addLabel(mxResources.get("AutomatonModeMenuLabel"), 230, 10, 465, 20, 20.0f);
		menu.setLeftButtonText(mxResources.get("TranslationMode"));
		menu.setRightButtonText(mxResources.get("RecognitionMode"));
		menu.setMenuListener(this);
	}
}
