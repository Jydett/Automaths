package fr.iutvalence.automath.app.io.in;

public interface Importer {

	void importAutomaton(boolean clearBefore) throws Exception;

	default void importAutomaton() throws Exception {
		importAutomaton(true);
	}

}