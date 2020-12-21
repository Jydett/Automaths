package fr.iutvalence.automath.app.view.utils;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class JMenuItemWithHints extends JMenuItem {

	private static final long serialVersionUID = 7363824302444226060L;
	
	   @Override 
	   public void setAccelerator(KeyStroke keyStroke) {
		   //do nothing
	   }
	   
	   public JMenuItemWithHints(Action a) {
		   super();
		   super.setAction(a);
	   }

	   
	   public JMenuItemWithHints setAcceleratorBuilder(KeyStroke k) {
		   super.setAccelerator(k);
		   return this;
	   }
}
