package br.com.diogo.example;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import br.com.diogo.example.services.UserManager;


public class InitServletContextClass implements ServletContextListener {
	
	private static final Logger log = Logger.getLogger("InitServletContextClass");

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("App AppEngineDatastore startup");
		
		initializeUserEntities();
		
	}

	private void initializeUserEntities() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter roleFilter = new FilterPredicate(UserManager.PROP_ROLE, FilterOperator.EQUAL, "ADMIN");
		Query query = new Query(UserManager.USER_KIND).setFilter(roleFilter);
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));
		if(entities.size() == 0) {
			log.info("No user was found. Creating the User type in Datastore");
			Key userKey = KeyFactory.createKey(UserManager.USER_KIND, "userkey");
			Entity userEntity = new Entity(UserManager.USER_KIND, userKey);
			userEntity.setProperty(UserManager.PROP_EMAIL, "adm@gmail.com");
			userEntity.setProperty(UserManager.PROP_PASSWORD, "Admin#7");
			userEntity.setProperty(UserManager.PROP_GCM_REG_ID, "");
			userEntity.setProperty(UserManager.PROP_LAST_LOGIN, Calendar.getInstance().getTime());
			userEntity.setProperty(UserManager.PROP_LAST_GCM_REGISTER, Calendar.getInstance().getTime());
			userEntity.setProperty(UserManager.PROP_ROLE, "ADMIN");
			datastore.put(userEntity);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}


}
