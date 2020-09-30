package fr.iutvalence.automath.app.editor;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.io.in.ImporterXML;
import fr.iutvalence.automath.app.io.out.ExportPDF;
import fr.iutvalence.automath.app.io.out.ExportPython;
import fr.iutvalence.automath.app.io.out.ExportXML;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.view.menu.PopUpMenu;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.utils.DefaultFileFilter;
import fr.iutvalence.automath.launcher.view.utils.FilePreviewerWithWorker;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
/**
 * 
 *
 */
public class EditorActions {
	
	/**
	 * To retrieve the graphic panel
	 * @param e An action
	 * @return
	 */
	public static GUIPanel getEditor(ActionEvent e)
	{
		if (e.getSource() instanceof Component)
		{
			Component component = (Component) e.getSource();

			while (component != null
					&& !(component instanceof GUIPanel))
			{
				if(component instanceof PopUpMenu) return ((PopUpMenu)component).getEditor();
				component = component.getParent();
				
			}

			return (GUIPanel) component;
		}

		return null;
	}
	
	@SuppressWarnings("serial")
	/**
	 * Modify a state in initial state
	 */
	public static class SetInitialAction extends JCheckBoxMenuItem
	{
		public SetInitialAction(String name) {
			super(name);
			
			addActionListener(new ActionListener(){
		
				public void actionPerformed(ActionEvent e){
					GUIPanel editor = getEditor(e);
					if(e.getSource() instanceof Component) {
						@SuppressWarnings("unused")
						Component comp = ((Component)e.getSource());
					}
					FiniteStateAutomatonGraph graph =  (FiniteStateAutomatonGraph) editor.getGraphComponent().getGraph();
					Object state = graph.getSelectionCell();
					if (state != null) {
						if (((mxCell)state).isVertex()) {
							StateInfo stInfo = ((StateInfo)((mxCell)state).getValue());
							mxIGraphModel model = graph.getModel();

							model.beginUpdate();
							stInfo.setStarting(! stInfo.isStarting());
							model.setValue(state, stInfo);
							stInfo.refresh((mxCell) state);
							model.endUpdate();
							editor.getCellDescriptorPanel().refresh();
						} else {
							//System.err.println("InitialAction called on non vertex cell !");
						}
					}
				}
			});
		}
	}
	
	@SuppressWarnings("serial")
	/**
	 * Modify a state in final state
	 */
	public static class SetFinalAction extends JCheckBoxMenuItem
	{
		public SetFinalAction(String name) {
			super(name);
			addActionListener(new ActionListener(){
		
				public void actionPerformed(ActionEvent e){
					GUIPanel editor = getEditor(e);
					FiniteStateAutomatonGraph graph =  (FiniteStateAutomatonGraph) editor.getGraphComponent().getGraph();
					Object state = graph.getSelectionCell();
					if(state != null) {
						if(((mxCell)state).isVertex()) {
							StateInfo stInfo = ((StateInfo)((mxCell)state).getValue());
							mxIGraphModel model = graph.getModel();

							model.beginUpdate();
							stInfo.setAccepting(! stInfo.isAccepting());
							model.setValue(state, stInfo);
							stInfo.refresh((mxCell) state);
							model.endUpdate();
							editor.getCellDescriptorPanel().refresh();
						}else {
							//System.err.println("InitialAction called on non vertex cell !");
						}
					}
				}
			});
		}
	}

	public static class ReorderAction extends AbstractAction {
		private final boolean isBack;

		public ReorderAction(String name, boolean isBack) {
			super(name);
			this.isBack = isBack;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mxGraph graph = getEditor(e).getGraphComponent().getGraph();
			graph.getModel().beginUpdate();
			graph.orderCells(isBack, graph.getSelectionCells());
			graph.getModel().endUpdate();
		}
	}

	public static class ReverseAction extends AbstractAction {

