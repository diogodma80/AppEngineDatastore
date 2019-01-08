package br.com.diogo.example.authentication;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.DatatypeConverter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import br.com.diogo.example.models.User;
import br.com.diogo.example.services.UserManager;

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

		// Authentication implemented with the method checkCredentalsAndRoles()
		// Gets the roles that the target method allows with the annotation RolesAllowed
		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
		Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAllowed.value()));

		// test if the credentials are correct and the user has the appropriate role
		if (checkCredentialsAndRoles(loginPassword[0], loginPassword[1], rolesSet, requestContext) == false) {
			// returns 401 response code if false
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

	private boolean checkCredentialsAndRoles(String username, String password, Set<String> roles,
			ContainerRequestContext requestContext) {
		boolean isUserAllowed = false;

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter emailFilter = new FilterPredicate(UserManager.PROP_EMAIL, FilterOperator.EQUAL, username);
		Query query = new Query(UserManager.USER_KIND).setFilter(emailFilter);
		Entity userEntity = datastore.prepare(query).asSingleEntity();

		if (userEntity != null) {
			if (password.equals(userEntity.getProperty(UserManager.PROP_PASSWORD))
					&& roles.contains(userEntity.getProperty(UserManager.PROP_ROLE))) {

				final User user = updateUserLogin(datastore, userEntity);

				requestContext.setSecurityContext(new SecurityContext() {

					@Override
					public boolean isUserInRole(String role) {
						return role.equals(user.getRole());
					}

					@Override
					public boolean isSecure() {
						return true;
					}

					@Override
					public Principal getUserPrincipal() {
						return user;
					}

					@Override
					public String getAuthenticationScheme() {
						return SecurityContext.BASIC_AUTH;
					}
				});
				isUserAllowed = true;
			}
		}

		return isUserAllowed;
	}

	private User updateUserLogin(DatastoreService datastore, Entity userEntity) {
		final User user = new User();
		
		boolean canUseCache = true;
		boolean saveOnCache = true;
		
		String email = (String) userEntity.getProperty(UserManager.PROP_EMAIL);
		
		Cache cache;
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
			
			if(cache.containsKey(email)) {
				Date lastLogin = (Date) cache.get(email);
				if(Calendar.getInstance().getTime().getTime() - lastLogin.getTime()) < 30000) {
					saveOnCache = false;
				}
			}
			if(saveOnCache == true) {
				cache.put(email,  (Date)Calendar.getInstance().getTime());
				canUseCache = false;
			}
		} catch (CacheException e) {
			canUseCache = false;
		}

		
		user.setEmail((String) userEntity.getProperty(UserManager.PROP_EMAIL));
		user.setGcmRegId((String) userEntity.getProperty(UserManager.PROP_GCM_REG_ID));
		user.setId(userEntity.getKey().getId());
		user.setLastGCMRegister((Date) userEntity.getProperty(UserManager.PROP_LAST_GCM_REGISTER));
		user.setLastLogin((Date) Calendar.getInstance().getTime());
		user.setPassword((String) userEntity.getProperty(UserManager.PROP_PASSWORD));
		user.setRole((String) userEntity.getProperty(UserManager.PROP_ROLE));

		userEntity.setProperty(UserManager.PROP_LAST_LOGIN, user.getLastLogin());

		datastore.put(userEntity);
		return user;
	}

}
