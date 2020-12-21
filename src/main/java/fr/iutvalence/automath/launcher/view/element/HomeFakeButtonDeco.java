package fr.iutvalence.automath.launcher.view.element;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
/**
 * this class causes to create a image with a button type to permit the click on it
 */
public class HomeFakeButtonDeco extends JButton {

	private static final long serialVersionUID = -2852772489885507092L;

	/**
	 * Build the fake button with its properties.
	 * @param iconfilePath
	 */
	public HomeFakeButtonDeco(String iconfilePath) {
		setIcon(new ImageIcon(iconfilePath));
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		setVisible(true);
	}
}