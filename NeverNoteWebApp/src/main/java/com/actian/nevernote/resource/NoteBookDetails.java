package com.actian.nevernote.resource;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.actian.nevernote.constants.NeverNoteAPIConstants;
import com.actian.nevernote.modal.MessageDetails;
import com.actian.nevernote.modal.Note;
import com.actian.nevernote.modal.NoteBook;
import com.actian.nevernote.utility.NeverNoteAPIUtility;

/**
 * @author sugan
 * This is the Resource Class for all the operations related to notebook and its child hierarchy notes
 */
@Path("/notebooks")
public class NoteBookDetails 
{
	private static final String CLASS_NAME = NoteBookDetails.class.getName();
	private static final Logger logger = Logger.getLogger(CLASS_NAME);
	
	//There is no Persistent Storage, so creating this ArrayList, or else we will be using DAO layer
	private static ArrayList<NoteBook> noteBookList = new ArrayList<NoteBook>();
	
	//STATIC FINAL MESSAGE BEANS FOR HANDLING FIXED ERROR
	private static final MessageDetails SYSTEM_ERROR_MESSAGE = new  MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI001E"));
	private static final MessageDetails INVALID_NOTEBOOK_UUID = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI002E"));
	private static final MessageDetails INVALID_NOTEBOOK_INPUT = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI006E"));
	private static final MessageDetails NO_NOTEBOOK_FOUND = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI008E"));
	private static final MessageDetails INVALID_NOTE_INPUT = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI014E"));
	private static final MessageDetails INVALID_NOTE_INPUT_UPDATE = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI018E"));
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	
	/**
	 * This Post Method is used to create a NoteBook
	 * @param noteBookToBeCreated
	 * @return Response
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNoteBook(NoteBook noteBookToBeCreated)
	{
		final String METHOD_NAME = "createNoteBook";
		
		logger.entering(CLASS_NAME, METHOD_NAME);
		try {
			//Validation 
			if(noteBookToBeCreated.getNoteBookTitle() != null) { 
				//Generating Unique ID
				noteBookToBeCreated.setNoteBookId(UUID.randomUUID());
				String noteBookId =noteBookToBeCreated.getNoteBookId().toString();
				noteBookToBeCreated.setUri(NeverNoteAPIUtility.getURI(noteBookId));
				noteBookList.add(noteBookToBeCreated);
				logger.log(Level.INFO, NeverNoteAPIConstants.BUNDLE.getString("NNAPI001I"));
				return Response.status(Response.Status.CREATED).entity(noteBookToBeCreated).build();
			}
			else {
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI006E"));
				return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTEBOOK_INPUT).build();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI007E") + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
		finally {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
	}
	/**
	 * This  Method returns all the Books Available. 
	 * @return Response
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllNoteBook()
	{
		final String METHOD_NAME = "getAllNoteBook";
		try {
			logger.entering(CLASS_NAME, METHOD_NAME);
			if(noteBookList != null && noteBookList.size()>0) {
				logger.exiting(CLASS_NAME, METHOD_NAME);
				return Response.status(Response.Status.OK).entity(noteBookList).build();
			}
			else {
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI008E") );
				return Response.status(Response.Status.NOT_FOUND).entity(NO_NOTEBOOK_FOUND).build();
			}
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI009E") + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
		finally {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
		
		
	}
	/**
	 * This method Retrieves List of notes for a notebook and also filtered list of notes if a tag is provided
	 * @param noteBookId
	 * @param tagToBeSearched
	 * @return Response
	 */
	@GET
	@Path("/{notebookid}/notes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveNoteBook(@PathParam("notebookid") String noteBookId,@QueryParam("tag") String tagToBeSearched)
	{
		final String METHOD_NAME = "retrieveNoteBook";
		logger.entering(CLASS_NAME, METHOD_NAME);
		try
		{
			UUID noteBookID = UUID.fromString(noteBookId);
			List<Note> selectedNoteListByTag = null;
			logger.log(Level.INFO, MessageFormat.format(NeverNoteAPIConstants.BUNDLE.getString("NNAPI001D"), noteBookId, tagToBeSearched));
			List<NoteBook> selectedNoteBookList = getSelectedNoteBook(noteBookID);
			
			if(selectedNoteBookList != null && selectedNoteBookList.size()>0) 
			{
				
					ArrayList<Note> selectedNoteList =selectedNoteBookList.get(0).getNoteList();
					if(selectedNoteList!= null && selectedNoteList.size()>0)
					{
						if(tagToBeSearched== null)
						{
							logger.exiting(CLASS_NAME, METHOD_NAME);
							return Response.status(Response.Status.OK).entity(selectedNoteList).build();
						}
						selectedNoteListByTag = 
								selectedNoteList
							    .stream()
							    .filter(note-> (note.getTags()!= null && Arrays.asList(note.getTags()).toString().toLowerCase().contains(tagToBeSearched.toLowerCase())))
							    .collect(Collectors.toList()) ;
						if(selectedNoteListByTag!= null && selectedNoteListByTag.size()>0)
						{
							logger.exiting(CLASS_NAME, METHOD_NAME);
							return Response.status(Response.Status.OK).entity(selectedNoteListByTag).build();
						}
						else
						{
							logger.log(Level.INFO, NeverNoteAPIConstants.BUNDLE.getString("NNAPI010E"));
							MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI010E")+tagToBeSearched);
							return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
						}
					}
					else
					{
						logger.log(Level.INFO, NeverNoteAPIConstants.BUNDLE.getString("NNAPI011E") );
						MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI011E")+noteBookId);
						return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
					}
			}
			else {
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E"));
				MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E")+noteBookId);
				return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
			}
		
	}
	catch(IllegalArgumentException e) 
		{
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI012E") + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTEBOOK_UUID).build();
		}
	catch(Exception e) 
		{
			e.printStackTrace();
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI012E") + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
	finally {
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	}
	/**
	 * This Method Deleted a noteBook, given the NoteBook Id
	 * @param noteBookId
	 * @return Response
	 */
	@DELETE
	@Path("/{notebookid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNoteBook(@PathParam("notebookid") String noteBookId) {
		final String METHOD_NAME = "deleteNoteBook";

		logger.entering(CLASS_NAME, METHOD_NAME);
		try
		{
			UUID noteBookID = UUID.fromString(noteBookId);
			logger.log(Level.INFO, NeverNoteAPIConstants.BUNDLE.getString("NNAPI002D") +noteBookId);
			if(noteBookList.removeIf(noteBook-> noteBook.getNoteBookId().equals(noteBookID)))
			{
				return Response.status(Response.Status.NO_CONTENT).build();
			}
			else {
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E"));
				MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E")+noteBookId);
				return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
			}
			
		}
		catch(IllegalArgumentException e) 
		{
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI013E")+ e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTEBOOK_UUID).build();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI013E") + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
		finally {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
	}
	/**
	 * Create A Notes under a given NoteBook
	 * @param noteBookId
	 * @param noteToBeCreated
	 * @return Response
	 */
	@POST
	@Path("/{notebookid}/notes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNote(@PathParam("notebookid") String noteBookId, Note noteToBeCreated)
	{
		final String METHOD_NAME = "createNote";

		logger.entering(CLASS_NAME, METHOD_NAME);
		try {
			UUID noteBookID = UUID.fromString(noteBookId);
			
			List<NoteBook> selectedNoteBook = getSelectedNoteBook(noteBookID);
			if(selectedNoteBook != null && selectedNoteBook.size()>0) {
				noteToBeCreated.setNoteId(UUID.randomUUID());
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				noteToBeCreated.setCreatedTS(sdf.format(timestamp));
				//VALIDATION
				if(noteToBeCreated.getTitle()!= null && noteToBeCreated.getBody()!=null && 
						noteToBeCreated.getTags() != null && noteToBeCreated.getTags().length>0) {
					noteToBeCreated.setUri(NeverNoteAPIUtility.getURI(noteBookId,noteToBeCreated.getNoteId()));
					 ArrayList<Note> noteList= selectedNoteBook.get(0).getNoteList();
					 if( noteList== null)
		    			  noteList= new ArrayList<Note>();
					 noteList.add(noteToBeCreated);
					 selectedNoteBook.get(0).setNoteList(noteList);
					 logger.log(Level.INFO, NeverNoteAPIConstants.BUNDLE.getString("NNAPI002I") );
					 return Response.status(Response.Status.CREATED).entity(noteToBeCreated).build();
				}
				else {
					logger.log(Level.SEVERE,  NeverNoteAPIConstants.BUNDLE.getString("NNAPI014E"));
					return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTE_INPUT).build();
				}
				
			}
			else {
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E"));
				MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E")+noteBookId);
				return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
			}
			
		}
		catch(IllegalArgumentException e) 
		{
			logger.log(Level.SEVERE,  NeverNoteAPIConstants.BUNDLE.getString("NNAPI015E")  + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTEBOOK_UUID).build();
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI015E")  + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
		finally {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
    }
	
	/**
	 * Deletes a note, given the Note Book Id and Note ID
	 * @param noteBookId
	 * @param noteId
	 * @return Response
	 */
	@DELETE
	@Path("/{notebookid}/notes/{noteid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNote(@PathParam("notebookid") String noteBookId,@PathParam("noteid") String noteId)
	{
		final String METHOD_NAME = "deleteNote";
		logger.entering(CLASS_NAME, METHOD_NAME);
		try
		{
			UUID noteBookID = UUID.fromString(noteBookId);
			NoteBook noteBook = null;
			logger.log(Level.INFO,  MessageFormat.format(NeverNoteAPIConstants.BUNDLE.getString("NNAPI003D"), noteBookId, noteId));
			List<NoteBook> selectedNoteBookList = getSelectedNoteBook(noteBookID);
			if(selectedNoteBookList != null && selectedNoteBookList.size()>0) 
			{
					noteBook=selectedNoteBookList.get(0);
					
					ArrayList<Note> selectedNoteList =selectedNoteBookList.get(0).getNoteList();
					if(selectedNoteList.removeIf(note-> note.getNoteId().equals(UUID.fromString(noteId))))
					{
						noteBookList.remove(noteBook);
						noteBook.setNoteList(selectedNoteList);
						noteBookList.add(noteBook);
						return Response.status(Response.Status.NO_CONTENT).build();
					}
					else
					{
						logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI005E"));
						MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI005E")+noteId);
						return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
					}
			}
			else
			{
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E"));
				MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E")+noteBookId);
				return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
			}
		}
		catch(IllegalArgumentException e) 
		{
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI016E") + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTEBOOK_UUID).build();
		}
		
		catch(Exception e) 
		{
			e.printStackTrace();
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI016E") + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
		finally {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
	}
	
	/**
	 * Gets a note, given the Note Book Id and Note ID
	 * @param noteBookId
	 * @param noteId
	 * @return Response
	 */
	@GET
	@Path("/{notebookid}/notes/{noteid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNoteById(@PathParam("notebookid") String noteBookId,@PathParam("noteid") String noteId)
	{
		final String METHOD_NAME = "getNoteById";
		logger.entering(CLASS_NAME, METHOD_NAME);
		try
		{
			UUID noteBookID = UUID.fromString(noteBookId);
			UUID noteID = UUID.fromString(noteId);
			Note noteToBeRetrieved = null;
			List<NoteBook> selectedNoteBookList = getSelectedNoteBook(noteBookID);
			if(selectedNoteBookList != null && selectedNoteBookList.size()>0) 
			{
					
					ArrayList<Note> selectedNoteList =selectedNoteBookList.get(0).getNoteList();
					
					List<Note> noteListTobeRetrieved = 
							selectedNoteList
						    .stream()
						    .filter(note-> note.getNoteId().equals(noteID))
						    .collect(Collectors.toList()) ;
					
					if(noteListTobeRetrieved!= null && noteListTobeRetrieved.size()>0)
					{
						 noteToBeRetrieved = noteListTobeRetrieved.get(0);
						 logger.log(Level.INFO,  NeverNoteAPIConstants.BUNDLE.getString("NNAPI003I") );
						 return Response.status(Response.Status.OK).entity(noteToBeRetrieved).build();
					}
					else
					{
						logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI005E"));
						MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI005E")+noteId);
						return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
					}
			}
			else
			{
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E"));
				MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E")+noteBookId);
				return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
			}
					
		}
		catch(IllegalArgumentException e) 
		{
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI017E") + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTEBOOK_UUID).build();
		}
		
		catch(Exception e) 
		{
			e.printStackTrace();
			logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI017E") + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
		finally {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
	}
	
	/**
	 * Updates a given Note(Send the element that needs to be updated), provided NoteId and NoteBookId
	 * @param noteBookId
	 * @param noteId
	 * @param notetobeupdated
	 * @return Response
	 */
	
	@PUT
	@Path("/{notebookid}/notes/{noteid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateNote(@PathParam("notebookid") String noteBookId,@PathParam("noteid") String noteId,Note notetobeupdated)
	{
		final String METHOD_NAME = "updateNote";
		logger.entering(CLASS_NAME, METHOD_NAME);
		try
		{
			UUID noteBookID = UUID.fromString(noteBookId);
			UUID noteID = UUID.fromString(noteId);
			NoteBook noteBook = null;
			Note noteToBeChanged = null;
			logger.log(Level.INFO,  MessageFormat.format(NeverNoteAPIConstants.BUNDLE.getString("NNAPI004D"), noteBookId, noteId));
			List<NoteBook> selectedNoteBookList = getSelectedNoteBook(noteBookID);
			if(selectedNoteBookList != null && selectedNoteBookList.size()>0) 
			{
					noteBook=selectedNoteBookList.get(0);
					
					ArrayList<Note> selectedNoteList =selectedNoteBookList.get(0).getNoteList();
					
					List<Note> noteListTobeUpdated = 
							selectedNoteList
						    .stream()
						    .filter(note-> note.getNoteId().equals(noteID))
						    .collect(Collectors.toList()) ;
					
					if(noteListTobeUpdated!= null && noteListTobeUpdated.size()>0)
					{
						noteToBeChanged = noteListTobeUpdated.get(0);
						Note noteToBeChangedInList = noteToBeChanged;
						if(notetobeupdated!= null && (notetobeupdated.getTitle()!= null || notetobeupdated.getBody()!= null || notetobeupdated.getTags()!= null ))
						{
							if(notetobeupdated.getTitle()!= null)
								noteToBeChanged.setTitle(notetobeupdated.getTitle());
							if(notetobeupdated.getBody()!= null)
								noteToBeChanged.setBody(notetobeupdated.getBody());
							if(notetobeupdated.getTags()!= null)
								noteToBeChanged.setTags(notetobeupdated.getTags());
							Timestamp timestamp = new Timestamp(System.currentTimeMillis());
							noteToBeChanged.setModifiedTS(sdf.format(timestamp));
							selectedNoteList.remove(noteToBeChangedInList);
							selectedNoteList.add(noteToBeChanged);
							noteBookList.remove(noteBook);
							noteBook.setNoteList(selectedNoteList);
							noteBookList.add(noteBook);
							return Response.status(Response.Status.OK).entity(noteToBeChanged).build();
							
						}
						else
						{
							logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI017E"));
							return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTE_INPUT_UPDATE).build();
						}
					}
					else
					{
						logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI005E"));
						MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI005E")+noteId);
						return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
					}
			}
			else
			{
				logger.log(Level.SEVERE, NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E"));
				MessageDetails noDataFoundDetails = new MessageDetails(NeverNoteAPIConstants.BUNDLE.getString("NNAPI004E")+noteBookId);
				return Response.status(Response.Status.NOT_FOUND).entity(noDataFoundDetails).build();
			}
					
		}
		catch(IllegalArgumentException e) 
		{
			logger.log(Level.SEVERE,  NeverNoteAPIConstants.BUNDLE.getString("NNAPI019E") + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(INVALID_NOTEBOOK_UUID).build();
		}
		
		catch(Exception e) 
		{
			e.printStackTrace();
			logger.log(Level.SEVERE,  NeverNoteAPIConstants.BUNDLE.getString("NNAPI019E") + e.getMessage());
			return Response.serverError().entity(SYSTEM_ERROR_MESSAGE).build();
		}
		finally {
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
	}
			
		
	
	/************************************ RESUABLE METHODS ***********************************************/
	/**
	 * 
	 * @param noteBookId
	 * @param noteToBeCreated
	 * @return
	 */
	private List<NoteBook> getSelectedNoteBook(UUID noteBookID) {
		//JAVA 8 Feature
		List<NoteBook> selectedNoteList = 
				noteBookList
			    .stream()
			    .filter(noteBook-> noteBook.getNoteBookId().equals(noteBookID))
			    .collect(Collectors.toList()) ;
		return selectedNoteList;
	}
	
	
}
