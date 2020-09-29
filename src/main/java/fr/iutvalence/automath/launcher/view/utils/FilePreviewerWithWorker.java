package fr.iutvalence.automath.launcher.view.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import fr.iutvalence.automath.app.bridge.InterfaceAutomaton;
import fr.iutvalence.automath.app.view.handler.TaskWorkerHandler;
import fr.iutvalence.automath.app.io.out.ExportImageSinceXMLRunnable;

/**
 * FilePreviewerWithWorker is redefinition of JFileChooser with a preview of the automaton
 */
public class FilePreviewerWithWorker extends JComponent implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	/**
	 * The image of the automaton selected
	 */
	private volatile ImageIcon image = null;
	/**
	 * The file selected
	 */
	private File f = null;
	/**
	 * A task manager to request the dynamic creation of the image
	 */
	private TaskWorkerHandler taskThread;
	/**
	 * The JFileChooser with contains the redefinition
	 */
	private JFileChooser fc;
	/**
	 * An interface to manipulate the automaton
	 */
	private InterfaceAutomaton automaton;

	public FilePreviewerWithWorker(JFileChooser fc, InterfaceAutomaton automaton) {
		setPreferredSize(new Dimension(200,200));
		fc.addPropertyChangeListener(this);
		this.fc = fc;

		this.automaton = automaton;
		this.taskThread = new TaskWorkerHandler();
		this.taskThread.setDaemon(true);
        this.taskThread.start();
	}

	/**
	 * To stop the task manager
	 */
	public void stop(){
		taskThread.stopTaskThread();
	}

	/**
	 * To generate an image from a pnj or jpg format
	 */
	public synchronized void loadImage() {
		if(f == null) return;
		if (f.getName().endsWith(".png") || f.getName().endsWith(".jpg")) {
			ImageIcon tmpIcon = new ImageIcon(f.getPath());
			if (tmpIcon.getIconWidth() > (getWidth()-10))
				tmpIcon = new ImageIcon(tmpIcon.getImage().getScaledInstance((getWidth()-10), -1, Image.SCALE_SMOOTH));
			if (tmpIcon.getIconHeight() > (getHeight()-10))
				tmpIcon = new ImageIcon(tmpIcon.getImage().getScaledInstance(-1, (getHeight()-10), Image.SCALE_SMOOTH));
			image = tmpIcon;
		}
		this.repaint();
	}

	/**
	 * To regenerate the image
	 */
	public synchronized void refrechImage(){
		if(image == null) return;
		image = new ImageIcon(image.getImage().getScaledInstance((getWidth()-10), -1, Image.SCALE_SMOOTH));
		image = new ImageIcon(image.getImage().getScaledInstance(-1, (getHeight()-10), Image.SCALE_SMOOTH));
	}

	/**
	 * To generate an image with the specified path and BufferedImage in parameter
	 * @param img The BufferedImage to build ImageIcon
	 * @param path The path of the file
	 */
	public synchronized void loadImage(BufferedImage img, String path) {
		if (f.getAbsolutePath().equals(path)) {
			ImageIcon tmpIcon = new ImageIcon(img);
			if (tmpIcon.getIconWidth() > (getWidth()-10))
				tmpIcon = new ImageIcon(tmpIcon.getImage().getScaledInstance((getWidth()-10), -1, Image.SCALE_SMOOTH));
			if (tmpIcon.getIconHeight() > (getHeight()-10))
				tmpIcon = new ImageIcon(tmpIcon.getImage().getScaledInstance(-1, (getHeight()-10), Image.SCALE_SMOOTH));
			image = tmpIcon;
		}
		this.repaint();
	}

	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
		if (prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
			f = (File) e.getNewValue();
			if (f == null)
				return;
			if (isShowing()) {
				loadImage();
				removeAll();
				if (f == null)
					return;
				// fichiers *.xml
				else if (f.getName().endsWith(".xml")) {

					taskThread.run(new ExportImageSinceXMLRunnable(f.getAbsolutePath(),this,automaton));
				}
				else if (f.getName().endsWith(".png")) {
					loadImage();
				}
				else if (f.getName().endsWith(".jpg")) {
					loadImage();
				}
				else {
					image = null;
				}
			}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		sizePreview(fc.getWidth(),fc.getHeight());
		//refrechImage();
		if (image != null) {
			if (image.getIconWidth() != -1) {
				// les fichiers images
				int x = getWidth() / 2 - image.getIconWidth() / 2;
				int y = getHeight() / 2 - image.getIconHeight() / 2;
				if (y < 0)
					y = 0;
				if (x < 5)
					x = 5;
				image.paintIcon(this, g, x, y);
			}
		}
	}

	/**
	 * Resizing the image container
	 * @param x The abscissa
	 * @param y The ordinates
	 */
	public void sizePreview(int x, int y){
		setPreferredSize(new Dimension(new Integer(x/4), new Integer(y/4)));
	}
}