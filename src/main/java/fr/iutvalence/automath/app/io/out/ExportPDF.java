package fr.iutvalence.automath.app.io.out;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxpdf.text.Document;
import com.mxpdf.text.DocumentException;
import com.mxpdf.text.Rectangle;
import com.mxpdf.text.pdf.PdfContentByte;
import com.mxpdf.text.pdf.PdfWriter;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.Header;

import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * ExportPDF is the class that contains the action to convert the graphical graph into a PDF file
 */
public class ExportPDF implements Exporter {

	private final static String infoStyle =
			mxConstants.STYLE_FILLCOLOR + "=white;" +
			mxConstants.STYLE_FONTCOLOR + "=black;" +
			mxConstants.STYLE_STROKECOLOR + "=white;" +
			mxConstants.STYLE_WHITE_SPACE + "=wrap;" +
			mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_ELLIPSE + ";";
	/**
	 * The graph of the application
	 */
	private final FiniteStateAutomatonGraph graph;
	
	/**
	 * A constructor of ExportPDF, with the parameter graph
	 * @param graph The graph of application
	 */
	public ExportPDF(FiniteStateAutomatonGraph graph){
		this.graph = graph;
	}
	
	/**
	 * Convert the graph to PDF and save the result to an (.pdf) file with the specified path in parameter
	 * @param file The path to saving the file
	 */
	public void exportAutomate(String file){
		mxRectangle bounds = graph.getGraphBounds();
		graph.getModel().beginUpdate();
		Header header = Header.getInstanceOfHeader();
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append(mxResources.get("HeaderName")).append(":");
		sb.append(header.getName());
		sb.append("\t").append(mxResources.get("HeaderForename")).append(":");
		sb.append(header.getForename());
		sb.append("\t").append(mxResources.get("HeaderGroup")).append(":");
		sb.append(header.getStudentClass());
		sb.append("\t").append(mxResources.get("HeaderStudentCode")).append(":");
		sb.append(header.getStudentCode());
		sb.append("\t").append(mxResources.get("HeaderMode")).append(":");
		sb.append(header.getModCode());
		try {
			Object obj = graph.insertVertex(graph.getDefaultParent(), null, sb, 10, bounds.getWidth() + 75, 350, 75,
					infoStyle + mxConstants.STYLE_PERIMETER + "=" + bounds.getWidth() + ";");
			bounds = graph.getGraphBounds();

			Document document = new Document(new Rectangle((float) (bounds.getWidth() + 5), (float) (bounds.getHeight() + 5)));
			PdfWriter writer;
			try {
				writer = PdfWriter.getInstance(document, new FileOutputStream(file));
				document.open();
				final PdfContentByte cb = writer.getDirectContent();

				mxGraphics2DCanvas canvas = (mxGraphics2DCanvas) mxCellRenderer
						.drawCells(graph, null, 1, null, new CanvasFactory() {
							public mxICanvas createCanvas(int width, int height) {
								Graphics2D g2 = cb.createGraphics(width, height);
								return new mxGraphics2DCanvas(g2);
							}
						});
				canvas.getGraphics().dispose();
				document.close();
			} catch (FileNotFoundException | DocumentException e1) {
				e1.printStackTrace();
			}
			graph.removeCells(new Object[]{obj});
		} finally {
			graph.getModel().endUpdate();
		}
	}
}