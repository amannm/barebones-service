package systems.cauldron.service.barebones.endpoint;

/**
 * Created by amann.malik on 6/2/2015.
 */

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CrossOriginResourceSharingFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Headers", "Origin, Authorization, Content-Type, Accept, X-Requested-With");
        headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS, HEAD, PATCH");
        headers.add("Access-Control-Max-Age", "86400");
    }

}