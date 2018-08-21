package com.actian.nevernote.modal;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

/**
 * @author sugan
 * Note Domain Object
 *
 */
public class Note implements Serializable

{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -869731359202088672L;
	
	private UUID noteId;
	private String	title;
	private String body;
	private String[] tags;
	private String createdTS;
	private String modifiedTS;
	private URI uri;
	/**
	 * @return the noteId
	 */
	public UUID getNoteId() {
		return noteId;
	}
	/**
	 * @param uuid the noteId to set
	 */
	public void setNoteId(UUID uuid) {
		this.noteId = uuid;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	/**
	 * @return the tags
	 */
	public String[] getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	/**
	 * @return the createdTS
	 */
	public String getCreatedTS() {
		return createdTS;
	}
	/**
	 * @param string the createdTS to set
	 */
	public void setCreatedTS(String createTS) {
		this.createdTS = createTS;
	}
	/**
	 * @return the modifiedTS
	 */
	public String getModifiedTS() {
		return modifiedTS;
	}
	/**
	 * @param modifiedTS the modifiedTS to set
	 */
	public void setModifiedTS(String modifiedTS) {
		this.modifiedTS = modifiedTS;
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
