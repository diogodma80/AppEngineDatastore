package br.com.diogo.example.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import br.com.diogo.example.models.Product;

@Path("/products")
public class ProductManager {

	private Product createProduct(int code) {
		Product product = new Product();
		product.setCode(code);
		product.setModel("Model " + code);
		product.setName("Name " + code);
		product.setPrice(10 * code);
		product.setProductId(Integer.toString(code));
		
		return product;
	}
	
	@GET
	@Produces("application/json")
	@Path("/{code}")
	public Product getProduct(@PathParam("code") int code) {
		return createProduct(code);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProducts() {
		List<Product> list = new ArrayList<Product>();
		for(int i = 0; i < 10; i++) {
			list.add(createProduct(i));
		}
		
		return list;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Product saveProduct(Product product) {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key productKey = KeyFactory.createKey("Products","productKey");
		Entity productEntity = new Entity("Products", productKey);
		productToEntity(product, productEntity);
		datastore.put(productEntity);
		product.setId(productEntity.getKey().getId());
		return product;
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{code}")
	public String deleteProduct(@PathParam("code") int code) {
		return "Product " + code + " deleted";
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{code}")
	public Product alterProduct(@PathParam("code") int code, Product product) {
		product.setName("Name " + code);
		return product;
	}
	
	private void productToEntity(Product product, Entity productEntity) {
		productEntity.setProperty("ProductId", product.getProductId());
		productEntity.setProperty("Name", product.getName());
		productEntity.setProperty("Code", product.getCode());
		productEntity.setProperty("Model", product.getModel());
		productEntity.setProperty("Price", product.getPrice());
	}
	
	private Product entityToProduct(Entity productEntity) {
		Product product = new Product();
		product.setId(productEntity.getKey().getId());
		product.setProductId((String)productEntity.getProperty("ProductId"));
		product.setName((String)productEntity.getProperty("Name"));
		product.setCode(Integer.parseInt(productEntity.getProperty("Code").toString()));
		product.setModel((String)productEntity.getProperty("Model"));
		product.setPrice(Float.parseFloat(productEntity.getProperty("Price").toString()));
		return product;
		
	}
}
