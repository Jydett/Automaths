package fr.iutvalence.automath.app.io.in;

import java.io.IOException;

import org.xml.sax.SAXException;

public interface Importer {

	void importAutomaton() throws SAXException, IOException;

}