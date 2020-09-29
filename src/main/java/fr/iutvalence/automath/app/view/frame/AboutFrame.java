package fr.iutvalence.automath.app.view.frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mxgraph.util.mxResources;
import javax.swing.JTextPane;
import java.awt.SystemColor;

public class AboutFrame extends JDialog {

	private static final long serialVersionUID = -9175463680426451244L;

	public AboutFrame(Frame parent){
		super(parent);

		this.setTitle(mxResources.get("OpenAboutFrame"));
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		JTextPane creditText = new JTextPane();
		creditText.setBackground(SystemColor.menu);
		creditText.setEditable(false);
		creditText.setText("Application r\u00E9alis\u00E9e par :\r\n\tAlexis TAREL\r\n\tDorian CASAGRANDE\r\n\tLouis GOURINCHAS\r\n\r\nDans le cadre du projet de 2\u00E8me ann\u00E9e de DUT informatique, propos\u00E9 par M. CHARENSOL.\r\n\r\nBas\u00E9 sur un projet ant\u00E9rieur r\u00E9alis\u00E9 par :\r\n\tArthur Pelloux-Prayer\r\n\tWilliam Nauroy\r\n\tRomain Bourdon\r\n\tCl\u00E9ment Coste\r\n\r\nLicences utilis\u00E9es mxPDF JGraphX dk.brics.automaton");
		creditText.setHighlighter(null);

		contentPanel.add(creditText);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton cancelButton = new JButton(mxResources.get("CloseDialog"));
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(e -> dispose());
	}
}
