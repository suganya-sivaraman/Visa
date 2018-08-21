/**
 * 
 */
package com.actian.nevernote.modal;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author sugan
 *
 */
public class NoteBook implements Serializable
{
	private static final long serialVersionUID = 3796218271871199800L;
	private UUID noteBookId;
	private String noteBookTitle;
	private ArrayList<Note> noteList;
	private URI uri;
	/**
	 * @return the noteBookId
	 */
	public UUID getNoteBookId() {
		return noteBookId;
	}
	/**
	 * @param noteBookId the noteBookId to set
	 */
	public void setNoteBookId(UUID noteBookId) {
		this.noteBookId = noteBookId;
	}
	/**
	 * @return the noteBookTitle
	 */
	public String getNoteBookTitle() {
		return noteBookTitle;
	}
	/**
	 * @param noteBookTitle the noteBookTitle to set
	 */
	public void setNoteBookTitle(String noteBookTitle) {
		this.noteBookTitle = noteBookTitle;
	}
	/**
	 * @return the noteList
	 */
	public ArrayList<Note> getNoteList() {
		return noteList;
	}
	/**
	 * @param noteList the noteList to set
	 */
	public void setNoteList(ArrayList<Note> noteList) {
		this.noteList = noteList;
	}
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}
	

}
