package fr.iutvalence.automath.app.editor;

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
import com.mxgraph.util.*;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.io.in.ImporterXML;
import fr.iutvalence.automath.app.io.out.*;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.view.menu.PopUpMenu;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.utils.DefaultFileFilter;
import fr.iutvalence.automath.app.view.utils.FilePreviewerWithWorker;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Store of all sort of action doable from the editor
 */
public final class EditorActions {

	/**
	 * To retrieve the graphic panel from an event
	 * @param e An action
	 * @return the main panel or null if the event is unrelated to the event
	 */
	public static GUIPanel getEditor(ActionEvent e) {
		if (e.getSource() instanceof Component) {
			Component component = (Component) e.getSource();

			while (component != null && !(component instanceof GUIPanel)) {
				if (component instanceof PopUpMenu) return ((PopUpMenu)component).getEditor();
				component = component.getParent();
			}
			return (GUIPanel) component;
		}

		return null;
	}

	/**
	 * Modify a state in initial state
	 */
	@SuppressWarnings("serial")
	public static class SetInitialAction extends JCheckBoxMenuItem {
		public SetInitialAction(String name) {
			super(name);

			addActionListener(e -> {
				GUIPanel editor = getEditor(e);
				FiniteStateAutomatonGraph graph =  (FiniteStateAutomatonGraph) editor.getGraphComponent().getGraph();
				Object[] states = graph.getSelectionCells();
				mxIGraphModel model = graph.getModel();
				model.beginUpdate();
				for (Object state : states) {
					if (((mxCell) state).isVertex()) {
						StateInfo oldInfo = ((StateInfo) ((mxCell) state).getValue());
						StateInfo newInfo = oldInfo.withStarting(!oldInfo.isStarting());
						model.setValue(state, newInfo);
						newInfo.refresh((mxCell) state, graph);
					}
				}
				model.endUpdate();
				editor.getCellDescriptorPanel().refresh();
			});
		}
	}

	/**
	 * Modify a state in final state
	 */
	@SuppressWarnings("serial")
	public static class SetFinalAction extends JCheckBoxMenuItem
	{
		public SetFinalAction(String name) {
			super(name);
			addActionListener(e -> {
				GUIPanel editor = getEditor(e);
				FiniteStateAutomatonGraph graph = (FiniteStateAutomatonGraph) editor.getGraphComponent().getGraph();
				Object[] states = graph.getSelectionCells();
				mxIGraphModel model = graph.getModel();
				model.beginUpdate();
				for (Object state : states) {
					if (((mxCell) state).isVertex()) {
						StateInfo oldInfo = ((StateInfo) ((mxCell) state).getValue());
						StateInfo newInfo = oldInfo.withAccepting(!oldInfo.isAccepting());
						model.setValue(state, newInfo);
						newInfo.refresh((mxCell) state, graph);
					}
				}
				model.endUpdate();
				editor.getCellDescriptorPanel().refresh();
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

	/**
	 * To quit the application
	 */
	@SuppressWarnings("serial")
	public static class ExitAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);

			if (editor != null) {
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

	/**
	 * the history of actions that are stored to restore them
	 */
	@SuppressWarnings("serial")
	public static class HistoryAction extends AbstractAction {

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

	/**
	 * The action to minimize the current automaton
	 */
	@SuppressWarnings("serial")
	public static class MinimizeAction extends AbstractAction{

	    public MinimizeAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);
			if (!editor.getAllowMinimisation()) return;
			long t0 = System.currentTimeMillis();
			mxGraph graph = editor.getGraphComponent().getGraph();
			graph.getModel().beginUpdate();
			try {
				((FiniteStateAutomatonGraph) graph).determinize();
				new EditorActions.OrganicAction().actionPerformed(e);
			} finally {
				graph.getModel().endUpdate();
			}
			editor.setAppStatusText(mxResources.get("Minimization") + " : " + (System.currentTimeMillis() - t0) + "ms");
		}

	}

	/**
	 * The action to determinize the current automaton
	 */
	@SuppressWarnings("serial")
	public static class DeterminizeAction extends AbstractAction{

	    public DeterminizeAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);
			if (! editor.getAllowDeterminisation()) return;
			long t0 = System.currentTimeMillis();
			mxGraph graph = editor.getGraphComponent().getGraph();
			graph.getModel().beginUpdate();
			try {
				((FiniteStateAutomatonGraph) editor.getGraphComponent().getGraph()).determinize();
				new EditorActions.OrganicAction().actionPerformed(e);
			} finally {
				graph.getModel().endUpdate();
			}
			editor.setAppStatusText(mxResources.get("Determinization") + " : " + (System.currentTimeMillis() - t0) + "ms");
		}

	}

	/**
	 * The action open a template
	 */
	@SuppressWarnings("serial")
	public static class OpenTemplate extends AbstractAction {

