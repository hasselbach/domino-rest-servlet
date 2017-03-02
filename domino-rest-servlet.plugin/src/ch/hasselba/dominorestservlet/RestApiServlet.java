package ch.hasselba.dominorestservlet;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.domino.osgi.core.context.ContextInfo;

import lotus.domino.Base;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.notes.addins.DominoServer;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RestApiServlet {

	final static String RESTAPI_GROUP = "RESTAPIAccessAllowed";
	
	@GET
	@Path("/helloworld/")
	public Response getHelloWorld() {
		
		try {
			 checkAuthentication();
		} catch (NotAuthenticatedException nae) {
			return Response.status(403).build();
		}
		 
		return Response.ok(new HelloWorld(), MediaType.APPLICATION_JSON).build();
	}

	@POST
    @Path("/helloworld/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postHelloWorld(HelloWorld helloWorld) {
		
		try {
			 checkAuthentication();
		} catch (NotAuthenticatedException nae) {
			return Response.status(403).build();
		}
		
        return Response.ok(helloWorld, MediaType.APPLICATION_JSON).build();
    }
	
	/**
	 * checks the authentication of the current user
	 * 
	 * @throws NotAuthenticatedException
	 * 	if the authentication is not ok
	 */
	@SuppressWarnings("unchecked")
	private void checkAuthentication() throws NotAuthenticatedException {

		// don't allow anonymous access
		if (ContextInfo.isAnonymous()) {
			throw new NotAuthenticatedException();
		}

		Session session = null;
	    Name name = null;

	    try {
			// get the current user session
			session = ContextInfo.getUserSession();
			name = session.createName(session.getEffectiveUserName());
			String userName = name.getCanonical();
			
			// get all groups for a user
			DominoServer server = new DominoServer( session.getServerName() );
			Collection<String> nameList = server.getNamesList(userName);
			
			// check if the group is in the list
			if (nameList.contains( RESTAPI_GROUP ))
				return;

		} catch (NotesException e) {
			e.printStackTrace();
	    } finally {
			name = recycle(name);
		}
		
		throw new NotAuthenticatedException();
	}
	
	/**
	 * recycle the domino object
	 * @param obj
	 * 		the domino object to recyle
	 * @return
	 * 		null
	 */
	private <T> T recycle( Base obj ){
		try {
			obj.recycle();
		} catch (NotesException e) {
			// ignore it. 
		}
		return null;
	}
	
}
