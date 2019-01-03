package br.com.diogo.example.authentication;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

public class AuthFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	// 401 response code
	private static final String ACCESS_UNAUTHORIZED = "You do not have permission to access this resource";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// using reflections to get the resouce method and find out the annotation on it
		Method method = resourceInfo.getResourceMethod();

		// checks if the annotation PermitAll is set on the resource method
		// getProduct and getProducts set with PermitAll annotation
		if (method.isAnnotationPresent(PermitAll.class)) {
			return;
		}

		String auth = requestContext.getHeaderString("Authorization");

		if (auth == null) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(ACCESS_UNAUTHORIZED).build());
			return;
		}

		String[] loginPassword = decode(auth);

		if (loginPassword == null || loginPassword.length != 2) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(ACCESS_UNAUTHORIZED).build());
			return;
		}

		//Authentication implemented with the method checkCredentalsAndRoles()
		//Gets the roles that the target method allows with the annotation RolesAllowed
		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
		Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAllowed.value()));
		
		//test if the credentials are correct and the user has the appropriate role
		if (checkCredentialsAndRoles(loginPassword[0], loginPassword[1], rolesSet) == false) {
			//returns 401 response code if false
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(ACCESS_UNAUTHORIZED).build());
			return;
		}
	}

	private String[] decode(String auth) {

		auth = auth.replaceFirst("[B|b]asic ", "");

		// extracts the string after the value "Basic", decoding on the Base64 format
		// returning a byte array
		byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);

		if (decodedBytes == null || decodedBytes.length == 0) {
			return null;
		}

		// if the decoded String is not null|0 an array containing two string is
		// returned: "user:password"
		return new String(decodedBytes).split(":", 2);
	}

	private boolean checkCredentialsAndRoles(String username, String password, Set<String> roles) {
		boolean isUserAllowed = false;
		if (username.equals("Admin") && password.equals("Admin")) {
			if (roles.contains("ADMIN")) {
				isUserAllowed = true;
			}
		}

		if (isUserAllowed == false) {
			if (username.equals("User") && password.equals("User")) {
				if(roles.contains("USER")) {
					isUserAllowed = true;
				}
			}
		}

		return isUserAllowed;
	}

}
