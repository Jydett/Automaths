package fr.iutvalence.automath.launcher;

import java.awt.EventQueue;

import javax.swing.UIManager;

import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.launcher.view.UserModeMenu;

public class ApplicationLauncher {

	static {
		mxResources.add("editor");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(UserModeMenu :: new);
	}

}
