package br.com.diogo.example.services;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Path;

import com.google.appengine.api.datastore.Entity;

import br.com.diogo.example.models.User;

@Path("/users")
public class UserManager {

	private static final Logger log = Logger.getLogger("UserManager");
	
	public static final String USER_KIND = "Users"; //type Users to be created on Datastore
	public static final String PROP_EMAIL = "email";
	public static final String PROP_PASSWORD = "password";
	public static final String PROP_GCM_REG_ID = "gcmRegId";
	public static final String PROP_LAST_LOGIN = "lastLogin";
	public static final String PROP_LAST_GCM_REGISTER = "lastGCMRegister";
	public static final String PROP_ROLE = "role";
	
	private void userToEntity(User user, Entity userEntity) {
		userEntity.setProperty(PROP_EMAIL, user.getEmail());
		userEntity.setProperty(PROP_PASSWORD, user.getPassword());
		userEntity.setProperty(PROP_GCM_REG_ID, user.getGcmRegId());
		userEntity.setProperty(PROP_LAST_LOGIN, user.getLastLogin());
		userEntity.setProperty(PROP_LAST_GCM_REGISTER, user.getLastGCMRegister());
		userEntity.setProperty(PROP_ROLE, user.getRole());
	}
	
	private User entityToUser(Entity userEntity) {
		
		User user = new User();
		
		user.setId(userEntity.getKey().getId());
		user.setEmail((String) userEntity.getProperty(PROP_EMAIL));
		user.setPassword((String) userEntity.getProperty(PROP_PASSWORD));
		user.setGcmRegId((String) userEntity.getProperty(PROP_GCM_REG_ID));
		user.setLastLogin((Date) userEntity.getProperty(PROP_LAST_LOGIN));
		user.setLastGCMRegister((Date) userEntity.getProperty(PROP_LAST_GCM_REGISTER));
		user.setRole((String) userEntity.getProperty(PROP_ROLE));
		
		return user;
	}
}