		protected String lastDir;
		private ImporterXML importerXML;

		protected void resetEditor(GUIPanel editor) {
			editor.setModified(true);
			editor.getGraphComponent().zoomAndCenter();
		}

		/**
		 * Reads XML+PNG format.
		 */
		protected void openXmlPng(GUIPanel editor, File file) throws IOException {
			Map<String, String> text = mxPngTextDecoder.decodeCompressedText(new FileInputStream(file));

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
			JOptionPane.showMessageDialog(editor, mxResources.get("imageContainsNoDiagramData"));
		}

		protected void openGD(GUIPanel editor, File file, String gdText) {
			mxGraph graph = editor.getGraphComponent().getGraph();
			if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
				try {
					importerXML.setFile(file.getAbsolutePath());
					importerXML.importAutomaton(false);
					editor.getGraphComponent().zoomAndCenter();
				} catch (Exception e) {
					editor.displayMessage(mxResources.get("XMLERROR"), mxResources.get("Error"),
							JOptionPane.ERROR_MESSAGE);
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
			if (editor != null) {
				mxGraph graph = editor.getGraphComponent().getGraph();
				if (graph != null) {
					String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");
					JFileChooser fc = new JFileChooser(wd);
					FiniteStateAutomatonGraph graphCustom = (FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph());
					FilePreviewerWithWorker fileChooserPreviewer = new FilePreviewerWithWorker(fc, graphCustom.getAutomaton());
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					panel.add(fileChooserPreviewer, BorderLayout.CENTER);
					panel.add(new JLabel(" " + mxResources.get("Preview")), BorderLayout.NORTH);
					fc.setAccessory(panel);
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
							"PNG+XML " + mxResources.get("File")
									+ " (.png)"));

					// Adds file filter for VDX import
					fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
							"XML Drawing " + mxResources.get("File")
									+ " (.vdx)"));

					// Adds file filter for GD import
					fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
							"Graph Drawing " + mxResources.get("File")
									+ " (.txt)"));


					fc.setFileFilter(defaultFilter);

					int rc = fc.showDialog(editor,
							mxResources.get("OpenTemplate"));

