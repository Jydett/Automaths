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

import java.awt.*;
import java.io.*;

/**
 * ExportPDF is the class that contains the action to convert the graphical graph into a PDF file
 */
public class ExportPDF implements Exporter {
	private final static String infoStyle =
			mxConstants.STYLE_OVERFLOW + "=visible;" +
			mxConstants.STYLE_FILLCOLOR + "=white;" +
			mxConstants.STYLE_FONTCOLOR + "=black;" +
			mxConstants.STYLE_STROKECOLOR + "=white;" +
			mxConstants.STYLE_FONTFAMILY + "=" + Font.MONOSPACED + ";" +
			mxConstants.STYLE_FONTSTYLE+"=1"
			;
	/**
	 * {@inheritDoc}
	 */
	public void exportAutomaton(FiniteStateAutomatonGraph graph, DataOutputStream outputStream) throws Exception {
		mxRectangle bounds = graph.getGraphBounds();
		Header header = Header.getInstanceOfHeader();
		StringBuilder sb = new StringBuilder();
		sb.append("\u00a0\u00a0\u00a0\u00a0\u00a0").append(mxResources.get("HeaderName")).append(":");
		sb.append(header.getName());
		sb.append(" ").append(mxResources.get("HeaderForename")).append(":");
		sb.append(header.getForename());
		sb.append(" ").append(mxResources.get("HeaderGroup")).append(":");
		sb.append(header.getStudentClass());
		sb.append(" ").append(mxResources.get("HeaderStudentCode")).append(":");
		sb.append(header.getStudentCode());
		sb.append(" ").append(mxResources.get("HeaderMode")).append(":");
		sb.append(header.getModCode());
		sb.append("\u00a0\u00a0\u00a0\u00a0\u00a0");
		graph.getModel().beginUpdate();
		Object obj = graph.insertVertex(graph.getDefaultParent(), null, sb, bounds.getX(), bounds.getY() - 50, 1, 1,
				infoStyle);
		graph.getModel().endUpdate();
		try {
			bounds = graph.getGraphBounds();
			Document document = new Document(new Rectangle((float) (bounds.getWidth() + 5), (float) (bounds.getHeight() + 10)));
			PdfWriter writer;
			try {
				writer = PdfWriter.getInstance(document, outputStream);
				document.open();
				final PdfContentByte cb = writer.getDirectContent();

				mxGraphics2DCanvas canvas = (mxGraphics2DCanvas) mxCellRenderer
						.drawCells(graph, null, 1, null, new CanvasFactory() {
							public mxICanvas createCanvas(int width, int height) {
								return new mxGraphics2DCanvas(cb.createGraphics(width, height));
							}
						});
				canvas.getGraphics().dispose();
				document.addTitle(sb.toString());
				document.close();
			} catch (DocumentException e1) {
				e1.printStackTrace();
			}
		} finally {
			graph.getModel().beginUpdate();
			graph.removeCells(new Object[]{obj});
			graph.getModel().endUpdate();
		}
	}
}
