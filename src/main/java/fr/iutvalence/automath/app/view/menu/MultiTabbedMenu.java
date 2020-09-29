package fr.iutvalence.automath.app.view.menu;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mxgraph.util.mxResources;

import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.model.Header;

public abstract class MultiTabbedMenu extends JTabbedPane {

	private static final long serialVersionUID = 2169638062606931902L;
	
	protected MultiTabbedMenu(GUIPanel editor) {
		super();
		init(editor);
	}
	
	private void init(GUIPanel editor) {
		JPanel panel = new JPanel();
		
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		SpringLayout panelLayout = new SpringLayout();
		
		JLabel lastNameLabel = new JLabel(mxResources.get("LastName"));
		JTextField lastNameTextField = new JTextField();
		lastNameTextField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				Header.getInstanceOfHeader().setName(lastNameTextField.getText());
			}
		});
		
		JLabel firstNameLabel = new JLabel(mxResources.get("FirstName"));
		JTextField firstNameTextField = new JTextField();
		firstNameTextField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				Header.getInstanceOfHeader().setForename(firstNameTextField.getText());
			}
		});
		
		JLabel groupLabel = new JLabel(mxResources.get("TDNumber"));
		JTextField groupTextField = new JTextField();
		groupTextField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				Header.getInstanceOfHeader().setStudentClass(groupTextField.getText());
			}
		});
		
		JLabel studentLabel = new JLabel(mxResources.get("StudentNumber"));
		JTextField studentTextField = new JTextField();
		studentTextField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				 Header.getInstanceOfHeader().setStudentCode(studentTextField.getText());
			}
		});
		
		panelLayout.putConstraint(SpringLayout.WEST, lastNameLabel, 10, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, lastNameLabel, -5, SpringLayout.SOUTH, panel);
		panelLayout.putConstraint(SpringLayout.NORTH, lastNameLabel, 5, SpringLayout.NORTH, panel);
		panel.add(lastNameLabel);
		
		panelLayout.putConstraint(SpringLayout.WEST, lastNameTextField, 5, SpringLayout.EAST, lastNameLabel);
		panelLayout.putConstraint(SpringLayout.NORTH, lastNameTextField, 30, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, lastNameTextField, -30, SpringLayout.SOUTH, panel);
		panel.add(lastNameTextField);
		lastNameTextField.setColumns(10);
		
		panelLayout.putConstraint(SpringLayout.WEST, firstNameLabel, 15, SpringLayout.EAST, lastNameTextField);
		panelLayout.putConstraint(SpringLayout.SOUTH, firstNameLabel, -5, SpringLayout.SOUTH, panel);
		panelLayout.putConstraint(SpringLayout.NORTH, firstNameLabel, 5, SpringLayout.NORTH, panel);
		panel.add(firstNameLabel);
		
		panelLayout.putConstraint(SpringLayout.WEST, firstNameTextField, 5, SpringLayout.EAST, firstNameLabel);
		panelLayout.putConstraint(SpringLayout.NORTH, firstNameTextField, 30, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, firstNameTextField, -30, SpringLayout.SOUTH, panel);
		panel.add(firstNameTextField);
		firstNameTextField.setColumns(10);
		
		panelLayout.putConstraint(SpringLayout.WEST, groupLabel, 15, SpringLayout.EAST, firstNameTextField);
		panelLayout.putConstraint(SpringLayout.SOUTH, groupLabel, -5, SpringLayout.SOUTH, panel);
		panelLayout.putConstraint(SpringLayout.NORTH, groupLabel, 5, SpringLayout.NORTH, panel);
		panel.add(groupLabel);
		
		panelLayout.putConstraint(SpringLayout.WEST, groupTextField, 5, SpringLayout.EAST, groupLabel);
		panelLayout.putConstraint(SpringLayout.NORTH, groupTextField, 30, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, groupTextField, -30, SpringLayout.SOUTH, panel);
		panel.add(groupTextField);
		groupTextField.setColumns(10);
		
		panelLayout.putConstraint(SpringLayout.WEST, studentLabel, 15, SpringLayout.EAST, groupTextField);
		panelLayout.putConstraint(SpringLayout.SOUTH, studentLabel, -5, SpringLayout.SOUTH, panel);
		panelLayout.putConstraint(SpringLayout.NORTH, studentLabel, 5, SpringLayout.NORTH, panel);
		panel.add(studentLabel);
		
		panelLayout.putConstraint(SpringLayout.WEST, studentTextField, 5, SpringLayout.EAST, studentLabel);
		panelLayout.putConstraint(SpringLayout.NORTH, studentTextField, 30, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, studentTextField, -30, SpringLayout.SOUTH, panel);
		panel.add(studentTextField);
		studentTextField.setColumns(10);
		
		addTab(mxResources.get("Identification"), null, panel, mxResources.get("IdentificationTip"));
	}
	
}
