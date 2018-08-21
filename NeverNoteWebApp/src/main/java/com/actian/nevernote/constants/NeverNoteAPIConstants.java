package com.actian.nevernote.constants;

import java.util.ResourceBundle;

/**
 * 
 * @author sugan
 * Constants related to NEVERNote Application
 * Certain Constants can be added as REP too, so that code need not be pushed.
 */
public class NeverNoteAPIConstants {

	//Related to Resource Bundle
	private static final String resourceBundleClass ="com.actian.nevernote.nl.NeverNoteResource";
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(resourceBundleClass);
	
	//Related to REST
	public static final String HOST = "localhost:8080";
	public static final String API_CONTEXT_PATH = "NeverNoteWebApp/v1/notebooks/";
	public static final String NOTES = "/notes/";
	public static final String PROTOCOL = "http";
	
	

}
