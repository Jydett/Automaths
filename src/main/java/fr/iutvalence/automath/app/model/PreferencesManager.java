package fr.iutvalence.automath.app.model;

import java.util.prefs.Preferences;

public class PreferencesManager {
	private Preferences preferences;

	public PreferencesManager() {
		preferences = Preferences.userNodeForPackage(this.getClass());
	}

	public void update() {

	}
}