					if (rc == JFileChooser.APPROVE_OPTION) {
						lastDir = fc.getSelectedFile().getParent();

						try {
							if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png")) {
								openXmlPng(editor, fc.getSelectedFile());
							} else if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".txt")) {
								openGD(editor, fc.getSelectedFile(),
										mxUtils.readFile(fc
												.getSelectedFile()
												.getAbsolutePath()));
							} else if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".xml")) {
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

	/**
	 * The action to import files with a preview
	 */
	@SuppressWarnings("serial")
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
			try (final FileInputStream data = new FileInputStream(file)) {
				Map<String, String> text = mxPngTextDecoder.decodeCompressedText(data);

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
					editor.displayMessage(mxResources.get("XMLERROR"), mxResources.get("Error"),
							JOptionPane.ERROR_MESSAGE);
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
						JPanel panel = new JPanel();
						panel.setLayout(new BorderLayout());
						panel.add(fileChooserPreviewer, BorderLayout.CENTER);
						panel.add(new JLabel(" " + mxResources.get("Preview")), BorderLayout.NORTH);
						fc.setAccessory(panel);
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
	@RequiredArgsConstructor
	public static abstract class AbstractSaveAction extends AbstractAction {

		protected final boolean showDialog;
		protected String lastDir = null;

		protected DefaultFileFilter exporterToFileFilter(ExportersFactory.SupportedExtensions ext) {
			return createFileFilter(ext.getExt(), ext.getFormatFriendlyName());
		}

		protected DefaultFileFilter createFileFilter(String ext, String friendlyName) {
			return new DefaultFileFilter('.' + ext,
					friendlyName + " "
							+ mxResources.get("File") + " (." + ext + ")");
		}

		public void actionPerformed(ActionEvent e) {
			GUIPanel editor = getEditor(e);

			if (editor != null) {
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				FileFilter selectedFilter;
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

					populateFileChooser(fc);

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
					Exporter exporter = ExportersFactory.getForExtension(ext);
					if (exporter instanceof ExportPNGXML) {
						Color bg = null;

						if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
								graphComponent, mxResources
										.get("transparentBackground")) != JOptionPane.YES_OPTION) {
							bg = graphComponent.getBackground();
						}
						((ExportPNGXML) exporter).setBg(bg);
						((ExportPNGXML) exporter).setMxGraphComponent(graphComponent);
					}

					try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(filename))))) {
						exporter.exportAutomaton((FiniteStateAutomatonGraph) graph, out);
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

		protected abstract void populateFileChooser(JFileChooser fc);

	}

	/**
	 * The action to save in the mode of examination which allows to record in pdf
	 */
	@SuppressWarnings("serial")
	public static class SaveExamAction extends AbstractSaveAction {

		public SaveExamAction(boolean showDialog) {
			super(showDialog);
		}

		@Override
		protected void populateFileChooser(JFileChooser fc) {
			fc.addChoosableFileFilter(exporterToFileFilter(ExportersFactory.SupportedExtensions.PDF));
		}
	}

	/**
	 * The action to save in all available models
	 */
	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractSaveAction {
		public SaveAction(boolean showDialog) {
			super(showDialog);
		}

		@Override
		protected void populateFileChooser(JFileChooser fc) {

			// Finds all distinct extensions
			Set<String> distinctImgExtensions = new HashSet<>();
			for (ExportersFactory.SupportedExtensions ext : ExportersFactory.supportedExt()) {
				fc.addChoosableFileFilter(exporterToFileFilter(ext));
				distinctImgExtensions.add(ext.getExt());
			}

			// Adds a filter for each supported image format
			Object[] imageFormats = ImageIO.getReaderFormatNames();

			for (Object format : imageFormats) {
				String ext = format.toString().toLowerCase();
				distinctImgExtensions.add(ext);
			}

			for (String ext : distinctImgExtensions) {
				fc.addChoosableFileFilter(createFileFilter(ext, ext.toUpperCase()));
			}

			fc.setFileFilter(exporterToFileFilter(ExportersFactory.getDefaultExt()));

		}
	}

	/**
	 * The action to open a new automaton by overwriting the previous one
	 */
	@SuppressWarnings("serial")
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

	/**
	 * The action to open help in a browser
	 */
	@SuppressWarnings("serial")
	public static class OpenHelpAction extends AbstractAction {

		private final static String tempDirName = "automathstemp";

		public void actionPerformed(ActionEvent e) {
			String tempDir = System.getProperty("java.io.tmpdir");
			if (tempDir.charAt(tempDir.length() - 1) != File.separatorChar) {
				tempDir = tempDir + File.separatorChar;
			}
			String tempPath = tempDir + tempDirName;
			File tempdir = new File(tempPath);
			if (! tempdir.exists()) {
				tempdir.mkdir();
				try {
					getPathsFromResourceJAR("html", path -> {
						try {
							String filePathInJAR = path.toString().replace("/", File.separator);

							String htmlHint = File.separatorChar + "html" + File.separatorChar;

							String fileRelativePath = filePathInJAR.substring(filePathInJAR.indexOf(htmlHint) + htmlHint.length() - 1);

							String fullFilePath = tempPath + fileRelativePath;
							new File(fullFilePath.substring(0, fullFilePath.lastIndexOf(File.separatorChar))).mkdirs();
							File tempFileOut = new File(fullFilePath);
							tempFileOut.createNewFile();
							try(InputStream in = new BufferedInputStream(Files.newInputStream(path));
								OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFileOut, false))) {
								copy(in, out);
							}
							return null;
						} catch (Exception exception) {
							return exception;
						}
					});
				} catch (URISyntaxException | IOException exception) {
					exception.printStackTrace();
					return;
				}
				tempdir.deleteOnExit();
			}

			String url = ("file://" + tempPath + "|aide|HTML_Files|indexrepsonsive.html")
				.replace("|", "/")
				.replace(File.separator, "/");
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
				getEditor(e).displayMessage(mxResources.get("Exception"), mxResources.get("Error"), JOptionPane.ERROR_MESSAGE);
			}
		}

		void copy(InputStream source, OutputStream target) throws IOException {
			byte[] buf = new byte[8192];
			int length;
			while ((length = source.read(buf)) > 0) {
				target.write(buf, 0, length);
			}
		}

		private void getPathsFromResourceJAR(String folder, Function<Path, Exception> whatToDo)
			throws URISyntaxException, IOException {

			// get path of the current running JAR
			String jarPath = getClass().getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();
			if (! jarPath.endsWith(".jar")) {
				getAllFilesFromResource(folder)
					.stream()
					.map(whatToDo)
					.filter(Objects ::nonNull)
					.findAny()
					.ifPresent(Throwable :: printStackTrace);
			} else {
				// file walks JAR
				URI uri = URI.create("jar:file:" + jarPath);
				try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
					Files.walk(fs.getPath(folder))
						.filter(Files::isRegularFile)
						.map(whatToDo)
						.filter(Objects ::nonNull)
						.findAny()
						.ifPresent(Throwable :: printStackTrace);
				}
			}
		}

		private List<Path> getAllFilesFromResource(String folder)
			throws URISyntaxException, IOException {

			ClassLoader classLoader = getClass().getClassLoader();

			URL resource = classLoader.getResource(folder);

			// dun walk the root path, we will walk all the classes

			return Files.walk(Paths.get(resource.toURI()))
				.filter(Files::isRegularFile)
				.collect(Collectors.toList());
		}
	}
}
