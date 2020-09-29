package fr.iutvalence.automath.app.model;

/**
 * Header is the class that contains the user's information
 * <p>A user is characterized by the following information:</p>
 * <ul>
 * <li>A first name</li>
 * <li>A forename</li>
 * <li>A group</li>
 * <li>A student code</li>
 * <li>the mode in which he works</li>
 * </ul>
 */
public class Header {
	
	/**
	 * The enumeration of workable modes:
	 * <ul>
	 * <li>Examen, for users who use the application during exams</li>
	 * <li>Classic, for users who uses the application with all the features</li>
	 * </ul>
	 */
	public enum UserProfile {
		/**
		 * State of the application used during review with functionality in less
		 */
		EXAMEN,
		/**
		 * State of the application used outside the exam with all the features
		 */
		CLASSIC
    }
	
	/**
	 * The name of the user
	 */
	private String name;
	/**
	 * The last name of the user
	 */
	private String forename;
	/**
	 * The group of the user
	 */
	private String studentClass;
	/**
	 * The user's student code
	 */
	private String studentCode;
	/**
	 * The mode of the application and it is unique
	 */
	private final UserProfile modCode;
	
	/**
	 * The instance of the header
	 */
	private static Header INSTANCE;

	/**
	 * return the instance of the header with the chosen parameter
	 * 
	 * @param mod, the mode in which the user wants to work
	 * @return the instance of the Class Header
	 */
	public static Header getInstanceOfHeader(UserProfile mod){
		if(INSTANCE == null)
			INSTANCE = new Header(mod);
		return INSTANCE;
	}

	/**
	 * A constructor of ExportPDF, with the parameter the mode chosen by the user when launching the application 
	 * @param mod
	 */
	private Header(UserProfile mod){
		this.modCode = mod;
	}

	/**
	 * Return the name of the user
	 * 
	 * @return The name of the user, as a string of characters
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the forename of the user 
	 * 
	 * @return The forename of the user, as a string of characters
	 */
	public String getForename() {
		return forename;
	}

	/**
	 * Return the group of the user 
	 * 
	 * @return The group of the user, as a string of characters
	 */
	public String getStudentClass() {
		return studentClass;
	}

	/**
	 * Return the student code 
	 * 
	 * @return The student code of the user, as a string of characters
	 */
	public String getStudentCode() {
		return studentCode;
	}

	/**
	 * Return the mode in which the user uses the application 
	 * 
	 * @return The mode in which the user uses the application, as a string of characters
	 */
	public UserProfile getModCode() {
		return modCode;
	}

	/**
	 * Return the instance of the header
	 * <p>If the INSTANCE attribute is null, then the default mode will be classic mode</p>
	 * @return The instance of the Class Header
	 */
	public static Header getInstanceOfHeader() {
		if(INSTANCE == null)
			return Header.getInstanceOfHeader(UserProfile.CLASSIC);
		return INSTANCE;
	}

	/**
	 * Updating the first name 
	 * 
	 * @param name The new fist name, in the form of a string of characters
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Updating the forename
	 * 
	 * @param forename The forename, in the form of a string of characters
	 */
	public void setForename(String forename) {
		this.forename = forename;
	}

	/**
	 * Updating the group of the user
	 * 
	 * @param studentClass The group of the user, in the form of a string of characters
	 */
	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	/**
	 * Updating the student code
	 * 
	 * @param studentCode The student code, in the form of a string of characters
	 */
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}
}

