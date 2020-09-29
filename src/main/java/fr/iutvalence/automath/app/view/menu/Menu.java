package fr.iutvalence.automath.app.view.menu;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Color;
import java.awt.Cursor;
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

public abstract class Menu {

	protected final JFrame frame;

	protected final JLayeredPane layeredPane;
	
	protected Menu() {
		frame = new JFrame(mxResources.get("LauncherTitle"));
		layeredPane = new JLayeredPane();
		init();
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
		
		ActionListener al1 = e -> EventQueue.invokeLater(() -> {
			actionButton1();
			frame.dispose();
		});
		addButton(getButton1Name(), Color.GRAY, al1, 70, 148, 300, 300);
		
		ActionListener al2 = e -> EventQueue.invokeLater(() -> {
			actionButton2();
			frame.dispose();
		});
		addButton(getButton2Name(), Color.LIGHT_GRAY, al2, 314, 137, 500, 200);

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
	
	protected final void addButton(String text, Color color, ActionListener actionListener, int x, int y, int width, int height) {
		JButton button = new HomeButton(text, color);
		button.addActionListener(actionListener);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBounds(x, y, width, height);
		layeredPane.add(button);
	}
	
	protected final void addLabel(String text, int x, int y, int width, int height) {
		addLabel(text, x, y, width, height, UIManager.getDefaults().getFont("Label.font").getSize());
	}
	
	protected final void addLabel(String text, int x, int y, int width, int height, float fontSize) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		label.setFont(label.getFont().deriveFont(fontSize));
		layeredPane.add(label);
	}
	
	protected abstract void actionButton1();
	
	protected abstract void actionButton2();
	
	protected abstract String getButton1Name();
	
	protected abstract String getButton2Name();
	
}
