package fr.iutvalence.automath.launcher.view.element;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.JButton;
import java.awt.event.*;

/**
 * Create round button for Home page 
 */
public class HomeButton extends JButton {
	private static final long serialVersionUID = 8660109775944301911L;
	/** To know if the mouse has been over, false default */
	private boolean mouseOver = false;
	/** To know if the mouse has been pressed, false default */
	private boolean mousePressed = false;
	/** The color of the button */
	private final Color hisColor;
	/**
	 * Button builder with 2 args to personalize button
	 * Set a font 
	 * @param text
	 * @param color
	 */
	public HomeButton(String text, Color color) {
		super(text); 
		this.hisColor = color;
		Font aFont = new Font("Arial",Font.BOLD,20);
		this.setFont(aFont);
		setOpaque(false);
		setFocusPainted(false);
		setBorderPainted(false);
		setBackground(color);
		//Create a mouse listener to know user's choices (clicks on buttons)
		MouseAdapter mouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me){
				if (contains(me.getX(), me.getY())) {
					mousePressed = true;
					repaint();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent me) {
				mousePressed = false;
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent me) {
				mouseOver = false;
				mousePressed = false;
				repaint();
			}
			
			@Override
			public void mouseMoved(MouseEvent me) {
				mouseOver = contains(me.getX(), me.getY());
				repaint();
			}	
		};
		
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);		
	}
	/**
	 * Get the diameter for the button
	 * @return diameter
	 */
	private int getDiameter(){
		return Math.min(getWidth(), getHeight());
	}
	/**
	 * Create a Dimension 
	 * @return new Dimension
	 */
	@Override
	public Dimension getPreferredSize(){
		FontMetrics metrics = getGraphics().getFontMetrics(getFont());
		int minDiameter = 10 + Math.max(metrics.stringWidth(getText()), metrics.getHeight());
		return new Dimension(minDiameter, minDiameter);
	}
	/**
	 * Create radius 
	 * @return Point2D.distance
	 */
	@Override
	public boolean contains(int x, int y) {
		int radius = getDiameter()/2;
		return Point2D.distance(x, y, getWidth()/2, getHeight()/2) < radius;
	}
	/**
	 * Customize the component (button)
	 * Use Graphics object
	 * Set color
	 * Check if mouse is over or have click on the button and make differents actions after
	 * @param g
	 */
	@Override
	public void paintComponent(Graphics g) {
		int diameter = getDiameter();
		int radius = diameter/2;
		g.setColor(hisColor);

		if (! mousePressed) {
			g.setColor(hisColor);
		}

		g.fillOval(getWidth()/2 - radius, getHeight()/2 - radius, diameter, diameter);
		
		if (mouseOver) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(hisColor);
		}
		//Draw the button
		g.drawOval(getWidth()/2 - radius, getHeight()/2 - radius, diameter, diameter);
		//Set font color
		g.setColor(Color.BLACK);
		FontMetrics metrics = g.getFontMetrics(getFont());
		int stringWidth = metrics.stringWidth(getText());
		int stringHeight = metrics.getHeight();
		g.drawString(getText(), getWidth()/2 - stringWidth/2, getHeight()/2 + stringHeight/4);
	}
	
}