		public ReverseAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mxGraph graph = getEditor(e).getGraphComponent().getGraph();
			mxIGraphModel model = graph.getModel();
			model.beginUpdate();
			Arrays.stream(graph.getSelectionCells())
				.map(c -> (mxCell) c)
				.filter(mxCell::isEdge)
				.forEach(transition -> {
					mxICell oldSource = transition.getSource();
					model.setTerminal(transition, transition.getTarget(), true);
					model.setTerminal(transition, oldSource, false);
				});
			model.endUpdate();
		}
	}
	
	@SuppressWarnings("serial")
	/**
	 * To quit the application
	 */
	public static class ExitAction extends AbstractAction
	{

		public void actionPerformed(ActionEvent e)
		{
			GUIPanel editor = getEditor(e);

			if (editor != null)
			{
				editor.exit();
			}
		}
	}

	@SuppressWarnings("serial")
	public static class DeleteAction extends AbstractAction {
		private final Action action;

		public DeleteAction() {
			action = mxGraphActions.getDeleteAction();
		}

		public void actionPerformed(ActionEvent e) {
			action.actionPerformed(e);
			getEditor(e).getCellDescriptorPanel().clear();
		}
	}
	
	@SuppressWarnings("serial")
	/**
	 * the history of actions that are stored to restore them
	 */
	public static class HistoryAction extends AbstractAction
	{

		protected final boolean undo;


		public HistoryAction(boolean undo) {
			this.undo = undo;
		}


		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);
			if (editor != null) {
				if (undo) {
					editor.getUndoManager().undo();
				} else {
					editor.getUndoManager().redo();
				}
			}
		}
	}

	@SuppressWarnings("serial")
	/**
	 * The action to minimize the current automaton
	 */
	public static class MinimizeAction extends AbstractAction{

	    public MinimizeAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);
			if(!editor.getAllowMinimisation()) return;
			long t0 = System.currentTimeMillis();
			mxGraph graph = editor.getGraphComponent().getGraph();
			graph.getModel().beginUpdate();
			try {
				((FiniteStateAutomatonGraph) graph).determinize();
				new EditorActions.OrganicAction().actionPerformed(e);
			} finally {
				graph.getModel().endUpdate();
			}
			editor.setAppStatusText(mxResources.get("Minimization")+" : "+(System.currentTimeMillis()-t0)+"ms");
		}
		
	}
	
	@SuppressWarnings("serial")
	/**
	 * The action to determinize the current automaton
	 */
	public static class DeterminizeAction extends AbstractAction{

	    public DeterminizeAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);
			if(!editor.getAllowDeterminisation()) return;
			long t0 = System.currentTimeMillis();
			mxGraph graph = editor.getGraphComponent().getGraph();
			graph.getModel().beginUpdate();
			try {
				((FiniteStateAutomatonGraph) editor.getGraphComponent().getGraph()).determinize();
				new EditorActions.OrganicAction().actionPerformed(e);
			} finally {
				graph.getModel().endUpdate();
			}
			editor.setAppStatusText(mxResources.get("Determinization")+" : "+(System.currentTimeMillis()-t0)+"ms");
		}
		
	}
	
	@SuppressWarnings("serial")
	/**
	 * The action open a template 
	 */
	public static class OpenTemplate extends AbstractAction
	{
		

		protected String lastDir;
		private ImporterXML importerXML;


		protected void resetEditor(GUIPanel editor)
		{
			editor.setModified(true);
			editor.getGraphComponent().zoomAndCenter();
		}

		/**
		 * Reads XML+PNG format.
		 */
		protected void openXmlPng(GUIPanel editor, File file)
				throws IOException
		{
			Map<String, String> text = mxPngTextDecoder
					.decodeCompressedText(new FileInputStream(file));

			if (text != null)
			{
				String value = text.get("mxGraphModel");

				if (value != null)
				{
					Document document = mxXmlUtils.parseXml(URLDecoder.decode(
							value, "UTF-8"));
					mxCodec codec = new mxCodec(document);
					codec.decode(document.getDocumentElement(), editor
							.getGraphComponent().getGraph().getModel());
					editor.setCurrentFile(file);
					resetEditor(editor);

					return;
				}
			}

			JOptionPane.showMessageDialog(editor,
					mxResources.get("imageContainsNoDiagramData"));
		}

		protected void openGD(GUIPanel editor, File file, String gdText) {
			mxGraph graph = editor.getGraphComponent().getGraph();
			
			if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
				try {
					importerXML.setFile(file.getAbsolutePath());
					importerXML.importAutomaton(false);
					editor.getGraphComponent().zoomAndCenter();
				} catch (Exception e) {
					editor.displayMessage(mxResources.get("XMLERROR"), mxResources.get("Error"), JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// Replaces file extension with .mxe
				String filename = file.getName();
				filename = filename.substring(0, filename.length() - 4) + ".mxe";	
				((mxGraphModel) graph.getModel()).clear();
				mxGdCodec.decode(gdText, graph);
				editor.getGraphComponent().zoomAndCenter();
				editor.setCurrentFile(new File(lastDir + "/" + filename));
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			GUIPanel editor = getEditor(e);	

			if (editor != null)
			{
				
				mxGraph graph = editor.getGraphComponent().getGraph();

				if (graph != null)
				{
					String wd = (lastDir != null) ? lastDir : System
							.getProperty("user.dir");

					JFileChooser fc = new JFileChooser(wd);
					FiniteStateAutomatonGraph graphCustom = (FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph());
					FilePreviewerWithWorker fileChooserPreviewer = new FilePreviewerWithWorker(fc, graphCustom.getAutomaton());
					fc.setAccessory(fileChooserPreviewer);
					try {
						this.importerXML = new ImporterXML(graphCustom);
					} catch (ParserConfigurationException e1) {
						e1.printStackTrace();
					}
					
					DefaultFileFilter defaultFilter = new DefaultFileFilter(".xml",
							"XML  " + mxResources.get("Defaultformat") + " (.xml)");
					
					fc.addChoosableFileFilter(defaultFilter);

					fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
							"mxGraph Editor " + mxResources.get("File")
									+ " (.mxe)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".png",
							"PNG+XML  " + mxResources.get("File")
									+ " (.png)"));

					// Adds file filter for VDX import
					fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
							"XML Drawing  " + mxResources.get("File")
									+ " (.vdx)"));

					// Adds file filter for GD import
					fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
							"Graph Drawing  " + mxResources.get("File")
									+ " (.txt)"));
					

					fc.setFileFilter(defaultFilter);

					int rc = fc.showDialog(editor,
							mxResources.get("OpenTemplate"));

					if (rc == JFileChooser.APPROVE_OPTION)
					{
						lastDir = fc.getSelectedFile().getParent();

						try
						{
							if (fc.getSelectedFile().getAbsolutePath()
									.toLowerCase().endsWith(".png"))
							{
								openXmlPng(editor, fc.getSelectedFile());
							}
							else if (fc.getSelectedFile().getAbsolutePath()
									.toLowerCase().endsWith(".txt"))
							{
								openGD(editor, fc.getSelectedFile(),
										mxUtils.readFile(fc
												.getSelectedFile()
												.getAbsolutePath()));
							}
							else if (fc.getSelectedFile().getAbsolutePath()
									.toLowerCase().endsWith(".xml"))
							{
								openGD(editor, fc.getSelectedFile(),
										mxUtils.readFile(fc
												.getSelectedFile()
												.getAbsolutePath()));
							}
							else
							{
								Document document = mxXmlUtils
										.parseXml(mxUtils.readFile(fc
												.getSelectedFile()
												.getAbsolutePath()));

								mxCodec codec = new mxCodec(document);
								codec.decode(
										document.getDocumentElement(),
										graph.getModel());
								editor.setCurrentFile(fc
										.getSelectedFile());

								resetEditor(editor);
							}
						}
						catch (IOException ex)
						{
							ex.printStackTrace();
							JOptionPane.showMessageDialog(
									editor.getGraphComponent(),
									ex.toString(),
									mxResources.get("Error"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
		
	}
	
	@SuppressWarnings("serial")
	/**
	 * The action to import files with a preview
	 */
	public static class OpenActionWithPreviewer extends AbstractAction {

		protected String lastDir;
		private ImporterXML importerXML;

		protected void resetEditor(GUIPanel editor) {
			editor.setModified(false);
			editor.getUndoManager().clear();
			editor.getGraphComponent().zoomAndCenter();
		}

		/**
		 * Reads XML+PNG format.
		 */
		protected void openXmlPng(GUIPanel editor, File file) throws IOException {
			try (final FileInputStream data = new FileInputStream(file)){
				Map<String, String> text = mxPngTextDecoder
					.decodeCompressedText(data);

				if (text != null) {
					String value = text.get("mxGraphModel");

					if (value != null) {
						Document document = mxXmlUtils.parseXml(URLDecoder.decode(value, "UTF-8"));
						mxCodec codec = new mxCodec(document);
						codec.decode(document.getDocumentElement(), editor
							.getGraphComponent().getGraph().getModel());
						editor.setCurrentFile(file);
						resetEditor(editor);

						return;
					}
				}
			}

			JOptionPane.showMessageDialog(editor, mxResources.get("imageContainsNoDiagramData"));
		}

		protected void openGD(GUIPanel editor, File file, String gdText) {
			mxGraph graph = editor.getGraphComponent().getGraph();
			
			if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
				try {
					String filename = file.getName();
					importerXML.setFile(file.getAbsolutePath());
					importerXML.importAutomaton(true);
					editor.getGraphComponent().zoomAndCenter();
					editor.setCurrentFile(new File(lastDir + "/" + filename));
				} catch (Exception e) {
					editor.displayMessage(mxResources.get("XMLERROR"), mxResources.get("Error"), JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// Replaces file extension with .mxe
				String filename = file.getName();
				filename = filename.substring(0, filename.length() - 4) + ".mxe";
	
				if (new File(filename).exists()
						&& JOptionPane.showConfirmDialog(editor,
								mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
					return;
				}
	
				((mxGraphModel) graph.getModel()).clear();
				mxGdCodec.decode(gdText, graph);
				editor.getGraphComponent().zoomAndCenter();
				editor.setCurrentFile(new File(lastDir + "/" + filename));
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			GUIPanel editor = getEditor(e);	

			if (editor != null) {
				
				if (editor.isUnmodified()
						|| JOptionPane.showConfirmDialog(editor,
								mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {
					mxGraph graph = editor.getGraphComponent().getGraph();

					if (graph != null) {
						String wd = (lastDir != null) ? lastDir : System
								.getProperty("user.dir");

						JFileChooser fc = new JFileChooser(wd);
						FiniteStateAutomatonGraph graphCustom = (FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph());
						FilePreviewerWithWorker fileChooserPreviewer = new FilePreviewerWithWorker(fc, graphCustom.getAutomaton());
						fc.setAccessory(fileChooserPreviewer);
						try {
							this.importerXML = new ImporterXML(graphCustom);
						} catch (ParserConfigurationException e1) {
							e1.printStackTrace();
						}
						
						DefaultFileFilter defaultFilter = new DefaultFileFilter(".xml",
								"XML  " + mxResources.get("Defaultformat") + " (.xml)");
						
						fc.addChoosableFileFilter(defaultFilter);

						fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
								"mxGraph Editor " + mxResources.get("File")
										+ " (.mxe)"));
						fc.addChoosableFileFilter(new DefaultFileFilter(".png",
								"PNG+XML  " + mxResources.get("File")
										+ " (.png)"));

						// Adds file filter for VDX import
						fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
								"XML Drawing  " + mxResources.get("File")
										+ " (.vdx)"));

						// Adds file filter for GD import
						fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
								"Graph Drawing  " + mxResources.get("File")
										+ " (.txt)"));
						

						fc.setFileFilter(defaultFilter);

						int rc = fc.showDialog(editor,
								mxResources.get("Open"));

						if (rc == JFileChooser.APPROVE_OPTION) {
							lastDir = fc.getSelectedFile().getParent();

							try {
								if (fc.getSelectedFile().getAbsolutePath()
										.toLowerCase().endsWith(".png")) {
									openXmlPng(editor, fc.getSelectedFile());
								}
								else if (fc.getSelectedFile().getAbsolutePath()
										.toLowerCase().endsWith(".txt")) {
									openGD(editor, fc.getSelectedFile(),
											mxUtils.readFile(fc
													.getSelectedFile()
													.getAbsolutePath()));
								} else if (fc.getSelectedFile().getAbsolutePath()
										.toLowerCase().endsWith(".xml")) {
									openGD(editor, fc.getSelectedFile(),
											mxUtils.readFile(fc
													.getSelectedFile()
													.getAbsolutePath()));
								} else {
									Document document = mxXmlUtils
											.parseXml(mxUtils.readFile(fc
													.getSelectedFile()
													.getAbsolutePath()));

									mxCodec codec = new mxCodec(document);
									codec.decode(
											document.getDocumentElement(),
											graph.getModel());
									editor.setCurrentFile(fc
											.getSelectedFile());

									resetEditor(editor);
								}
							} catch (IOException ex) {
								ex.printStackTrace();
								JOptionPane.showMessageDialog(
										editor.getGraphComponent(),
										ex.toString(),
										mxResources.get("Error"),
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	/**
	 * The action to save in the mode of examination which allows to record in pdf
	 */
	public static class SaveExamAction extends AbstractAction {
		

		protected String lastDir = null;
		

		protected boolean showDialog;
		
		
		public SaveExamAction(boolean showDialog)
		{
			this.showDialog = showDialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);

			if (editor != null) {
				mxGraphComponent graphComponent = editor.getGraphComponent();
				FileFilter selectedFilter;
				DefaultFileFilter xmlPngFilter = (new DefaultFileFilter(".pdf",
						"PDF " + mxResources.get("File") + " (.pdf)"));
				String filename;

				if (showDialog || editor.getCurrentFile() == null) {
					String wd;
					if (lastDir != null) {
						wd = lastDir;
					} else if (editor.getCurrentFile() != null) {
						wd = editor.getCurrentFile().getParent();
					} else {
						wd = System.getProperty("user.dir");
					}

					JFileChooser fc = new JFileChooser(wd);
					fc.setDialogTitle(mxResources.get("SaveAs"));
					// Adds the default file format
					fc.addChoosableFileFilter(xmlPngFilter);
					
					fc.addChoosableFileFilter(new DefaultFileFilter(".pdf",
							"PDF " + mxResources.get("File")
									+ " (.pdf)"));

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();

					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<>();

					for (Object format : imageFormats) {
						String ext = format.toString().toLowerCase();
						formats.add(ext);
					}

					imageFormats = formats.toArray();

					for (Object imageFormat : imageFormats) {
						String ext = imageFormat.toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("."
							+ ext, ext.toUpperCase() + " "
							+ mxResources.get("File") + " (." + ext + ")"));
					}

					// Adds filter that accepts all supported image formats
					fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
							mxResources.get("allImages")));
					fc.setFileFilter(xmlPngFilter);
					
					int rc = fc.showSaveDialog(editor);

					if (rc != JFileChooser.APPROVE_OPTION) {
						return;
					} else {
						lastDir = fc.getSelectedFile().getParent();
					}

					filename = fc.getSelectedFile().getAbsolutePath();
					selectedFilter = fc.getFileFilter();

					if (selectedFilter instanceof DefaultFileFilter) {
						String ext = ((DefaultFileFilter) selectedFilter).getExtension();
						if (!filename.toLowerCase().endsWith(ext)) {
							filename += ext;
						}
					}

					if (new File(filename).exists()
							&& JOptionPane.showConfirmDialog(graphComponent,
									mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
						return;
					}
				} else {
					filename = editor.getCurrentFile().getAbsolutePath();
				}

				try {
					String ext = filename.substring(filename.lastIndexOf('.') + 1);
					if (ext.equalsIgnoreCase("pdf")) {
						new ExportPDF((FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph())).exportAutomate(filename);
					}
					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
					editor.updateTitle();
				} catch (Throwable ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("Error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	/**
	 * The action to save in all available models
	 */
	public static class SaveAction extends AbstractAction {
		protected boolean showDialog;

		protected String lastDir = null;

		private ExportXML importerXML;

		public SaveAction(boolean showDialog) {
			this.showDialog = showDialog;
		}

		/**
		 * Saves XML+PNG format.
		 */
		protected void saveXmlPng(GUIPanel editor, String filename, Color bg) throws IOException {
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();

			// Creates the image for the PNG file
			BufferedImage image = mxCellRenderer.createBufferedImage(graph,
					null, 1, bg, graphComponent.isAntiAlias(), null,
					graphComponent.getCanvas());

			// Creates the URL-encoded XML data
			mxCodec codec = new mxCodec();
			String xml = URLEncoder.encode(
					mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
			mxPngEncodeParam param = mxPngEncodeParam
					.getDefaultEncodeParam(image);
			param.setCompressedText(new String[] { "mxGraphModel", xml });

			// Saves as a PNG file
			try (FileOutputStream outputStream = new FileOutputStream(new File(filename))) {
				mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream, param);

				if (image != null) {
					encoder.encode(image);

					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
				} else {
					JOptionPane.showMessageDialog(graphComponent,
						mxResources.get("noImageData"));
				}
			}
		}


		public void actionPerformed(ActionEvent e)
		{
			GUIPanel editor = getEditor(e);

			if (editor != null) {
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				FileFilter selectedFilter = null;
				DefaultFileFilter xmlPngFilter = (new DefaultFileFilter(".xml",
						"XML " + mxResources.get("File") + " (.xml)"));
				FileFilter vmlFileFilter = new DefaultFileFilter(".html",
						"VML " + mxResources.get("File") + " (.html)");
				String filename;

				if (showDialog || editor.getCurrentFile() == null) {
					String wd;

					if (lastDir != null) {
						wd = lastDir;
					} else if (editor.getCurrentFile() != null) {
						wd = editor.getCurrentFile().getParent();
					} else {
						wd = System.getProperty("user.dir");
					}

					JFileChooser fc = new JFileChooser(wd);
					fc.setDialogTitle(mxResources.get("SaveAs"));
					// Adds the default file format
					fc.addChoosableFileFilter(xmlPngFilter);
					
					fc.addChoosableFileFilter(new DefaultFileFilter(".py",
							"Python " + mxResources.get("File")
									+ " (.py)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".pdf",
							"PDF " + mxResources.get("File")
									+ " (.pdf)"));
					// Adds special vector graphics formats and HTML
					fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
							"mxGraph Editor " + mxResources.get("File")
									+ " (.mxe)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
							"Graph Drawing " + mxResources.get("File")
									+ " (.txt)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
							"SVG " + mxResources.get("File") + " (.svg)"));
					fc.addChoosableFileFilter(vmlFileFilter);
					fc.addChoosableFileFilter(new DefaultFileFilter(".html",
							"HTML " + mxResources.get("File") + " (.html)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".xml",
							"XML " + mxResources.get("File")
									+ " (.xml)"));

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();

					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<>();

					for (Object format : imageFormats) {
						String ext = format.toString().toLowerCase();
						formats.add(ext);
					}

					imageFormats = formats.toArray();

					for (Object imageFormat : imageFormats) {
						String ext = imageFormat.toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("."
							+ ext, ext.toUpperCase() + " "
							+ mxResources.get("File") + " (." + ext + ")"));
					}

					// Adds filter that accepts all supported image formats
					fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
							mxResources.get("allImages")));
					fc.setFileFilter(xmlPngFilter);
					
					int rc = fc.showSaveDialog(editor);

					if (rc != JFileChooser.APPROVE_OPTION) {
						return;
					} else {
						lastDir = fc.getSelectedFile().getParent();
					}

					filename = fc.getSelectedFile().getAbsolutePath();
					selectedFilter = fc.getFileFilter();

					if (selectedFilter instanceof DefaultFileFilter) {
						String ext = ((DefaultFileFilter) selectedFilter).getExtension();

						if (!filename.toLowerCase().endsWith(ext)) {
							filename += ext;
						}
					}

					if (new File(filename).exists()
							&& JOptionPane.showConfirmDialog(graphComponent,
									mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
						return;
					}
				} else {
					filename = editor.getCurrentFile().getAbsolutePath();
				}

				try {
					String ext = filename.substring(filename.lastIndexOf('.') + 1);

					if (ext.equalsIgnoreCase("svg")) {
						mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
								.drawCells(graph, null, 1, null,
										new CanvasFactory() {
											public mxICanvas createCanvas(int width, int height) {
												mxSvgCanvas canvas = new mxSvgCanvas(
														mxDomUtils.createSvgDocument(width, height));
												canvas.setEmbedded(true);
												return canvas;
											}
										});
						mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), filename);
					} else if (selectedFilter == vmlFileFilter) {
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("html")) {
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("py")) {
						ExportPython importToPython = new ExportPython((FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph()));
						importToPython.exportAutomate(filename);
					}
					else if (ext.equalsIgnoreCase("pdf")) {
						new ExportPDF((FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph())).exportAutomate(filename);
					}
					else if (ext.equalsIgnoreCase("xml")) {
						try {
							this.importerXML = new ExportXML((FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph()));
						} catch (ParserConfigurationException e1) {
							e1.printStackTrace();
						}
						
						importerXML.exportAutomate(filename);
						
						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
					else if (ext.equalsIgnoreCase("mxe")) {
						mxCodec codec = new mxCodec();
						String xml = mxXmlUtils.getXml(codec.encode(graph
								.getModel()));

						mxUtils.writeFile(xml, filename);

						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
					else if (ext.equalsIgnoreCase("txt")) {
						String content = mxGdCodec.encode(graph);
						mxUtils.writeFile(content, filename);
					} else {
						Color bg = null;

						if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
												.get("transparentBackground")) != JOptionPane.YES_OPTION) {
							bg = graphComponent.getBackground();
						}

						if (selectedFilter == xmlPngFilter || ext.equalsIgnoreCase("png")) {
							saveXmlPng(editor, filename, bg);
						}
					}
					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
					editor.updateTitle();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("Error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	@SuppressWarnings("serial")
	/**
	 * The action to open a new automaton by overwriting the previous one
	 */
	public static class NewAction extends AbstractAction {

		protected void resetEditor(GUIPanel editor) {
			editor.setModified(false);
			editor.setCurrentFile(null);
			editor.getUndoManager().clear();
			editor.getGraphComponent().zoomAndCenter();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);
			if (editor.isUnmodified()
					|| JOptionPane.showConfirmDialog(editor,
							mxResources.get("loseChanges")) == JOptionPane.YES_OPTION){
				((FiniteStateAutomatonGraph) editor.getGraphComponent().getGraph()).deleteAllElements();
				resetEditor(editor);
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class OrganicAction extends AbstractAction {
		
	    public OrganicAction() {
			super(mxResources.get("organicLayout"));
		}
	    
		@Override
		public void actionPerformed(ActionEvent e) {
			long temps = System.currentTimeMillis();
			GUIPanel editor = getEditor(e);
			mxGraph graph = editor.getGraphComponent().getGraph();
			editor.eraseAllGeometryPoint();
			graph.getModel().beginUpdate();
			try {
				mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
				layout.setForceConstant(100.0);
				layout.setMinDistanceLimit(50);
				layout.setMaxDistanceLimit(300);
				layout.setMaxIterations(500);
				layout.execute(graph.getDefaultParent());
			} finally {
				graph.getModel().endUpdate();
			}
		    editor.setAppStatusText(super.getValue(Action.NAME)+": "+(System.currentTimeMillis()-temps)+"ms");
		}
	}

	@SuppressWarnings("serial")
	public static class CircularAction extends AbstractAction {

		public CircularAction() {
			super(mxResources.get("circleLayout"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			long temps = System.currentTimeMillis();
			GUIPanel editor = getEditor(e);
			mxGraph graph = editor.getGraphComponent().getGraph();
			editor.eraseAllGeometryPoint();
			graph.getModel().beginUpdate();
			try {
				mxCircleLayout layout = new mxCircleLayout(graph);
				layout.execute(graph.getDefaultParent());
				editor.defaultParallelEdgeLayout.execute(graph.getDefaultParent());
			} finally {
				graph.getModel().endUpdate();
			}
			editor.setAppStatusText(super.getValue(Action.NAME)+": "+(System.currentTimeMillis()-temps)+"ms");
		}
	}

	@SuppressWarnings("serial")
	/**
	 * The action to open help in a browser
	 */
	public static class OpenHelpAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			if (Desktop.isDesktopSupported()) {
				 String url = "file://"+(EditorActions.class.getResource("/html/aide/HTML_Files/indexrepsonsive.html")).getPath();
				 System.out.println(url);
				 url = "file://"+System.getProperty("user.dir")+"/bin/html/aide/HTML_Files/indexrepsonsive.html";
//				 url.replace((char) 0x5C, '/');
				 
				 StringBuilder finalUrl = new StringBuilder();
				 for (Character c : url.toCharArray()) {
					 if(c.equals((char) 0x5C)) {
						 finalUrl.append('/');
					 } else {
						 finalUrl.append(c);
					 }
				 }
				 System.out.println(url);
				 url = finalUrl.toString();
				 System.out.println(url);
		        try {
			        if (Desktop.isDesktopSupported()) {
			            // Windows
			            Desktop.getDesktop().browse(new URI(url));
			        } else {
			            // Ubuntu
			            Runtime runtime = Runtime.getRuntime();
						runtime.exec("xdg-open " + url);
			        }
				} catch (IOException | URISyntaxException e1) {
					getEditor(e).displayMessage(mxResources.get("XMLERROR"), mxResources.get("Error"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
