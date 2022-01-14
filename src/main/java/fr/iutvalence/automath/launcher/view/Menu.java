package fr.iutvalence.automath.launcher.view;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.BorderFactory;

import com.mxgraph.util.mxResources;

import fr.iutvalence.automath.launcher.view.element.HomeFakeButtonDeco;
import fr.iutvalence.automath.launcher.view.element.HomeButton;
import fr.iutvalence.automath.app.editor.EditorActions.OpenHelpAction;

public class Menu {

	protected final JFrame frame;

	protected final JLayeredPane layeredPane;
    private JButton rightButton;
    private JButton leftButton;

    protected Menu() {
		frame = new JFrame(mxResources.get("LauncherTitle"));
		layeredPane = new JLayeredPane();
		init();
		frame.setVisible(true);
	}

	private void init() {
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(new ImageIcon(Menu.class.getResource("/img/icon/logo.png")).getImage());
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		Box horizontalBox = Box.createHorizontalBox();
		panel.add(horizontalBox, BorderLayout.CENTER);

		Box verticalBox = Box.createVerticalBox();
		horizontalBox.add(verticalBox);
		verticalBox.add(layeredPane);

		rightButton = addButton("", Color.GRAY, null, 70, 148, 300, 300);
		leftButton = addButton("", Color.LIGHT_GRAY, null, 314, 137, 500, 200);

		JButton btnHelp = new JButton();
		btnHelp.setBorder(BorderFactory.createEmptyBorder());
		btnHelp.setContentAreaFilled(false);
		ImageIcon helpIcon = new ImageIcon(Menu.class.getResource("/img/icon/help.png"));
		btnHelp.setIcon(helpIcon);
		btnHelp.setRolloverIcon(helpIcon);
		btnHelp.addActionListener(e -> new OpenHelpAction().actionPerformed(e));
		btnHelp.setBounds(367, 60, 100, 100);
		layeredPane.add(btnHelp);

		JButton btnDeco = new HomeFakeButtonDeco("/img/icon/logo.png");
		btnDeco.setSelectedIcon(helpIcon);
		btnDeco.setIcon(new ImageIcon(Menu.class.getResource("/img/index.png")));
		btnDeco.setBounds(278, 10, 259,228);
		layeredPane.add(btnDeco);

		panel.add(Box.createGlue(), BorderLayout.SOUTH);
		panel.add(Box.createGlue(), BorderLayout.WEST);
		panel.add(Box.createGlue(), BorderLayout.EAST);
		panel.add(Box.createGlue(), BorderLayout.NORTH);
	}

	protected final JButton addButton(String text, Color color, ActionListener actionListener, int x, int y, int width, int height) {
		JButton button = new HomeButton(text, color);
		button.addActionListener(actionListener);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBounds(x, y, width, height);
		layeredPane.add(button);
		return button;
	}

	public final void addLabel(String text, int x, int y, int width, int height) {
		addLabel(text, x, y, width, height, UIManager.getDefaults().getFont("Label.font").getSize());
	}

	public final void addLabel(String text, int x, int y, int width, int height, float fontSize) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		label.setFont(label.getFont().deriveFont(fontSize));
		layeredPane.add(label);
	}

	public void removeLabels() {
		for (Component component : layeredPane.getComponents()) {
			if (component instanceof JLabel) {
				layeredPane.remove(component);
			}
		}
	}

	public void setLeftButtonText(String text) {
		leftButton.setText(text);
	}

	public void setRightButtonText(String text) {
		rightButton.setText(text);
	}

	public void setMenuListener(IMenuListener listener) {
		for (ActionListener actionListener : this.rightButton.getActionListeners()) {
			this.rightButton.removeActionListener(actionListener);
		}
		for (ActionListener actionListener : this.leftButton.getActionListeners()) {
			this.leftButton.removeActionListener(actionListener);
		}
		this.rightButton.addActionListener(e -> listener.onRightButtonClicked());
		this.leftButton.addActionListener(e -> listener.onLeftButtonClicked());
	}

	public void dispose() {
		frame.dispose();
	}
}
