package ch.hasselba.dominorestservlet;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.ibm.domino.osgi.core.context.ContextInfo;

import ch.hasselba.concurrent.NotesCallableUserTask;
import ch.hasselba.domino.GC;
import ch.hasselba.memcache.ValueHolder;
import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.notes.addins.DominoServer;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RestApiServlet {

	final static String CONTENT_TYPE = MediaType.APPLICATION_JSON + ";charset=UTF-8";
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
	 *             if the authentication is not ok
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
			DominoServer server = new DominoServer(session.getServerName());
			Collection<String> nameList = server.getNamesList(userName);

			// check if the group is in the list
			if (nameList.contains(RESTAPI_GROUP))
				return;

		} catch (NotesException e) {
			e.printStackTrace();
		} finally {
			name = GC.recycle(name);
		}

		throw new NotAuthenticatedException();
	}

	/**
	 * The Demo Endpoint
	 * returns the UNID of the first document in groups view
	 * using MemCached & MultiThreaded response
	 * 
	 */
	@GET
	@Path("/demo")
	@Produces(CONTENT_TYPE)
	public Response getDemo() {

		// get the current user name
		String hlp = null;
		try {
			hlp = ContextInfo.getUserSession().getEffectiveUserName();
		} catch (NotesException ne) {
			ne.printStackTrace();
		}

		// add the username into a "final" variable (otherwise it is not accessable in the Callable)
		final String userName = hlp;
	
		// use MemCached for 10 seconds
		ValueHolder<String> vh = new ValueHolder<String>("DEMO~" + userName, 10) {

			// todo if value is not in cache
			@Override
			public String loadValue() {
				// get the NotesThreadPool
				ServiceLocator locator = (ServiceLocator) ServiceFactory.getInstance().getServiceLocator();
				
				// add a callable to 
				Future<String> doc = locator.getNotesThreadPool()
						.doSomeNotesWorkReturnFuture(new NotesCallableUserTask<String>(userName) {
							
							@Override
							public String callNotes(Session session) throws Exception {

								Database db = null;
								View view = null;
								try {
									
									// get the local mail database
									db = session.getDatabase("", "names.nsf", false);
									
									if (db.isOpen() == false) {
										boolean success = db.open();
										if (!success) {
											throw new Exception("Unable to open Database 'names.nsf'.");
										}
									}

									// get the "groups" view
									view = db.getView("($Groups)");
									if (view == null) {
										throw new Exception("View '($Groups)' not found.");
									}

									// return the UNID of the first document
									return view.getFirstDocument().getUniversalID();

								} catch (NotesException ne) {
									this.setNotesException(ne);

								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									view = GC.recycle(view);
									db = GC.recycle(db);
								}
								return null;
							}
						});
				
				// execute the callable and return the result
				String result = null;
				try {
					result = doc.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				return result;
			}

		};
		
		// grab the result from the ValueHolder
		String result = vh.getValue();

		// Build the JSON response and send it back 
		ResponseBuilder builder = Response.ok(result, MediaType.APPLICATION_JSON).header(HttpHeaders.CONTENT_TYPE,
				CONTENT_TYPE);

		return builder.build();

	}


}
