package com.actian.nevernote;


import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.actian.nevernote.modal.MessageDetails;
import com.actian.nevernote.modal.Note;
import com.actian.nevernote.modal.NoteBook;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

/**
 * @author sugan
 * Unit test Class for NEVERNOTE API
 */
//Uncomment the below Annotation to make CreateNoteBook and CreateNote Methods to be executed first For testing all testing the happy flows and Delete Method is last
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NeverNoteAPITest extends FunctionalTestConfigurations {

	private static NoteBook noteBookCreated = null;
	private static Note noteCreated = null;
	private static MessageDetails messageDetails = null;
	//To Test Negative Test Cases
	private static final UUID dummyUUID = UUID.randomUUID();
	/**
	 * Testing CreateNoteBook Method with Valid Values
	 */
	@Test
	public void a_createNoteBookTest() {
        
		NoteBook noteBookToBeCreated = new NoteBook();
        noteBookToBeCreated.setNoteBookTitle("Actian");
        
        noteBookCreated = RestAssured.given()
        .contentType(ContentType.JSON)
        .body(noteBookToBeCreated)
        .when().post("/notebooks").then()
        .assertThat().statusCode(Status.CREATED.getStatusCode())
        .and()
        .contentType(ContentType.JSON)
        .extract().as(NoteBook.class);
        
        Assert.assertTrue(noteBookCreated.getNoteBookTitle().equals(noteBookToBeCreated.getNoteBookTitle()));
        
        // NEGATIVE TEST CASE
        noteBookToBeCreated = new NoteBook();
		//SENDING EMPTY Object
		messageDetails = RestAssured.given()
        .contentType(ContentType.JSON)
        .body(noteBookToBeCreated)
        .when().post("/notebooks").then()
        .assertThat().statusCode(Status.BAD_REQUEST.getStatusCode())
        .and()
        .contentType(ContentType.JSON)
        .extract().as(MessageDetails.class);
       
		Assert.assertTrue(messageDetails.getErrorCode().equals("NNAPI006E"));
	}
	
	/**
	 * Testing getAllNoteBook Method
	 * This is also like a ping test method
	 */
	@Test
    public void getAllNoteBookTest() {
		
		if(noteBookCreated != null) {
			 RestAssured.given().when().get("/notebooks").then()
					.assertThat()
					.statusCode(Status.OK.getStatusCode())
					.and()
					.contentType(ContentType.JSON);
		}
		//THIS IS UNIT Testing case, if NoteBook is not created in this flow, then List will be empty
		else {
			messageDetails = RestAssured.given().when().get("/notebooks").then()
					.assertThat()
					.statusCode(Status.NOT_FOUND.getStatusCode())
					.and()
					.contentType(ContentType.JSON)
					.extract().as(MessageDetails.class);
			Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI008E"));
		}
		
    }
	/**
	 * Testing CreateNote Method with Valid Values
	 */
	@Test
	public void b_createNoteTest() {
		String[] tags = {"Java-8", "Oracle", "userGuide"};
        Note noteToBeCreated = new Note();
        noteToBeCreated.setTitle("Java");
        noteToBeCreated.setBody("Notes About Java 8 Features");
        noteToBeCreated.setTags(tags);
        if(noteBookCreated != null && noteBookCreated.getNoteBookId() != null) {
        	noteCreated =  RestAssured.given()
        					.contentType(ContentType.JSON)
        					.body(noteToBeCreated)
        					.when().post("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes")
        					.then()
        					.assertThat()
        					.statusCode(Status.CREATED.getStatusCode())
        					.and()
        					.contentType(ContentType.JSON)
        					.extract().as(Note.class);
        	
        	//Adding 2nd Note
        	noteToBeCreated.setTitle("Python");
            noteToBeCreated.setBody("Notes About Python");
            String[] pythonTags = {"Python", "ML"};
            noteToBeCreated.setTags(pythonTags);
            noteCreated =   RestAssured.given()
							.contentType(ContentType.JSON)
							.body(noteToBeCreated)
							.when().post("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes")
							.then()
							.assertThat()
							.statusCode(Status.CREATED.getStatusCode())
							.and()
							.contentType(ContentType.JSON)
							.extract().as(Note.class);
           
             //Adding 3rd Note
     		noteToBeCreated.setTitle("RESTAPI");
     		noteToBeCreated.setBody("Notes About RESTful Features");
     		String[] restAPITags = {"REST", "userGUIDE", "get", "post"};
     		noteToBeCreated.setTags(restAPITags);
     		noteCreated = RestAssured.given()
							.contentType(ContentType.JSON)
							.body(noteToBeCreated)
							.when().post("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes")
							.then()
							.assertThat()
							.statusCode(Status.CREATED.getStatusCode())
							.and()
							.contentType(ContentType.JSON)
							.extract().as(Note.class);
     		
     		//Testing Empty Note Creation
     		noteToBeCreated = new Note();
     		messageDetails = RestAssured.given()
					.contentType(ContentType.JSON)
					.body(noteToBeCreated)
					.when().post("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes")
					.then()
					.assertThat()
					.statusCode(Status.BAD_REQUEST.getStatusCode())
					.and()
					.contentType(ContentType.JSON)
					.extract().as(MessageDetails.class);
     		
     		//TEST For Error Code : NNAPI014E
     		Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI014E"));
        }
        // IF there is no NoteBook, we cannot create Notes
        
        messageDetails = RestAssured.given()
				        .contentType("application/json")
				        .body(noteToBeCreated)
				        .when().post("/notebooks/"+dummyUUID.toString()+"/notes").then()
				        .assertThat()
				        .statusCode(Status.NOT_FOUND.getStatusCode())
				        .and()
				        .contentType(ContentType.JSON)
				        .extract().as(MessageDetails.class);
        
        Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI004E"));
        
      //IF we don;t PASS UUID Type value then we will get exception.
        messageDetails = RestAssured.given()
		        .contentType("application/json")
		        .body(noteToBeCreated)
		        .when().post("/notebooks/nonUUIDString/notes").then()
		        .assertThat()
		        .statusCode(Status.BAD_REQUEST.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class);
        
    	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
        }
        
	/**
	 * Test Method : This method Retrieves List of notes for a notebook and also filtered list of notes if a tag is provided
	 */
	@Test
	public void retrieveNoteBookTest() {
		if(noteBookCreated != null && noteCreated!= null) {
			//Getting all the notes
			Note[] noteList =  RestAssured.given()
						        .when()
						        .get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes")
						        .then()
						        .assertThat()
						        .statusCode(Status.OK.getStatusCode())
						        .and()
						        .contentType(ContentType.JSON)
						        .extract().as(Note[].class);
			Assert.assertTrue(noteList.length == 3);
			
			//Two notes with userGuide parameter
	        String queryParameter = "userguide";
	        noteList = RestAssured.given()
			        .when()
			        .get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes?tag="+queryParameter)
			        .then()
			        .assertThat()
			        .statusCode(Status.OK.getStatusCode())
			        .and()
			        .contentType(ContentType.JSON)
			        .extract().as(Note[].class); 
	        //ONLY Two Note with tag userguide(Case insensitive) is present.
	        Assert.assertTrue(noteList.length == 2);
	        
	        //Dummy Tag should return 404
	        messageDetails = RestAssured.given()
	        .when()
	        .get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes?tag=dummyTag")
	        .then()
	        .assertThat()
	        .statusCode(Status.NOT_FOUND.getStatusCode())
	        .and()
	        .contentType(ContentType.JSON)
	        .extract().as(MessageDetails.class); 
	        
	      //TEST For Error Code : NNAPI010E
     		Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI010E"));
		}
		
		//TEST With A Dummy NoteBook ID
		messageDetails = RestAssured.given()
			        .when()
			        .get("/notebooks/"+dummyUUID.toString()+"/notes")
			        .then()
			        .assertThat()
			        .statusCode(Status.NOT_FOUND.getStatusCode())
			        .and()
			        .contentType(ContentType.JSON)
			        .extract().as(MessageDetails.class); 
			        
	     //TEST For Error Code : NNAPI004E
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI004E"));
	
		
		//TEST With A Bad Request
     	messageDetails = RestAssured.given()
		        .when()
		        .get("/notebooks/nonUUIDString/notes")
		        .then()
		        .assertThat()
		        .statusCode(Status.BAD_REQUEST.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class); 
		        
     	//TEST For Error Code : NNAPI002E
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
	}
	
	/**
	 * Test Method to validate Retrieve Note
	 */
	@Test
	public void getNoteByIdTest() {
		Note retrevedNote;
		if(noteBookCreated != null && noteCreated != null) {
			//Get the note details for the given noteId
			retrevedNote =  RestAssured.given()
					        .when()
					        .get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/"+noteCreated.getNoteId().toString())
					        .then()
					        .assertThat()
					        .statusCode(Status.OK.getStatusCode())
					        .and()
					        .contentType(ContentType.JSON)
					        .extract().as(Note.class);
			//TO Validate if the correct NoteId is retrieved. If ID is different then it is not correct Note
			// Testing unique parameter rather than title or Body which is not unique.
			Assert.assertTrue(noteCreated.getNoteId().equals(retrevedNote.getNoteId()));
			
			//Testing with a dummy Note ID should return 404.
			messageDetails = RestAssured.given()
							.when()
							.get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/"+dummyUUID.toString())
							.then()
							.assertThat()
							.statusCode(Status.NOT_FOUND.getStatusCode())
							.and()
							.contentType(ContentType.JSON)
							.extract().as(MessageDetails.class);
			//TEST For Error Code : NNAPI005E
	     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI005E"));
	     	
	     	//Testing with a bad data for NoteID
	     	messageDetails = RestAssured.given()
			        .when()
			        .get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/nonUUIDString")
			        .then()
			        .assertThat()
			        .statusCode(Status.BAD_REQUEST.getStatusCode())
			        .and()
			        .contentType(ContentType.JSON)
			        .extract().as(MessageDetails.class); 
			        
	     	//TEST For Error Code : NNAPI002E
	     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
		}
		
		//TEST With A Dummy Note ID and DummyNoteId
		messageDetails = RestAssured.given()
		        .when()
		        .get("/notebooks/"+dummyUUID.toString()+"/notes/"+dummyUUID.toString())
		        .then()
		        .assertThat()
		        .statusCode(Status.NOT_FOUND.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class); 
		        
     	//TEST For Error Code : NNAPI004E
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI004E"));
		
		//TEST With A Bad Request for Both NotID and NoteBookId
     	messageDetails = RestAssured.given()
		        .when()
		        .get("/notebooks/nonUUIDString/notes/nonUUIDString")
		        .then()
		        .assertThat()
		        .statusCode(Status.BAD_REQUEST.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class); 
		        
     	//TEST For Error Code : NNAPI002E
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
	}
	
	@Test
	public void updateNoteTest() {
		
        Note updatedNote;
		if(noteBookCreated != null && noteCreated != null) {
			Note noteToBeUpdated = new Note();
	        noteToBeUpdated.setBody("Notes About RESTful Features using Jersey");
			//Update the note details for the given noteId
			updatedNote =  RestAssured.given()
					        .contentType(ContentType.JSON)
					        .body(noteToBeUpdated)
					        .when()
					        .put("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/"+noteCreated.getNoteId().toString())
					        .then()
					        .assertThat()
					        .statusCode(Status.OK.getStatusCode())
					        .and()
					        .contentType(ContentType.JSON)
					        .extract().as(Note.class);
			//TO Validate if the correct NoteId is Updated. If ID is different then it is not correct Note
			// Testing unique parameter rather than title or Body which is not unique.
			Assert.assertTrue(noteCreated.getNoteId().equals(updatedNote.getNoteId()));
			
			//Send an empty Bean so that you get a Bad Data Response
			noteToBeUpdated = new Note();
			messageDetails =  RestAssured.given()
			        .contentType(ContentType.JSON)
			        .body(noteToBeUpdated)
			        .when()
			        .put("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/"+noteCreated.getNoteId().toString())
			        .then()
			        .assertThat()
			        .statusCode(Status.BAD_REQUEST.getStatusCode())
			        .and()
			        .contentType(ContentType.JSON)
			        .extract().as(MessageDetails.class);
			
			//TEST For Error Code : NNAPI017E
	     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI018E"));
			
	     	//TEST with a note ID that is not present
			messageDetails = RestAssured.given()
					        .contentType(ContentType.JSON)
					        .body(noteToBeUpdated)
					        .when()
					        .put("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/"+dummyUUID.toString())
					        .then()
					        .assertThat()
					        .statusCode(Status.NOT_FOUND.getStatusCode())
					        .and()
					        .contentType(ContentType.JSON)
					        .extract().as(MessageDetails.class);

			//TEST For Error Code : NNAPI005E
	     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI005E"));
	     	
	     	//Testing with a bad data for NoteID
	     	messageDetails = RestAssured.given()
					        .when()
					        .get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/nonUUIDString")
					        .then()
					        .assertThat()
					        .statusCode(Status.BAD_REQUEST.getStatusCode())
					        .and()
					        .contentType(ContentType.JSON)
					        .extract().as(MessageDetails.class); 
			        
	     	//TEST For Error Code : NNAPI002E
	     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
		}
		
		//TEST With A Dummy Note ID and DummyNoteId
		messageDetails = RestAssured.given()
		        .when()
		        .get("/notebooks/"+dummyUUID.toString()+"/notes/"+dummyUUID.toString())
		        .then()
		        .assertThat()
		        .statusCode(Status.NOT_FOUND.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class); 
		        
     	//TEST For Error Code : NNAPI004E
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI004E"));
		
		//TEST With A Bad Request for Both NotID and NoteBookId
     	messageDetails = RestAssured.given()
		        .when()
		        .get("/notebooks/nonUUIDString/notes/nonUUIDString")
		        .then()
		        .assertThat()
		        .statusCode(Status.BAD_REQUEST.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class); 
		        
     	//TEST For Error Code : NNAPI002E
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
     	
	}
	/**
	 * To Test Delete Note Method
	 */
	@Test
	public void y_deleteNoteTest()
	{
		if(noteBookCreated != null && noteCreated != null) {
			
			messageDetails =RestAssured.given()
			        .when()
			        .delete("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/"+dummyUUID.toString().toString())
			        .then()
			        .assertThat()
			        .statusCode(Status.NOT_FOUND.getStatusCode())
			        .and()
			        .contentType(ContentType.JSON)
			        .extract().as(MessageDetails.class);
			//TEST For Error Code : NNAPI005E
	     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI005E"));
	     	
	     	//TEST Deleting Node
			RestAssured.given()
	        .when()
	        .delete("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes/"+noteCreated.getNoteId().toString())
	        .then()
	        .assertThat()
	        .statusCode(Status.NO_CONTENT.getStatusCode());
			//After Deletion note should return 404
			RestAssured.given()
	        .when()
	        .get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes"+noteCreated.getNoteId().toString())
	        .then()
	        .assertThat()
	        .statusCode(Status.NOT_FOUND.getStatusCode());
			noteCreated = null;
		}
		//Test Dummy UUID it should return 404
		messageDetails =RestAssured.given()
					        .when()
					        .delete("/notebooks/"+dummyUUID.toString()+"/notes/"+dummyUUID.toString().toString())
					        .then()
					        .assertThat()
					        .statusCode(Status.NOT_FOUND.getStatusCode())
					        .and()
					        .contentType(ContentType.JSON)
					        .extract().as(MessageDetails.class);
		
		//TEST For Error Code : NNAPI004E
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI004E"));
     	
		//Test With a bad request
     	messageDetails =RestAssured.given()
		        .when()
		        .delete("/notebooks/noUUIDString/notes/noUUIDString")
		        .then()
		        .assertThat()
		        .statusCode(Status.BAD_REQUEST.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class);

		//TEST For Error Code : NNAPI004E
		Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
		
	}
	/**
	 * To Test Delete Notebook Method
	 */
	@Test
	public void z_deleteNoteBookTest() {

		if(noteBookCreated != null) {
			RestAssured.given()
	        .when().
	        delete("/notebooks/"+noteBookCreated.getNoteBookId().toString()).
	        then().
	        statusCode(Status.NO_CONTENT.getStatusCode());
			//After Deletion notebook should return 404
			RestAssured.given()
	        .when().get("/notebooks/"+noteBookCreated.getNoteBookId().toString()+"/notes").then()
	        .assertThat()
	        .statusCode(Status.NOT_FOUND.getStatusCode());
			noteBookCreated = null;
		}
		//Test Dummy UUID it should return 404
		messageDetails =RestAssured.given()
					        .when()
					        .delete("/notebooks/"+dummyUUID.toString())
					        .then()
					        .assertThat()
					        .statusCode(Status.NOT_FOUND.getStatusCode())
					        .and()
					        .contentType(ContentType.JSON)
					        .extract().as(MessageDetails.class);
		
		//TEST For Error Code : NNAPI004E
		
     	Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI004E"));
     	
		//Test With a bad request
     	messageDetails =RestAssured.given()
		        .when()
		        .delete("/notebooks/noUUIDString")
		        .then()
		        .assertThat()
		        .statusCode(Status.BAD_REQUEST.getStatusCode())
		        .and()
		        .contentType(ContentType.JSON)
		        .extract().as(MessageDetails.class);

		//TEST For Error Code : NNAPI004E
		Assert.assertTrue(messageDetails.getErrorCode().equalsIgnoreCase("NNAPI002E"));
		
	}
}
