package br.com.diogo.example.models;

import java.io.Serializable;

public class Product implements Serializable{
	
	private static final long serialVersionUID = 8477645934817423876L;
	private String productId;
	private String name;
	private String model;
	private int code;
	private float price;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
}
