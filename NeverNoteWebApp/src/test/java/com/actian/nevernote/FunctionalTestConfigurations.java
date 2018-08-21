package com.actian.nevernote;

import org.junit.BeforeClass;

import io.restassured.RestAssured;

/**
 * @author sugan
 * This Class sets up the Server Configurations for the REST API
 */
public class FunctionalTestConfigurations {
	
	@BeforeClass
	public static void setup() {
        String port = System.getProperty("server.port");
        if (port == null) {
            RestAssured.port = Integer.valueOf(8080);
        }
        else{
            RestAssured.port = Integer.valueOf(port);
        }


        String basePath = System.getProperty("server.base");
        if(basePath==null){
            basePath = "/NeverNoteWebApp/v1/";
        }
        RestAssured.basePath = basePath;

        String baseHost = System.getProperty("server.host");
        if(baseHost==null){
            baseHost = "http://localhost";
        }
        RestAssured.baseURI = baseHost;
    }


}
