package com.actian.nevernote.utility;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import com.actian.nevernote.constants.NeverNoteAPIConstants;

/**
 * 
 * @author sugan
 * Utility Class
 */
public class NeverNoteAPIUtility {

	/**
	 * 
	 * @param noteBookId
	 * @return
	 */
	public static URI getURI(String noteBookId) {
		
		UriBuilder builder = UriBuilder
				.fromPath(NeverNoteAPIConstants.HOST)
				.scheme(NeverNoteAPIConstants.PROTOCOL)
				.path(NeverNoteAPIConstants.API_CONTEXT_PATH+noteBookId+NeverNoteAPIConstants.NOTES);
		return builder.build();
	}

	/**
	 * 
	 * @param noteBookId
	 * @param noteId
	 * @return
	 */
	public static URI getURI(String noteBookId, UUID noteId) {
		UriBuilder builder = UriBuilder
				.fromPath(NeverNoteAPIConstants.HOST)
				.scheme(NeverNoteAPIConstants.PROTOCOL)
				.path(NeverNoteAPIConstants.API_CONTEXT_PATH+noteBookId+NeverNoteAPIConstants.NOTES+noteId);
		return builder.build();
	}

}
