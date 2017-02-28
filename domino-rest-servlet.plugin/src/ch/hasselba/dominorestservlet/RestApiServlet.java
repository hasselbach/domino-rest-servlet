package ch.hasselba.dominorestservlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RestApiServlet {

	@GET
	@Path("/helloworld/")
	public Response getHelloWorld() {
		
		return Response.ok(new HelloWorld(), MediaType.APPLICATION_JSON).build();
	}

}
